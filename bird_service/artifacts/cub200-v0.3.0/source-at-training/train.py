import csv
import json
from pathlib import Path
import random
import time
import numpy as np
import torch
from torch import nn
from torch.utils.data import DataLoader
from torchvision.models import efficientnet_v2_s, EfficientNet_V2_S_Weights
from .data import load_manifest, BirdDataset
from .model import transform, sha256, load_classifier


def seed_worker(_):
    seed=torch.initial_seed() % 2**32
    random.seed(seed);np.random.seed(seed)


def loader(dataset,batch,device,workers=0,shuffle=False,seed=42):
    return DataLoader(dataset,batch_size=batch,shuffle=shuffle,num_workers=workers,
                      pin_memory=torch.device(device).type=='cuda',persistent_workers=workers>0,
                      worker_init_fn=seed_worker,generator=torch.Generator().manual_seed(seed))


@torch.inference_mode()
def score(model,batches,device,nclasses,collect=False):
    model.eval();matrix=torch.zeros(nclasses,nclasses,dtype=torch.long)
    loss,total,top5=0.,0,0;predictions=[]
    for images,targets in batches:
        logits=model(images.to(device,non_blocking=True))
        loss+=float(nn.functional.cross_entropy(logits,targets.to(device),reduction='sum'))
        probabilities=logits.float().softmax(1).cpu()
        values,indices=probabilities.topk(min(5,nclasses),dim=1);pred=indices[:,0]
        matrix+=torch.bincount(targets*nclasses+pred,minlength=nclasses*nclasses).reshape(nclasses,nclasses)
        top5+=int((indices==targets[:,None]).any(1).sum());total+=len(targets)
        if collect:
            for y,p,confidence,top in zip(targets.tolist(),pred.tolist(),values[:,0].tolist(),indices.tolist()):
                predictions.append({'target':y,'prediction':p,'confidence':confidence,'top_indices':top})
    if not total:raise ValueError('Cannot evaluate an empty dataset')
    tp=matrix.diag().double();denom=matrix.sum(0)+matrix.sum(1)
    f1=torch.where(denom>0,2*tp/denom,torch.zeros_like(tp))
    report={'images':total,'loss':loss/total,'accuracy':float(tp.sum()/total),'top5_accuracy':top5/total,
            'macro_f1':float(f1.mean()),'per_class_f1':f1.tolist(),'confusion_matrix':matrix.tolist()}
    if collect:report['predictions']=predictions
    return report


