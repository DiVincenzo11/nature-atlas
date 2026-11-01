import hashlib
import json
from pathlib import Path
from PIL import Image, ImageOps
from torch.utils.data import Dataset
from .pipeline import crop_box


def load_manifest(path):
    path = Path(path)
    rows = [json.loads(l) for l in path.read_text(encoding="utf-8").splitlines() if l.strip()]
    groups, hashes, labels = {}, {}, set()
    for r in rows:
        if r["split"] not in {"train","val","test"} or not r.get("group") or not r.get("label"):
            raise ValueError("Every image needs a label, source group and train/val/test split")
        p = (path.parent/r["file"]).resolve()
        if not p.is_relative_to(path.parent.resolve()):
            raise ValueError("Image paths must stay within the manifest directory")
        with Image.open(p) as image:
            rgb = ImageOps.exif_transpose(image).convert("RGB")
            digest = hashlib.sha256(str(rgb.size).encode()+rgb.tobytes()).hexdigest()
        for mapping,key in [(groups,r["group"]),(hashes,digest)]:
            old = mapping.setdefault(key, r["split"])
            if old != r["split"]:
                raise ValueError("Source group or identical pixels leak across splits")
        labels.add(r["label"])
        r["path"] = p
    if not rows:
        raise ValueError("Empty manifest")
    return rows, sorted(labels)


class BirdDataset(Dataset):
    def __init__(self, rows, labels, split, transform):
        self.rows = [r for r in rows if r["split"] == split]
        self.mapping = {label:i for i,label in enumerate(labels)}
        self.transform = transform
        if not self.rows:
            raise ValueError("Requested split is empty")
    def __len__(self):
        return len(self.rows)
    def __getitem__(self, index):
        row = self.rows[index]
        with Image.open(row["path"]) as raw:
            image = ImageOps.exif_transpose(raw).convert("RGB")
        if "box" in row:
            image,_ = crop_box(image, row["box"])
        return self.transform(image), self.mapping[row["label"]]
