"""Evaluate selected CUB checkpoints, then the deployed two-stage pipeline.

No training/threshold tuning is performed here. Reports contain IDs and scores,
not copies of CUB photographs. Keep the official archive outside the repository.
"""
import argparse
import csv
import json
import time
from pathlib import Path
from PIL import Image
import torch
from nature_birds.data import load_manifest
from nature_birds.model import export_classifier, sha256
from nature_birds.pipeline import BirdPipeline
from nature_birds.train import evaluate


def iou(a, b):
    area = lambda r: max(0, r[2]-r[0])*max(0, r[3]-r[1])
    intersection = area([max(a[0],b[0]),max(a[1],b[1]),min(a[2],b[2]),min(a[3],b[3])])
    union = area(a)+area(b)-intersection
    return intersection/union if union > 0 else 0.


def evaluate_pipeline(manifest, detector, classifier, out, device):
    out=Path(out)
    if out.exists():raise FileExistsError(out)
    rows,_=load_manifest(manifest)
    rows=[r for r in rows if r['split']=='test']
    pipeline=BirdPipeline(detector,classifier,device=device)
    counters=dict(images=len(rows),any_detection=0,any_iou50=0,primary_iou50=0,
                  primary_species_correct=0,primary_accepted=0,
                  localized_species_correct=0,localized_accepted=0,
                  localized_accepted_correct=0)
    records=[];started=time.perf_counter()
    for index,row in enumerate(rows):
        with Image.open(row['path']) as image:prediction=pipeline.predict(image)
        detections=prediction['detections']
        overlaps=[iou(d['box'],row['box']) for d in detections]
        # Primary = highest detector confidence, regardless of ground truth.
        primary=max(detections,key=lambda d:d['detector_confidence']) if detections else None
        matched=primary is not None and iou(primary['box'],row['box'])>=.5
        correct=primary is not None and primary['top_k'][0]['label']==row['label']
        accepted=primary is not None and primary['accepted']
        counters['any_detection']+=bool(detections)
        counters['any_iou50']+=any(v>=.5 for v in overlaps)
        counters['primary_iou50']+=matched
        counters['primary_species_correct']+=bool(correct)
        counters['primary_accepted']+=bool(accepted)
        counters['localized_species_correct']+=bool(matched and correct)
        counters['localized_accepted']+=bool(matched and accepted)
        counters['localized_accepted_correct']+=bool(matched and accepted and correct)
        records.append(dict(image_id=row.get('image_id',row['group']),target=row['label'],
            detections=len(detections),max_iou=max(overlaps,default=0.),
            primary_iou=iou(primary['box'],row['box']) if primary else 0.,
            primary_prediction=primary['top_k'][0]['label'] if primary else '',
            primary_probability=primary['classifier_probability'] if primary else 0.,
            primary_accepted=bool(accepted),localized_accepted_correct=bool(matched and accepted and correct)))
        if (index+1)%500==0:print(json.dumps({'pipeline_images':index+1,'seconds':time.perf_counter()-started}),flush=True)
    n=len(rows);matched=counters['primary_iou50']
    report={'counts':counters,'rates':{
        'any_box_subject_recall_iou50':counters['any_iou50']/n,
        'primary_box_subject_recall_iou50':matched/n,
        'primary_species_correct_over_all_images':counters['primary_species_correct']/n,
        'species_accuracy_given_primary_iou50':counters['localized_species_correct']/matched if matched else 0.,
        'localized_accepted_correct_over_all_images':counters['localized_accepted_correct']/n,
        'accepted_primary_coverage':counters['primary_accepted']/n},
        'scope':'Single CUB annotated subject; primary box selected by detector confidence; not detection mAP.',
        'limitations':'Other birds may be unannotated. Thresholds are fixed defaults, not calibrated open-set recognition. ImageNet pretraining may overlap CUB test images.',
        'detector_confidence':pipeline.confidence,'species_threshold':pipeline.threshold,
        'iou_threshold':.5,'detector_input':pipeline.image_size,'crop_padding':.05,
        'model':pipeline.metadata,'manifest_sha256':sha256(manifest),
        'elapsed_seconds':time.perf_counter()-started,'device':device}
    out.parent.mkdir(parents=True,exist_ok=True)
    with out.with_suffix('.per_image.csv').open('x',newline='',encoding='utf-8') as stream:
        writer=csv.DictWriter(stream,fieldnames=list(records[0]));writer.writeheader();writer.writerows(records)
    out.write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
    return report


def main():
    p=argparse.ArgumentParser();p.add_argument('--dataset-root',type=Path,required=True)
    p.add_argument('--experiment',type=Path,required=True);p.add_argument('--detector',type=Path,required=True)
    p.add_argument('--out',type=Path,required=True);p.add_argument('--device',default='cuda:0')
    p.add_argument('--batch',type=int,default=32);p.add_argument('--workers',type=int,default=4)
    a=p.parse_args();torch.set_num_threads(4);torch.set_num_interop_threads(1)
    a.out.mkdir(parents=True,exist_ok=False);manifest=a.dataset_root/'manifest.jsonl'
    reports={};exports={}
    for name in ['finetuned','frozen_head']:
        checkpoint=a.out/f'cub200-{name}-efficientnetv2-s.pt'
        exports[name]=export_classifier(a.experiment/name/'best.pt',checkpoint)
        reports[name]=evaluate(checkpoint,manifest,'test',a.out/f'{name}-test.json',a.device,a.batch,a.workers)
        print(json.dumps({'stage':'test','model':name,**{k:reports[name][k] for k in ['accuracy','macro_f1','top5_accuracy']}}),flush=True)
    reports['pipeline']=evaluate_pipeline(manifest,a.detector,a.out/'cub200-finetuned-efficientnetv2-s.pt',a.out/'pipeline-test.json',a.device)
    summary={'exports':exports,'results':{k:{key:value for key,value in v.items() if key not in {'confusion_matrix','per_class_f1','labels'}} for k,v in reports.items()}}
    (a.out/'summary.json').write_text(json.dumps(summary,indent=2)+'\n',encoding='utf-8')
    print(json.dumps(summary),flush=True)


if __name__=='__main__':main()
