from __future__ import annotations
import base64
import io
import math
from pathlib import Path
import time
import cv2
import numpy as np
from PIL import Image, ImageOps
import torch
from .model import ALIASES, BIRD_INDICES, load_classifier, sha256


def crop_box(image, box, padding=.05):
    w, h = image.size
    if len(box) != 4 or not all(math.isfinite(float(v)) for v in box):
        raise ValueError("Non-finite or malformed bounding box")
    x1,y1,x2,y2 = map(float, box)
    if x2 <= x1 or y2 <= y1:
        raise ValueError("Degenerate detection")
    dx,dy = (x2-x1)*padding, (y2-y1)*padding
    bounds = [max(0,int(math.floor(x1-dx))), max(0,int(math.floor(y1-dy))),
              min(w,int(math.ceil(x2+dx))), min(h,int(math.ceil(y2+dy)))]
    if bounds[2] <= bounds[0] or bounds[3] <= bounds[1]:
        raise ValueError("Detection lies outside image")
    # OpenCV performs the actual crop and BGR -> RGB handoff.
    rgb = np.asarray(image)
    bgr = cv2.cvtColor(rgb, cv2.COLOR_RGB2BGR)
    crop = bgr[bounds[1]:bounds[3], bounds[0]:bounds[2]]
    return Image.fromarray(cv2.cvtColor(crop, cv2.COLOR_BGR2RGB)), bounds


def decide(probabilities, labels, mode, threshold, top_k=5):
    scores, indices = torch.topk(probabilities, min(top_k, len(labels)))
    index = int(indices[0])
    accepted = float(scores[0]) >= threshold and (mode != "imagenet_demo" or index in BIRD_INDICES)
    top = [{"label": labels[int(i)], "probability": float(s),
            "bird_category": (int(i) in BIRD_INDICES) if mode == "imagenet_demo" else True}
           for s,i in zip(scores,indices)]
    label = labels[index] if accepted else "unknown"
    return {"label": label, "display_name": ALIASES.get(label, label) if accepted else "未知鸟类/类别不支持",
            "classifier_probability": float(scores[0]), "accepted": accepted, "top_k": top}


class BirdPipeline:
    def __init__(self, detector, classifier, device="cpu", confidence=.25, species_threshold=.2, image_size=640):
        from ultralytics import YOLO
        if not Path(detector).is_file() or not Path(classifier).is_file():
            raise FileNotFoundError("Model files missing; run nature-birds fetch-models explicitly")
        if not 0 <= confidence <= 1 or not 0 <= species_threshold <= 1:
            raise ValueError("Thresholds must lie in [0,1]")
        self.detector = YOLO(str(detector), task="detect")
        if "v8" not in Path(detector).stem.lower() and "yolov8" not in str(self.detector.model.yaml.get("yaml_file", "")).lower():
            raise ValueError("This service requires a YOLOv8 detection checkpoint")
        self.bird_id = next((i for i,n in self.detector.names.items() if n.casefold() == "bird"), None)
        if self.bird_id is None:
            raise ValueError("Detector label map does not contain the bird category")
        self.model, self.transform, self.labels, self.mode = load_classifier(classifier, device)
        self.device, self.confidence, self.threshold, self.image_size = device, confidence, species_threshold, image_size
        self.metadata = {"detector": "YOLOv8", "detector_sha256": sha256(detector),
                         "classifier": "EfficientNetV2-S", "classifier_sha256": sha256(classifier),
                         "mode": self.mode, "original_project_weights": False,
                         "probability_calibrated": False,
                         "notice": "公开预训练模型流程演示；非原比赛模型，未进行目标鸟类数据集效果评测。" if self.mode == "imagenet_demo" else
                                   "自训练鸟类分类权重；类别和效果以该权重的独立测试报告为准。"}

    @torch.inference_mode()
    def predict(self, image):
        started = time.perf_counter()
        image = ImageOps.exif_transpose(image).convert("RGB")
        if image.width*image.height > 25_000_000 or min(image.size) < 16:
            raise ValueError("Image dimensions outside supported range")
        result = self.detector.predict(image, classes=[self.bird_id], conf=self.confidence,
            imgsz=self.image_size, max_det=20, device=self.device, verbose=False)[0]
        detections = []
        boxes = result.boxes
        if boxes is not None:
            for box, score in zip(boxes.xyxy.cpu().tolist(), boxes.conf.cpu().tolist()):
                crop, padded = crop_box(image, box)
                probabilities = self.model(self.transform(crop).unsqueeze(0).to(self.device)).softmax(1)[0].cpu()
                decision = decide(probabilities, self.labels, self.mode, self.threshold)
                detections.append({"box": [round(float(v),2) for v in box], "crop_box": padded,
                                   "detector_confidence": float(score), **decision})
        annotated = cv2.cvtColor(np.asarray(image), cv2.COLOR_RGB2BGR)
        counts = {}
        for i,d in enumerate(detections):
            counts[d["display_name"]] = counts.get(d["display_name"],0)+1
            x1,y1,x2,y2 = map(int,d["box"])
            cv2.rectangle(annotated,(x1,y1),(x2,y2),(36,150,50),max(2,image.width//400))
            cv2.putText(annotated, f'{i+1}: {d["label"]} {d["classifier_probability"]:.2f}',
                        (max(0,x1),max(20,y1-8)),cv2.FONT_HERSHEY_SIMPLEX,.65,(36,150,50),2)
        ok, encoded = cv2.imencode('.jpg',annotated,[cv2.IMWRITE_JPEG_QUALITY,85])
        if not ok:
            raise RuntimeError("Annotated image encoding failed")
        return {"status": "detected" if detections else "no_bird_detected", "detections": detections,
                "info": counts, "image": "data:image/jpeg;base64,"+base64.b64encode(encoded).decode("ascii"),
                "model": self.metadata, "image_size": list(image.size),
                "elapsed_seconds": time.perf_counter()-started}
