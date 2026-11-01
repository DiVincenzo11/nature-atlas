import json
from pathlib import Path
import random
import torch
from torch import nn
from torch.utils.data import DataLoader
from torchvision.models import efficientnet_v2_s, EfficientNet_V2_S_Weights
from .data import load_manifest, BirdDataset
from .model import transform, sha256, load_classifier


@torch.inference_mode()
def score(model, loader, device, nclasses):
    model.eval()
    matrix = torch.zeros(nclasses,nclasses,dtype=torch.long)
    loss,total = 0.,0
    for images, targets in loader:
        logits = model(images.to(device))
        loss += float(nn.functional.cross_entropy(logits,targets.to(device),reduction="sum"))
        pred = logits.argmax(1).cpu()
        for y,p in zip(targets,pred):
            matrix[int(y),int(p)] += 1
        total += len(targets)
    tp = matrix.diag().float()
    denom = matrix.sum(0)+matrix.sum(1)
    f1 = torch.where(denom>0,2*tp/denom,torch.zeros_like(tp))
    return {"images": total,"loss": loss/total,"accuracy": float(tp.sum()/total),
            "macro_f1": float(f1.mean()),"per_class_f1": f1.tolist(),"confusion_matrix": matrix.tolist()}


def train(manifest, out, epochs=10, batch=4, size=224, device="cpu", seed=42, pretrained=True, freeze_backbone=False):
    if epochs < 1 or batch < 1 or size < 32:
        raise ValueError("Invalid training budget")
    torch.manual_seed(seed); random.seed(seed)
    rows, labels = load_manifest(manifest)
    if len(labels)<2:
        raise ValueError("At least two species classes required")
    train_labels = {r["label"] for r in rows if r["split"] == "train"}
    if train_labels != set(labels):
        raise ValueError("All labels must have training examples")
    folder=Path(out);folder.mkdir(parents=True,exist_ok=False)
    train_loader=DataLoader(BirdDataset(rows,labels,"train",transform(size,True)),batch_size=batch,shuffle=True)
    val_loader=DataLoader(BirdDataset(rows,labels,"val",transform(size)),batch_size=batch)
    weights=EfficientNet_V2_S_Weights.IMAGENET1K_V1 if pretrained else None
    model=efficientnet_v2_s(weights=weights)
    if freeze_backbone:
        for parameter in model.features.parameters():parameter.requires_grad=False
    model.classifier[1]=nn.Linear(model.classifier[1].in_features,len(labels))
    model.to(device)
    optimizer=torch.optim.AdamW([p for p in model.parameters() if p.requires_grad],lr=1e-3,weight_decay=1e-4)
    best=-1.;history=[]
    for epoch in range(epochs):
        model.train()
        if freeze_backbone:model.features.eval()
        for images,targets in train_loader:
            optimizer.zero_grad(set_to_none=True)
            logits=model(images.to(device))
            loss=nn.functional.cross_entropy(logits,targets.to(device))
            loss.backward();optimizer.step()
        metrics=score(model,val_loader,device,len(labels))
        history.append({"epoch":epoch+1,"validation":metrics})
        if metrics["macro_f1"]>best:
            best=metrics["macro_f1"]
            torch.save({"architecture":"efficientnet_v2_s","state_dict":{k:v.cpu() for k,v in model.state_dict().items()},
                "labels":labels,"mode":"custom_species","image_size":size,"epoch":epoch+1,"seed":seed,
                "manifest_sha256":sha256(manifest),"selected_by":"validation_macro_f1","pretrained":pretrained,
                "freeze_backbone":freeze_backbone},folder/"best.pt")
        (folder/"history.json").write_text(json.dumps(history,indent=2)+"\n",encoding="utf-8")
        print(json.dumps(history[-1]),flush=True)
    return history


def evaluate(checkpoint,manifest,split,out,device="cpu",batch=4):
    rows,labels=load_manifest(manifest)
    model,preprocessing,model_labels,mode=load_classifier(checkpoint,device)
    if mode!="custom_species" or set(labels)!=set(model_labels):
        raise ValueError("Evaluation requires custom checkpoint and matching manifest labels")
    loader=DataLoader(BirdDataset(rows,model_labels,split,preprocessing),batch_size=batch)
    report={"split":split,"labels":model_labels,"checkpoint_sha256":sha256(checkpoint),
            "manifest_sha256":sha256(manifest),"evaluation_scope":"classifier_on_provided_crops; not end-to-end detection mAP",
            **score(model,loader,device,len(model_labels))}
    Path(out).write_text(json.dumps(report,indent=2)+"\n",encoding="utf-8")
    return report