def train(manifest,out,epochs=10,batch=4,size=224,device='cpu',seed=42,pretrained=True,freeze_backbone=False,
          lr=1e-3,backbone_lr=None,warmup_head_epochs=0,amp=False,num_workers=0,pretrained_checkpoint=None,
          scheduler='constant',experiment_metadata=None):
    if (epochs<1 or batch<1 or size<32 or lr<=0 or num_workers<0 or not 0<=warmup_head_epochs<=epochs
            or (backbone_lr is not None and backbone_lr<=0) or scheduler not in {'constant','cosine'}):
        raise ValueError('Invalid training configuration')
    if amp and torch.device(device).type!='cuda':raise ValueError('AMP requires CUDA in this trainer')
    if torch.device(device).type=='cuda' and not torch.cuda.is_available():raise RuntimeError('CUDA unavailable')
    torch.manual_seed(seed);random.seed(seed);np.random.seed(seed)
    rows,labels=load_manifest(manifest)
    if len(labels)<2:raise ValueError('At least two species classes required')
    if {r['label'] for r in rows if r['split']=='train'}!=set(labels):raise ValueError('All labels need training examples')
    folder=Path(out);folder.mkdir(parents=True,exist_ok=False)
    train_loader=loader(BirdDataset(rows,labels,'train',transform(size,True)),batch,device,num_workers,True,seed)
    val_loader=loader(BirdDataset(rows,labels,'val',transform(size)),batch,device,num_workers,False,seed)
    weights=EfficientNet_V2_S_Weights.IMAGENET1K_V1 if pretrained and not pretrained_checkpoint else None
    model=efficientnet_v2_s(weights=weights)
    if pretrained_checkpoint:
        if not pretrained:raise ValueError('A pretrained checkpoint conflicts with random initialization')
        initial=torch.load(pretrained_checkpoint,map_location='cpu',weights_only=True)
        if (initial.get('architecture')!='efficientnet_v2_s' or initial.get('mode')!='imagenet_demo'
                or initial.get('labels')!=EfficientNet_V2_S_Weights.IMAGENET1K_V1.meta['categories']):
            raise ValueError('Initialization must be the explicit ImageNet EfficientNetV2-S checkpoint')
        model.load_state_dict(initial['state_dict'],strict=True)
    model.classifier[1]=nn.Linear(model.classifier[1].in_features,len(labels));model.to(device)
    if freeze_backbone or warmup_head_epochs:
        for parameter in model.features.parameters():parameter.requires_grad=False
    groups=[{'params':model.classifier.parameters(),'lr':lr,'name':'classifier'}]
    if not freeze_backbone:groups.append({'params':model.features.parameters(),'lr':backbone_lr or lr,'name':'backbone'})
    optimizer=torch.optim.AdamW(groups,weight_decay=1e-4)
    schedule=torch.optim.lr_scheduler.CosineAnnealingLR(optimizer,T_max=epochs,eta_min=1e-6) if scheduler=='cosine' else None
    scaler=torch.amp.GradScaler('cuda',enabled=amp)
    config={'epochs':epochs,'batch':batch,'image_size':size,'device':device,'seed':seed,'pretrained':pretrained,
            'pretrained_checkpoint_sha256':sha256(pretrained_checkpoint) if pretrained_checkpoint else None,
            'freeze_backbone':freeze_backbone,'warmup_head_epochs':warmup_head_epochs,'lr':lr,
            'backbone_lr':backbone_lr or lr,'amp_training':amp,'validation_precision':'float32',
            'num_workers':num_workers,'scheduler':scheduler,'weight_decay':1e-4,
            'manifest_sha256':sha256(manifest),'train_images':len(train_loader.dataset),'validation_images':len(val_loader.dataset),
            'classes':len(labels),'torch':torch.__version__,'cuda':torch.version.cuda,
            'gpu':torch.cuda.get_device_name(device) if torch.device(device).type=='cuda' else None}
    (folder/'config.json').write_text(json.dumps(config,indent=2)+'\n',encoding='utf-8')
    best=-1.;history=[];started=time.perf_counter()
    for epoch in range(epochs):
        frozen=freeze_backbone or epoch<warmup_head_epochs
        for parameter in model.features.parameters():parameter.requires_grad=not frozen
        model.train()
        if frozen:model.features.eval()
        train_loss=0.;train_correct=0;seen=0;epoch_started=time.perf_counter()
        for images,targets in train_loader:
            images=images.to(device,non_blocking=True);targets=targets.to(device,non_blocking=True)
            optimizer.zero_grad(set_to_none=True)
            with torch.autocast(device_type=torch.device(device).type,dtype=torch.float16,enabled=amp):
                logits=model(images);loss=nn.functional.cross_entropy(logits,targets)
            scaler.scale(loss).backward();scaler.step(optimizer);scaler.update()
            train_loss+=float(loss.detach())*len(targets);train_correct+=int((logits.argmax(1)==targets).sum());seen+=len(targets)
        metrics=score(model,val_loader,device,len(labels))
        entry={'epoch':epoch+1,'backbone_frozen':frozen,'train_loss':train_loss/seen,'train_accuracy':train_correct/seen,
               'learning_rates':[g['lr'] for g in optimizer.param_groups],
               'validation':{k:v for k,v in metrics.items() if k not in {'confusion_matrix','per_class_f1'}},
               'epoch_seconds':time.perf_counter()-epoch_started}
        history.append(entry)
        if metrics['macro_f1']>best:
            best=metrics['macro_f1']
            (folder/'best-validation.json').write_text(json.dumps(metrics,indent=2)+'\n',encoding='utf-8')
            torch.save({'architecture':'efficientnet_v2_s','state_dict':{k:v.detach().cpu() for k,v in model.state_dict().items()},
                'labels':labels,'mode':'custom_species','image_size':size,'epoch':epoch+1,'seed':seed,
                'manifest_sha256':config['manifest_sha256'],'selected_by':'validation_macro_f1','pretrained':pretrained,
                'freeze_backbone':freeze_backbone,'training_config':config,'experiment_metadata':experiment_metadata or {},
                'validation_macro_f1':best},folder/'best.pt')
        if schedule:schedule.step()
        (folder/'history.json').write_text(json.dumps(history,indent=2)+'\n',encoding='utf-8')
        print(json.dumps({'epoch':epoch+1,'train_loss':entry['train_loss'],'validation_accuracy':metrics['accuracy'],
                          'validation_macro_f1':metrics['macro_f1'],'seconds':entry['epoch_seconds'],'backbone_frozen':frozen}),flush=True)
    (folder/'completion.json').write_text(json.dumps({'epochs_completed':len(history),'total_seconds':time.perf_counter()-started,
                'best_validation_macro_f1':best,'checkpoint_sha256':sha256(folder/'best.pt')},indent=2)+'\n',encoding='utf-8')
    return history


def evaluate(checkpoint,manifest,split,out,device='cpu',batch=4,num_workers=0):
    rows,labels=load_manifest(manifest)
    model,preprocessing,model_labels,mode=load_classifier(checkpoint,device)
    if mode!='custom_species' or set(labels)!=set(model_labels):raise ValueError('Custom checkpoint and matching labels required')
    dataset=BirdDataset(rows,model_labels,split,preprocessing)
    report=score(model,loader(dataset,batch,device,num_workers),device,len(model_labels),collect=True)
    predictions=report.pop('predictions');target=Path(out);target.parent.mkdir(parents=True,exist_ok=True)
    if target.exists():raise FileExistsError('Refusing to overwrite an evaluation report')
    per_image=target.with_suffix('.per_image.csv')
    with per_image.open('x',newline='',encoding='utf-8') as stream:
        fields=['image_id','file','target','prediction','confidence','correct','top5_labels']
        writer=csv.DictWriter(stream,fieldnames=fields);writer.writeheader()
        for row,pred in zip(dataset.rows,predictions):
            writer.writerow({'image_id':row.get('image_id',row['group']),'file':row['file'],'target':model_labels[pred['target']],
                'prediction':model_labels[pred['prediction']],'confidence':pred['confidence'],'correct':pred['target']==pred['prediction'],
                'top5_labels':'|'.join(model_labels[i] for i in pred['top_indices'])})
    report.update(split=split,labels=model_labels,checkpoint_sha256=sha256(checkpoint),manifest_sha256=sha256(manifest),
        evaluation_scope='classifier_on_provided_crops; not end-to-end detection mAP',per_image_file=per_image.name)
    target.write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
    return report
