import argparse
import json
from pathlib import Path
from PIL import Image
import torch


def main():
    p=argparse.ArgumentParser(description="YOLOv8 + EfficientNetV2 bird pipeline")
    p.add_argument("--threads",type=int,default=2)
    sub=p.add_subparsers(dest="command",required=True)
    f=sub.add_parser("fetch-models");f.add_argument("--out",type=Path,default=Path("models"))
    for name in ["predict","serve"]:
        a=sub.add_parser(name)
        a.add_argument("--detector",type=Path,default=Path("models/yolov8n.pt"))
        a.add_argument("--classifier",type=Path,default=Path("models/efficientnet_v2_s_imagenet.pt"))
        a.add_argument("--device",default="cpu")
        a.add_argument("--confidence",type=float,default=.25)
        a.add_argument("--species-threshold",type=float,default=.2)
        if name=="predict":
            a.add_argument("--image",type=Path,required=True);a.add_argument("--out",type=Path,required=True)
        else:
            a.add_argument("--port",type=int,default=18902)
    a=sub.add_parser("train");a.add_argument("--manifest",type=Path,required=True);a.add_argument("--out",type=Path,required=True)
    a.add_argument("--epochs",type=int,default=10);a.add_argument("--batch",type=int,default=4)
    a.add_argument("--size",type=int,default=224);a.add_argument("--device",default="cpu")
    a.add_argument("--seed",type=int,default=42);a.add_argument("--random-init",action="store_true");a.add_argument("--freeze-backbone",action="store_true")
    a=sub.add_parser("evaluate");a.add_argument("--manifest",type=Path,required=True);a.add_argument("--checkpoint",type=Path,required=True)
    a.add_argument("--out",type=Path,required=True);a.add_argument("--split",choices=["val","test"],default="test");a.add_argument("--device",default="cpu")
    a=p.parse_args();torch.set_num_threads(a.threads)
    if a.command=="fetch-models":
        from .model import prepare_models
        result=prepare_models(a.out)
    elif a.command=="train":
        from .train import train
        result=train(a.manifest,a.out,a.epochs,a.batch,a.size,a.device,a.seed,not a.random_init,a.freeze_backbone)
    elif a.command=="evaluate":
        from .train import evaluate
        result=evaluate(a.checkpoint,a.manifest,a.split,a.out,a.device)
    else:
        from .pipeline import BirdPipeline
        pipeline=BirdPipeline(a.detector,a.classifier,a.device,a.confidence,a.species_threshold)
        if a.command=="serve":
            import uvicorn
            from .api import create_app
            uvicorn.run(create_app(pipeline),host="127.0.0.1",port=a.port)
            return
        import base64
        with Image.open(a.image) as image:result=pipeline.predict(image)
        a.out.mkdir(parents=True,exist_ok=False)
        (a.out/"annotated.jpg").write_bytes(base64.b64decode(result.pop("image").split(",",1)[1]))
        (a.out/"result.json").write_text(json.dumps(result,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")
    print(json.dumps(result,ensure_ascii=False,indent=2))


if __name__=="__main__":main()
