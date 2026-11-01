<template>
  <div class="container"><el-alert :title="modelNotice" type="info" :closable="false" style="position:absolute;top:8px;left:2%;width:96%;z-index:1" />
    <div class="left">
      <div class="detail">
        <h1>图片选择</h1>
      </div>
      <div class="upload-box">
        <el-upload
            action="#"
            ref='pictureUpload'
            list-type="picture-card"
            :limit="limit"
            :on-exceed="handleExceed"
            :on-change="handleAvatarSuccess"
            :auto-upload="false">
          <i slot="default" class="el-icon-plus"></i>
          <div slot="file" slot-scope="{file}">
            <img
                class="el-upload-list__item-thumbnail"
                :src="file.url" alt=""
            >
            <span class="el-upload-list__item-actions">
                            <span
                                v-if="!disabled"
                                class="el-upload-list__item-delete"
                                @click="handleRemove(file)"
                            >
                                <i class="el-icon-delete"></i>
                            </span>
                        </span>
          </div>
        </el-upload>
      </div>
      <div class="image-box" v-if="result.length!==0">
        <div class="detail">
          <h1>识别结果</h1>
        </div>
        <div v-for="(item, i) in result.info_list" :key="i" @click="lookDetail(i)">
          <img :src="item[3]" alt="" v-if="item[3]!==''">
          <p v-else>图鉴暂无对应配图</p>
          <div class="name">{{ item[0] }}</div>
          <div class="rate">数量：
            <div class="point">{{ item[1] }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="divider-container">
      <el-divider class="full-divider" direction="vertical"></el-divider>
    </div>
    <div class="right">
      <div v-if="result.detect_img_url" class="local-result">
        <h2>两阶段识别输出</h2>
        <img :src="result.detect_img_url" alt="鸟类检测框与分类结果" style="max-width:100%;max-height:55vh">
        <p v-if="result.status === 'no_bird_detected'">未检测到鸟类，不生成物种预测。</p>
        <p v-for="(d, i) in result.detections" :key="'d'+i">
          {{ i+1 }}. {{ d.display_name }} · 检测置信度 {{ (d.detector_confidence*100).toFixed(1) }}%
          · 分类分数 {{ (d.classifier_probability*100).toFixed(1) }}%（未校准）
        </p>
      </div>
      <div class="detail" v-if="detaildata.length!==0">
        <h1>识别结果</h1>
        <div class="mu topmu">
          <div class="title">
            <el-tag
                :key="detaildata.mu"
                type="dark"
                effect="dark">
              <span>目</span>
            </el-tag>
          </div>
          <h2>{{
              this.detaildata.mu === null ? '暂无数据' : this.detaildata.mu + "(" + this.detaildata.muEng + ")"
            }}</h2>
        </div>

        <div class="mu">
          <div class="title">
            <el-tag
                :key="detaildata.ke"
                type="dark"
                effect="dark">
              <span>科</span>
            </el-tag>
          </div>
          <h2>{{
              this.detaildata.ke === null ? '暂无数据' : this.detaildata.ke + "(" + this.detaildata.keEng + ")"
            }}</h2>
        </div>

        <div class="mu">
          <div class="title">
            <el-tag
                :key="detaildata.name"
                type="dark"
                effect="dark">
              <span>名字</span>
            </el-tag>
          </div>
          <h2>{{ this.detaildata.name === null ? '暂无数据' : this.detaildata.name }}</h2>
        </div>
        <div class="mu">
          <div class="title">
            <el-tag
                :key="detaildata.nameLatin"
                type="dark"
                effect="dark">
              <span>拉丁名</span>
            </el-tag>
          </div>
          <h2>{{ this.detaildata.nameLatin === null ? '暂无数据' : this.detaildata.nameLatin }}</h2>
        </div>
        <div class="mu">
          <div class="title">
            <el-tag
                :key="detaildata.introduction"
                type="dark"
                effect="dark">
              <span>介绍</span>
            </el-tag>
          </div>
          <h2>{{ this.detaildata.introduction === null ? '暂无数据' : this.detaildata.introduction }}</h2>
        </div>
        <div class="mu">
          <div class="title">
            <el-tag
                :key="detaildata.characteristic"
                type="dark"
                effect="dark">
              <span>特征</span>
            </el-tag>
          </div>
          <h2>{{ this.detaildata.characteristic === null ? '暂无数据' : this.detaildata.characteristic }}</h2>
        </div>
        <div class="mu">
          <div class="title">
            <el-tag
                :key="detaildata.growthAndDistribution"
                type="dark"
                effect="dark">
              <span>生长环境</span>
            </el-tag>
          </div>
          <h2>{{
              this.detaildata.growthAndDistribution === null ? '暂无数据' : this.detaildata.growthAndDistribution
            }}</h2>
        </div>
        <div class="mu">
          <div class="title">
            <el-tag
                :key="detaildata.livingHabit"
                type="dark"
                effect="dark">
              <span>生活习惯</span>
            </el-tag>
          </div>
          <h2>{{ this.detaildata.livingHabit === null ? '暂无数据' : this.detaildata.livingHabit }}</h2>
        </div>
        <div v-if="this.detaildata.voiceURL!=null" class="mu">
          <div class="title">
            <el-tag
                :key="detaildata.voiceURL"
                type="dark"
                effect="dark">
              <span>音频</span>
            </el-tag>
          </div>
          <audio controls ref="audion" class="audio">
            <source :src="this.detaildata.voiceURL">
          </audio>
        </div>
      </div>
      <div class="detail" v-else>
        <h1>识别结果</h1>
        <el-empty :description="tips"></el-empty>
      </div>
    </div>

    <!-- 加载动画 -->
    <div class="overlay" v-if="loading">
      <div class="body">
        <div class="loading"></div>
        <div class="loading-text">正在识别......</div>
      </div>
    </div>
  </div>
</template>

<script>
import {Message} from 'element-ui';
import axios from '../../../utils/request';
import Compressor from "compressorjs";

export default {

  mounted() {
    this.$store.state.currentPlace = '/scan/birdscan';
  },
  data() {
    return {
      disabled: false,
      limit: 1,
      result: [],
      detaildata: [],
      loading: false,
      tips: '暂无识别结果'
    };
  },
  computed: {
    modelNotice() {
      return this.result.model ? this.result.model.notice : '可接入本地 YOLOv8＋EfficientNetV2 服务；需启动并配置模型。原比赛权重未恢复，公开预训练演示不代表原项目性能。';
    }
  },
  methods: {
    handleRemove(file) {
      let uploadFiles = this.$refs.pictureUpload.uploadFiles
      for (var i = 0; i < uploadFiles.length; i++) {
        if (uploadFiles[i]['url'] == file.url) {
          uploadFiles.splice(i, 1)
        }
      }
    },
    handleExceed() {
      Message.error('超出数量了')
    },
    handleAvatarSuccess(file) {

      // 若未登录则发出警告
      if (this.$store.state.token === '') return Message.error('请先进行登录！')

      // 开启加载动画
      this.loading = true

      // 定义最大文件大小为1MB
      const maxSize = 1024 * 1024;

      // 检查文件大小
      if (file.raw.size <= maxSize) {
        // 小于等于1MB，直接上传
        this.uploadFile(file.raw);
      } else {
        // 大于1MB，进行压缩
        this.compressAndUpload(file.raw, maxSize);
      }
    },
    compressAndUpload(originalFile, maxSize) {
      new Compressor(originalFile, {
        quality: 0.95, // 初始压缩质量
        maxWidth: 1024, // 最大宽度
        maxHeight: 1024, // 最大高度
        success: (compressedFile) => {
          // 检查压缩后文件大小
          if (compressedFile.size > maxSize) {
            this.$message.error("图片压缩后仍超过1MB，请选择较小的文件");
            this.loading = false;
          } else {
            // 压缩成功，上传
            this.uploadFile(compressedFile);
          }
        },
        error: (err) => {
          this.$message.error("图片压缩失败，请重试");
          console.error(err);
        }
      });
    },
    uploadFile(fileToUpload) {
      const formData = new FormData();

      formData.append('file', fileToUpload);
      formData.append('userid', this.$store.state.userid);

      axios.post('/detect/birdpic', formData, {
        headers: {
          'Content-Type': 'multipart/form-data;',
          "token": this.$store.state.token
        },

      })
          .then(res => {
            // 结束加载动画
            this.loading = false

            if (res.data === '') return Message.error('token已过期，请重新登录')

            if(res.code !== 0) return Message.error(res.mseeage || "识别服务未配置");
            this.result = res.data

            if (this.result.info_list && this.result.info_list.length === 0) return (
                this.detaildata = [],
                    this.tips = '暂无详细数据'
            )
            this.lookDetail(0)


          })
          .catch(err => {
            console.log(err);
            // 结束加载动画
            this.loading = false
            Message.error("识别失败，请重试")
          });
    },
    lookDetail(i) {
      if (this.result.info_list && this.result.info_list.length === 0) return (
          this.detaildata = [],
              this.tips = this.result.status === 'no_bird_detected' ? '未检测到鸟类' : '图鉴暂无对应条目，识别图与分类分数见上方'
      )

      axios.get('/bird/birdDetail', {
        params: {
          name: this.result.info_list[i][0]
        }
      })
          .then(response => {
            // 请求成功，处理响应
            if (!response.data || response.code !== 0) {
              this.detaildata = [];
              this.tips = '图鉴暂无该类别条目；模型结果仍可在上方查看';
            } else {
              this.detaildata = response.data;
            }
            console.log(response)
          })
          .catch(error => {
            // 请求失败，处理错误
            console.error('Error:', error);
          });
    }
  }
}


</script>

<style lang="less" scoped>
.container {
  display: flex;
  position: relative; /* 添加相对定位以使绝对定位的元素相对于父容器定位 */
  height: 90vh;
}

.left {
  position: fixed; /* 左侧区域固定定位 */
  top: 75px;
  bottom: 0;
  left: 10px;
  width: 250px; /* 左侧区域宽度 */
  overflow-y: scroll;
  padding-top: 65px;
  box-sizing: border-box;
}

.divider-container {
  position: absolute;
  display: flex;
  justify-content: center;
  align-items: stretch;
  width: 2px;
  background-color: gray;
  left: 260px;
  top: 25px;
  bottom: 0px;
}

.right {
  flex: 1; /* 右边图片区域可伸缩 */
  position: absolute;
  top: 0;
  bottom: 0;
  right: 0;
  left: 265px; /* 左侧区域宽度 */
  height: 90vh;
  overflow-y: auto;
  padding-top: 65px;
  max-height: calc(100vh - 20px); /* 右边图片区域最大高度为视口高度减去一定空间 */
}

.upload-box {
  position: absolute;
  left: 50%;
  height: 148px;
  transform: translate(-50%);
  overflow: hidden;
}

.detail h1 {
  position: relative;
  font-family: '黑体';
  font-size: 22px;
  margin-top: 10px;
  margin-bottom: 15px;
  // align-items: center;
  text-align: center;
}

.detectImg {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  height: 500px;
  width: 500px;
}

.image-box {
  position: absolute;
  top: 250px;
  left: 50%;
  height: 148px;
  transform: translate(-50%);

  img {
    height: 148px;
    width: 148px;
    border-radius: 5px;
  }

  .name {
    font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;
    font-size: 16px;
    text-align: center;
  }

  .rate {
    margin-bottom: 10px;
    font-size: 13px;
    display: flex;
    text-align: center;
    font-style: italic;
    justify-content: center;

    .point {
      font-weight: bold;
    }
  }
}

.title {
  margin-left: 10px;
}

.title span {
  font-family: '黑体';
  font-size: 16px;
}

.mu {
  margin-top: 5px;
  margin-bottom: 5px;
}

.topmu {
  margin-top: 520px;
}

.mu h2 {
  font-size: 16px;
  font-family: '宋体';
  margin-left: 10px;
  margin-top: 5px;
}

/* 遮罩层 */
.overlay {
  position: fixed;
  left: 0px;
  top: 0px;
  width: 100%;
  height: 100%;
  font-size: 16px;
  /* IE9以下不支持rgba模式 */
  background-color: rgba(0, 0, 0, 0.5);
  /* 兼容IE8及以下 */
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#7f000000,endColorstr=#7f000000);
  display: block;
}

.body {
  /* 弹性布局 让页面元素垂直+水平居中 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  /* 让页面始终占浏览器总高 */
  height: 100vh;
}

.loading {
  /* 相对定位 */
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  /* 只需要将其它的边的颜色设为透明就好 */
  border: 8px solid transparent;
  /* 我们需要隐藏其它三条边只显示一条边 */
  border-top-color: #9370db;
  animation: rotate 2s linear infinite;
}

.loading-text {
  font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;
  font-size: 16px;
  font-weight: bold;
  color: white;
  margin-top: 20px;
}

.loading::before {
  content: "";
  position: absolute;
  top: 5px;
  right: 5px;
  bottom: 5px;
  left: 5px;
  /* 这边也是一样 用伪元素做边框 */
  border-radius: 50%;
  border: 8px solid transparent;
  border-top-color: #ba55d3;
  animation: rotate 3s linear infinite;
}

.loading::after {
  content: "";
  position: absolute;
  top: 15px;
  right: 15px;
  bottom: 15px;
  left: 15px;
  border-radius: 50%;
  border: 8px solid transparent;
  border-top-color: #f0f;
  /* 动画 时长 linear是匀速运动 infinite 是无限次运动 */
  animation: rotate 1.5s linear infinite;
}

/* 下面定义一下动画 */
@keyframes rotate {
  0% {
    transform: rotate(0);
  }
  100% {
    /* 顺时针旋转360度 */
    transform: rotate(360deg);
  }
}

</style>
