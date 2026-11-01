import io
import threading
from fastapi import FastAPI, File, HTTPException, UploadFile
from PIL import Image, UnidentifiedImageError
from .pipeline import open_image


def create_app(pipeline):
    app = FastAPI(title="Nature Atlas local YOLOv8 + EfficientNetV2", version="0.3.0")
    lock = threading.Lock()
    @app.get("/health")
    def health():
        return {"ready": True, "model": pipeline.metadata}
    @app.post("/predict")
    async def predict(file: UploadFile = File(...)):
        data = await file.read(8*1024*1024+1)
        if len(data)>8*1024*1024:
            raise HTTPException(413,"Image exceeds 8 MB")
        try:
            # Ultralytics may replace PIL.Image.open with a HEIF fallback that
            # raises ModuleNotFoundError on malformed uploads. Keep Pillow's
            # original decoder captured before importing the detector.
            with open_image(io.BytesIO(data)) as image:
                if image.width*image.height > 25_000_000:
                    raise ValueError("Image exceeds 25 megapixels")
                image.load()
                # Single CPU/GPU inference at a time; no mutable predictor races.
                with lock:
                    return pipeline.predict(image)
        except (UnidentifiedImageError, OSError, ValueError, Image.DecompressionBombError) as exc:
            raise HTTPException(400,"Invalid or unsupported image") from exc
    return app
