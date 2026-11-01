"""Synthetic, non-bird software diagnostic: train -> save -> load -> evaluate."""
import argparse
import json
from pathlib import Path
import random
from PIL import Image, ImageDraw
import torch
from nature_birds.train import train, evaluate


def main():
    p=argparse.ArgumentParser();p.add_argument("--out",type=Path,required=True);a=p.parse_args()
    a.out.mkdir(parents=True,exist_ok=False)
    torch.set_num_threads(2)
    rng=random.Random(42);rows=[]
    for label in ["synthetic_a","synthetic_b"]:
        for i,split in enumerate(["train","train","val","test"]):
            image=Image.new('RGB',(80,80),(rng.randrange(200),rng.randrange(200),rng.randrange(200)))
            draw=ImageDraw.Draw(image);draw.rectangle([i+5,12,50,62],outline='white',width=3)
            draw.text((3,3),'SYNTHETIC',fill='white')
            name=f'{label}_{i}.png';image.save(a.out/name)
            rows.append(dict(file=name,label=label,group=name,split=split))
    manifest=a.out/'manifest.jsonl';manifest.write_text('\n'.join(json.dumps(r) for r in rows)+'\n')
    train(manifest,a.out/'train',epochs=1,batch=2,size=64,pretrained=False,freeze_backbone=True)
    report=evaluate(a.out/'train/best.pt',manifest,'test',a.out/'test.json')
    report.update(synthetic=True,real_bird_accuracy=False,scope='one-epoch random-initialized frozen-backbone software diagnostic')
    (a.out/'DIAGNOSTIC.json').write_text(json.dumps(report,indent=2)+'\n')
    print(json.dumps({'trained_and_reloaded':True,'synthetic':True,'test_images':report['images']}))


if __name__=='__main__':main()
