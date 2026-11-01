from __future__ import annotations
from pathlib import Path
import hashlib
import torch
from torch import nn
from torchvision.models import efficientnet_v2_s, EfficientNet_V2_S_Weights
from torchvision import transforms as T
from torchvision.transforms import InterpolationMode

# Canonical ImageNet-1K bird categories, not a comprehensive bird species taxonomy.
BIRD_INDICES = frozenset(list(range(7, 25))+list(range(80, 101))+list(range(127, 147)))
ALIASES = {"bald eagle": "白头海雕", "king penguin": "王企鹅", "black swan": "黑天鹅",
           "peacock": "孔雀（ImageNet类别）", "magpie": "喜鹊（ImageNet类别）",
           "ostrich": "鸵鸟（ImageNet类别）", "great grey owl": "乌林鸮", "flamingo": "红鹳（ImageNet类别）"}


def sha256(path):
    h = hashlib.sha256()
    with Path(path).open("rb") as f:
        for data in iter(lambda: f.read(1024*1024), b""):
            h.update(data)
    return h.hexdigest()


def export_classifier(source,out):
    """Normalize legacy TorchVersion metadata without changing trained tensors."""
    from torch.torch_version import TorchVersion
    out=Path(out)
    if out.exists():raise FileExistsError('Refusing to overwrite an exported checkpoint')
    with torch.serialization.safe_globals([TorchVersion]):
        original=torch.load(source,map_location='cpu',weights_only=True)
    if original.get('architecture')!='efficientnet_v2_s' or original.get('mode')!='custom_species':
        raise ValueError('Expected an explicit custom-species classifier checkpoint')
    def plain(value):
        if isinstance(value,TorchVersion):return str(value)
        if isinstance(value,dict):return {k:plain(v) for k,v in value.items()}
        if isinstance(value,list):return [plain(v) for v in value]
        if isinstance(value,tuple):return tuple(plain(v) for v in value)
        return value
    result=plain(original)
    result['portable_export']={'source_sha256':sha256(source),'trained_tensors_changed':False}
    out.parent.mkdir(parents=True,exist_ok=True);torch.save(result,out)
    loaded=torch.load(out,map_location='cpu',weights_only=True)
    if set(loaded['state_dict'])!=set(original['state_dict']):raise RuntimeError('Export changed model keys')
    if not all(torch.equal(v,loaded['state_dict'][k]) for k,v in original['state_dict'].items()):
        raise RuntimeError('Export changed a trained tensor')
    return {'file':out.name,'sha256':sha256(out),'source_sha256':sha256(source),'tensor_equality_verified':True}


def transform(size=224, train=False):
    resize = [T.RandomResizedCrop(size, scale=(0.8, 1.0)), T.RandomHorizontalFlip()] if train else [
        T.Resize(size, interpolation=InterpolationMode.BILINEAR), T.CenterCrop(size)]
    return T.Compose(resize+[T.ToTensor(), T.Normalize([.485,.456,.406], [.229,.224,.225])])


def load_classifier(path, device):
    checkpoint = torch.load(path, map_location="cpu", weights_only=True)
    if checkpoint.get("architecture") != "efficientnet_v2_s":
        raise ValueError("Only explicit EfficientNetV2-S checkpoints are supported")
    labels = checkpoint["labels"]
    if len(labels) < 2 or len(set(labels)) != len(labels):
        raise ValueError("Checkpoint label map must contain distinct classes")
    model = efficientnet_v2_s(weights=None)
    model.classifier[1] = nn.Linear(model.classifier[1].in_features, len(labels))
    model.load_state_dict(checkpoint["state_dict"], strict=True)
    mode = checkpoint.get("mode", "custom_species")
    if mode == "imagenet_demo":
        if labels != EfficientNet_V2_S_Weights.IMAGENET1K_V1.meta["categories"]:
            raise ValueError("ImageNet label map mismatch")
        preprocessing = EfficientNet_V2_S_Weights.IMAGENET1K_V1.transforms()
    elif mode == "custom_species":
        preprocessing = transform(checkpoint["image_size"])
    else:
        raise ValueError("Unknown classifier mode")
    model.experiment_metadata=checkpoint.get("experiment_metadata",{})
    return model.to(device).eval(), preprocessing, labels, mode


def prepare_models(folder):
    """Explicit download command; startup never silently initializes random weights."""
    import json
    from urllib.request import urlretrieve
    folder = Path(folder)
    folder.mkdir(parents=True, exist_ok=True)
    detector = folder/"yolov8n.pt"
    if not detector.exists():
        temp = folder/"yolov8n.download"
        urlretrieve("https://github.com/ultralytics/assets/releases/download/v8.3.0/yolov8n.pt", temp)
        temp.replace(detector)
    if sha256(detector) != "f59b3d833e2ff32e194b5bb8e08d211dc7c5bdf144b90d2c8412c47ccfc83b36":
        raise ValueError("Downloaded YOLOv8n checksum differs from the verified upstream checkpoint")
    classifier = folder/"efficientnet_v2_s_imagenet.pt"
    if not classifier.exists():
        weights = EfficientNet_V2_S_Weights.IMAGENET1K_V1
        model = efficientnet_v2_s(weights=weights)
        torch.save({"architecture": "efficientnet_v2_s", "state_dict": model.state_dict(),
                    "labels": weights.meta["categories"], "mode": "imagenet_demo", "image_size": 384}, classifier)
    metadata = {"detector": {"file": detector.name, "sha256": sha256(detector), "training": "upstream COCO"},
                "classifier": {"file": classifier.name, "sha256": sha256(classifier), "training": "upstream ImageNet-1K"},
                "original_project_weights": False, "custom_species_training": False}
    (folder/"models.json").write_text(json.dumps(metadata, indent=2)+"\n", encoding="utf-8")
    return metadata
