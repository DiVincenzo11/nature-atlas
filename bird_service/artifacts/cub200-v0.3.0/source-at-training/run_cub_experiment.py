"""Fixed-budget CUB experiment. Test data is evaluated only after training both models."""
import argparse,json,os,time
from pathlib import Path
os.environ.setdefault('CUBLAS_WORKSPACE_CONFIG',':4096:8')
import torch
from nature_birds.train import train,evaluate
from nature_birds.model import sha256


def main():
    p=argparse.ArgumentParser();p.add_argument('--dataset-root',type=Path,required=True)
    p.add_argument('--init-imagenet',type=Path,required=True);p.add_argument('--out',type=Path,required=True)
    p.add_argument('--epochs',type=int,default=15);p.add_argument('--batch',type=int,default=32)
    p.add_argument('--workers',type=int,default=4);p.add_argument('--device',default='cuda:0');p.add_argument('--seed',type=int,default=42)
    a=p.parse_args();torch.set_num_threads(4);torch.set_num_interop_threads(1)
    torch.backends.cudnn.benchmark=False;torch.backends.cudnn.deterministic=True
    manifest=a.dataset_root/'manifest.jsonl'
    audit=json.loads((a.dataset_root/'split-audit.json').read_text(encoding='utf-8'))
    if not audit['official_test_unchanged'] or audit['classes']!=200:raise ValueError('Expected the prepared complete CUB protocol')
    a.out.mkdir(parents=True,exist_ok=False)
    protocol={'dataset':'CUB-200-2011','classes':200,'counts':audit['counts'],'seed':a.seed,'epochs_each':a.epochs,
        'batch':a.batch,'size':224,'workers':a.workers,'device':a.device,'amp_training':True,
        'classifier_lr':1e-3,'backbone_lr':5e-5,'head_warmup_epochs':3,'scheduler':'cosine','weight_decay':1e-4,
        'initialization_sha256':sha256(a.init_imagenet),'manifest_sha256':sha256(manifest),
        'selection':'validation macro-F1; official test untouched during training/selection',
        'comparison':'equal epoch budgets; frozen-head vs staged full fine-tuning; compute budgets differ',
        'pretraining_warning':audit['pretraining_warning'],'original_competition_model_recovered':False}
    (a.out/'protocol.json').write_text(json.dumps(protocol,indent=2)+'\n',encoding='utf-8')
    metadata={'dataset':'CUB-200-2011','dataset_homepage':'https://www.vision.caltech.edu/datasets/cub_200_2011/',
              'label_display_names':audit['label_display_names'],'pretraining_warning':audit['pretraining_warning']}
    for name,frozen,warmup in [('finetuned',False,3),('frozen_head',True,0)]:
        print(json.dumps({'stage':'training','model':name,'start_epoch':time.time()}),flush=True)
        train(manifest,a.out/name,epochs=a.epochs,batch=a.batch,size=224,device=a.device,seed=a.seed,
              pretrained=True,freeze_backbone=frozen,lr=1e-3,backbone_lr=5e-5,warmup_head_epochs=warmup,
              amp=True,num_workers=a.workers,pretrained_checkpoint=a.init_imagenet,scheduler='cosine',experiment_metadata=metadata)
        if torch.cuda.is_available():torch.cuda.empty_cache()
    # No checkpoint or training setting is selected using these test reports.
    reports={}
    for name in ['finetuned','frozen_head']:
        reports[name]=evaluate(a.out/name/'best.pt',manifest,'test',a.out/name/'test.json',a.device,a.batch,a.workers)
        print(json.dumps({'stage':'test','model':name,'accuracy':reports[name]['accuracy'],'macro_f1':reports[name]['macro_f1']}),flush=True)
        if torch.cuda.is_available():torch.cuda.empty_cache()
    summary={'protocol':protocol,'results':{name:{k:v for k,v in report.items() if k not in {'confusion_matrix','per_class_f1','labels'}} for name,report in reports.items()}}
    (a.out/'experiment.json').write_text(json.dumps(summary,indent=2)+'\n',encoding='utf-8')
    print(json.dumps(summary),flush=True)


if __name__=='__main__':main()
