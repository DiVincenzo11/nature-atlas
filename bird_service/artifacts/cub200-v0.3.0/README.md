# CUB-200 experiment evidence

No CUB photographs are redistributed here. Download the official archive with the provided script before using the manifest.

The manifest and source-at-training snapshots retain their original bytes (including Windows line endings) through Git attributes so their recorded SHA-256 values remain verifiable. Other report formatting may be compacted; log trailing whitespace and the local workspace prefix are normalized for publication.

- `dataset-source.json`, `manifest.jsonl`, `split-audit.json`, `image-hashes.json`: official source, archive checksum, exact image IDs / relative paths / labels / splits, clipped-box audit, file and decoded-pixel hashes.
- `protocol.json`, model subdirectories: fixed protocol, configuration, all 15 epochs, validation-selected checkpoint metrics and completion records for both models.
- `source-at-training/`: actual trainer/controller snapshots used for this run. Final source converts the recorded torch version to plain text; these snapshots retain the original metadata behavior.
- `training-output.txt`: both completed training runs and the metadata-safe-loading failure at the first subsequent evaluation. This failure is retained, not represented as a clean controller run.
- `provenance.json`, `summary.json`: raw vs portable checkpoint SHA-256, tensor equality, final classification and pipeline reports. Metadata-only export was followed by evaluation in a fresh process, without retraining or changing model selection.
- `*-test.json`, `*.per_image.csv`: full classification matrices and per-image predictions, plus the separately measured whole-image pipeline. Classifier reports use GT boxes; pipeline reports use actual YOLO boxes.
- `results.png`, `finetuned-confusion.csv`, `most-confused.csv`: statistical figures and error analysis derived from those reports.
- `integration.json`, `system-smoke-output.txt`, `tests-output.txt`: fresh model/service checks, existing system regression checks and algorithm tests. These are engineering checks, separate from the accuracy estimates.

See [the experiment report](../../docs/CUB200_EXPERIMENT.md) for limitations, dataset rights and executable reproduction commands. The official warning about possible ImageNet / CUB overlap also applies to this experiment.
