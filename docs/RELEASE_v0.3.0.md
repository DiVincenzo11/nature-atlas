# v0.3.0 · CUB-200 真实训练与两阶段评测

新增在 CUB-200-2011 完整 200 类上的 EfficientNetV2-S 训练权重、冻结主干对照、全量整图 YOLOv8n＋EfficientNetV2 评测，以及从数据下载到网页接入的复现入口。

- 数据：训练 4,794、验证 1,200、官方测试 5,794；按验证 macro-F1 选模，测试不参与训练、选模或调阈值。
- GT 裁剪分类：冻结主干 Top-1 63.57%，全网微调 **78.65%**（+15.08 个百分点）；微调 Top-5 94.79%、macro-F1 0.7857。
- 整图：主框 IoU≥0.5、鸟种正确且通过固定分类阈值，**4,215 / 5,794 = 72.75%**。单主体联合成功率，不是 detection mAP。
- 提供两组已选权重、SHA-256、完整训练记录、划分 ID/哈希、逐图预测、混淆矩阵与不含照片的统计图。
- 发布权重支持默认 `weights_only=True` 加载；本次原始检查点仅规范化 PyTorch 版本元数据，逐项验证训练张量保持一致。
- Python API → 已认证 Java 上传 → Vue 页面实测成功；13 项算法测试通过，修复检测库替换 Pillow 解码器导致无效图片返回 HTTP 500 的问题。

这是新的公开数据重建实验，**不是原比赛代码/权重恢复，也不是原比赛性能复现**。ImageNet 预训练图可能与 CUB 测试图重叠；单种子、固定训练预算，未做开放集或生产部署验收。CUB 研究数据及其衍生权重仅用于非商业研究/教育；原图和含原图的网页截图不随仓库发布。

下载 `cub200-finetuned-efficientnetv2-s.pt` 放入 `bird_service/models/`，并按 `SHA256SUMS` 核对：

```shell
nature-birds serve --detector models/yolov8n.pt --classifier models/cub200-finetuned-efficientnetv2-s.pt --device cpu --port 18902
```

先用 `nature-birds fetch-models --out models` 获取上游检测器；它不会自动加载 CUB 分类权重。完整步骤见 [实验报告](https://github.com/DiVincenzo11/nature-atlas/blob/v0.3.0/bird_service/docs/CUB200_EXPERIMENT.md)。
