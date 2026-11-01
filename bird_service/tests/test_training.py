import json
from pathlib import Path
import pytest
import torch
from torch import nn
from PIL import Image
from nature_birds.train import score,train
from nature_birds.model import export_classifier


def test_score_confusion_macro_f1_and_top5_have_independent_expected_values():
    logits=torch.tensor([[6.,5,4,3,2,1],[6.,5,4,3,2,1]])
    result=score(nn.Identity(),[(logits,torch.tensor([0,5]))],'cpu',6,collect=True)
    assert result['accuracy']==.5 and result['top5_accuracy']==.5
    assert result['macro_f1']==pytest.approx(1/9)
    assert result['confusion_matrix'][0][0]==1 and result['confusion_matrix'][5][0]==1


def test_portable_export_keeps_tensors_and_normalizes_legacy_version_metadata(tmp_path):
    from torch.torch_version import TorchVersion
    original={'architecture':'efficientnet_v2_s','mode':'custom_species','labels':['a','b'],
              'state_dict':{'weight':torch.arange(12).reshape(3,4)},'training_config':{'torch':TorchVersion('2.7.1+cu128')}}
    source=tmp_path/'raw.pt';target=tmp_path/'portable.pt';torch.save(original,source)
    result=export_classifier(source,target)
    loaded=torch.load(target,weights_only=True)
    assert type(loaded['training_config']['torch']) is str
    assert torch.equal(loaded['state_dict']['weight'],original['state_dict']['weight'])
    assert result['tensor_equality_verified']


@pytest.mark.parametrize('frozen,warmup,epochs',[(True,0,1),(False,1,2)])
def test_head_freezing_preserves_bn_and_warmup_releases_backbone(tmp_path,monkeypatch,frozen,warmup,epochs):
    instances=[]
    class Tiny(nn.Module):
        def __init__(self):
            super().__init__()
            self.features=nn.Sequential(nn.Conv2d(3,4,3,padding=1),nn.BatchNorm2d(4),nn.AdaptiveAvgPool2d(1))
            self.classifier=nn.Sequential(nn.Dropout(.1),nn.Linear(4,1000))
            self.original_weight=self.features[0].weight.detach().clone()
        def forward(self,x):return self.classifier(self.features(x).flatten(1))
    def factory(**kwargs):
        model=Tiny();instances.append(model);return model
    monkeypatch.setattr('nature_birds.train.efficientnet_v2_s',factory)
    rows=[]
    for i in range(6):
        name=f'{i}.png';Image.new('RGB',(40,40),(20+i*25,40,70)).save(tmp_path/name)
        rows.append({'file':name,'group':name,'label':'a' if i<3 else 'b','split':'val' if i in [2,5] else 'train'})
    manifest=tmp_path/'manifest.jsonl';manifest.write_text('\n'.join(json.dumps(r) for r in rows))
    history=train(manifest,tmp_path/'run',epochs=epochs,batch=2,size=32,pretrained=False,
                  freeze_backbone=frozen,warmup_head_epochs=warmup)
    model=instances[0]
    assert history[0]['backbone_frozen']
    assert (int(model.features[1].num_batches_tracked)==0)==frozen
    assert torch.equal(model.features[0].weight,model.original_weight)==frozen
    assert type(torch.load(tmp_path/'run/best.pt',weights_only=True)['training_config']['torch']) is str
