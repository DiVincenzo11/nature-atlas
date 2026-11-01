"""Preserve the official CUB test set; stratify validation from official training."""
import argparse,collections,hashlib,json,random
from pathlib import Path
from PIL import Image,ImageOps


def read_pairs(path):
    return {int(key):value for key,value in (line.split(maxsplit=1) for line in path.read_text().splitlines())}


def prepare(root,validation_per_class=6,seed=42):
    root=Path(root).resolve();source=root/'CUB_200_2011'
    paths=read_pairs(source/'images.txt');classes=read_pairs(source/'classes.txt')
    labels={i:int(v) for i,v in read_pairs(source/'image_class_labels.txt').items()}
    official={i:int(v) for i,v in read_pairs(source/'train_test_split.txt').items()}
    boxes={i:[float(x) for x in v.split()] for i,v in read_pairs(source/'bounding_boxes.txt').items()}
    if set(paths)!=set(labels) or set(paths)!=set(official) or set(paths)!=set(boxes):raise ValueError('Incomplete official annotations')
    if len(classes)!=200 or len(paths)!=11788:raise ValueError('Expected the complete CUB-200-2011 release')
    validation=set()
    for category in sorted(classes):
        ids=sorted(i for i in paths if labels[i]==category and official[i]==1)
        if not 0<validation_per_class<len(ids):raise ValueError('Invalid per-class validation size')
        random.Random(seed+category).shuffle(ids);validation.update(ids[:validation_per_class])
    rows=[];audit=[];pixel_splits={};counts=collections.Counter();per_class={};clipped_boxes=[]
    for i,name in sorted(paths.items()):
        p=(source/'images'/name).resolve()
        if not p.is_relative_to(source/'images'):raise ValueError('Unexpected image path')
        split='test' if official[i]==0 else 'val' if i in validation else 'train'
        with Image.open(p) as image:
            if image.getexif().get(274,1) not in {0,1}:raise ValueError('Unexpected rotated image; transform annotations before use')
            rgb=ImageOps.exif_transpose(image).convert('RGB')
            digest=hashlib.sha256(str(rgb.size).encode()+rgb.tobytes()).hexdigest()
            w,h=rgb.size
        old=pixel_splits.setdefault(digest,(split,i))
        if old[0]!=split:raise ValueError(f'Identical-pixel cross-split overlap: {old[1]} and {i}; review before training')
        x,y,bw,bh=boxes[i]
        if bw<=0 or bh<=0:raise ValueError('Invalid official bounding box')
        clipped=[max(0,x),max(0,y),min(w,x+bw),min(h,y+bh)]
        if clipped[2]<=clipped[0] or clipped[3]<=clipped[1]:raise ValueError('Bounding box lies outside image')
        if clipped!=[x,y,x+bw,y+bh]:clipped_boxes.append({'image_id':i,'original_xyxy':[x,y,x+bw,y+bh],'clipped_xyxy':clipped})
        label=classes[labels[i]]
        row={'file':str(p.relative_to(root)).replace('\\','/'),'label':label,'group':f'CUB2011:{i}',
             'split':split,'image_id':i,'official_training_image':bool(official[i]),'box':clipped}
        rows.append(row);counts[split]+=1
        per_class.setdefault(label,collections.Counter())[split]+=1
        audit.append({'image_id':i,'file':row['file'],'split':split,'label':label,'pixel_sha256':digest,
                      'file_sha256':hashlib.sha256(p.read_bytes()).hexdigest()})
    manifest=root/'manifest.jsonl'
    if manifest.exists():raise FileExistsError('Refusing to overwrite a prepared manifest')
    manifest.write_text(''.join(json.dumps(r)+'\n' for r in rows),encoding='utf-8')
    report={'dataset':'CUB-200-2011','classes':len(classes),'images':len(rows),'counts':dict(counts),'per_class_counts':per_class,
        'split_seed':seed,'validation_per_class':validation_per_class,'official_test_unchanged':True,
        'exact_pixel_cross_split_duplicates':0,'group_scope':'one official source image ID; not individual/photographer-disjoint',
        'pretraining_warning':'CUB official source warns of possible overlap with ImageNet pretraining images.',
        'manifest_sha256':hashlib.sha256(manifest.read_bytes()).hexdigest(),
        'clipped_boxes':clipped_boxes,
        'label_display_names':{name:name.split('.',1)[1].replace('_',' ') for name in classes.values()}}
    (root/'split-audit.json').write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
    (root/'image-hashes.json').write_text(json.dumps(audit,indent=2)+'\n',encoding='utf-8')
    print(json.dumps({k:v for k,v in report.items() if k not in {'per_class_counts','label_display_names'}},indent=2))


if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('--root',type=Path,required=True)
    p.add_argument('--validation-per-class',type=int,default=6);p.add_argument('--seed',type=int,default=42)
    a=p.parse_args();prepare(a.root,a.validation_per_class,a.seed)
