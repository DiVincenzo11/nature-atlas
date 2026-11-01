import io
import json
from pathlib import Path
import pytest
import torch
from PIL import Image
from fastapi.testclient import TestClient


def test_malformed_upload_remains_http400_after_detector_patches_pillow(monkeypatch):
    from nature_birds.api import create_app
    def patched_open(*args,**kwargs):
        raise ModuleNotFoundError('optional HEIF decoder missing')
    monkeypatch.setattr(Image,'open',patched_open)
    class Pipeline:
        metadata={}
    client=TestClient(create_app(Pipeline()))
    response=client.post('/predict',files={'file':('bad.jpg',b'not an image','image/jpeg')})
    assert response.status_code==400
from nature_birds.pipeline import crop_box, decide
from nature_birds.api import create_app
from nature_birds.data import load_manifest


def test_crop_clips_and_pads_at_image_boundary():
    crop,box=crop_box(Image.new("RGB",(100,80)),[-5,3,90,90])
    assert box==[0,0,95,80] and crop.size==(95,80)


@pytest.mark.parametrize("box",[[1,1,1,3],[float('nan'),0,2,3],[200,100,210,110]])
def test_crop_rejects_invalid_boxes(box):
    with pytest.raises(ValueError):crop_box(Image.new("RGB",(100,80)),box)


def test_imagenet_nonbird_is_unknown_without_renormalizing():
    labels=[str(i) for i in range(1000)]
    scores=torch.zeros(1000);scores[0]=.8;scores[22]=.2
    out=decide(scores,labels,"imagenet_demo",.1)
    assert not out["accepted"] and out["label"]=="unknown"
    assert out["top_k"][1]["probability"]==pytest.approx(.2)


def test_low_confidence_and_custom_species_decision():
    out=decide(torch.tensor([.1,.9]),["species_a","species_b"],"custom_species",.95)
    assert not out["accepted"]
    out=decide(torch.tensor([.1,.9]),["species_a","species_b"],"custom_species",.2)
    assert out["label"]=="species_b"


def test_manifest_prevents_pixel_leak_and_source_group_leak(tmp_path):
    Image.new("RGB",(20,20),"blue").save(tmp_path/"a.png")
    Image.new("RGB",(20,20),"blue").save(tmp_path/"b.png")
    rows=[dict(file="a.png",label="a",group="a",split="train"),dict(file="b.png",label="a",group="b",split="val")]
    path=tmp_path/"manifest.jsonl"
    path.write_text('\n'.join(json.dumps(r) for r in rows))
    with pytest.raises(ValueError,match="leak"):load_manifest(path)
    Image.new("RGB",(20,20),"red").save(tmp_path/"b.png")
    rows[1]["group"]="a"
    path.write_text('\n'.join(json.dumps(r) for r in rows))
    with pytest.raises(ValueError,match="leak"):load_manifest(path)


class Pipeline:
    metadata={"mode":"test_double","original_project_weights":False}
    def predict(self,image):
        return {"status":"no_bird_detected","detections":[],"info":{},"image":"","model":self.metadata}


def test_api_decodes_images_and_handles_no_bird_without_fabrication():
    client=TestClient(create_app(Pipeline()))
    raw=io.BytesIO();Image.new("RGB",(30,30)).save(raw,"PNG")
    out=client.post('/predict',files={'file':('x.png',raw.getvalue(),'image/png')})
    assert out.status_code==200 and out.json()["detections"]==[]
    assert client.post('/predict',files={'file':('bad.png',b'not an image','image/png')}).status_code==400
    assert client.get('/health').json()["ready"]
