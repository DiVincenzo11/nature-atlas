<template>
    <div class="container"><el-alert title="图片识别服务未配置：本地演示支持图鉴与特征检索，暂无真实 AI 推理结果。" type="info" :closable="false" style="position:absolute;top:8px;left:2%;width:96%;z-index:1" />
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
                <div v-for="(item, i) in result" @click="lookDetail(i)">
                    <img :src="item[2]" alt="" v-if="item[2]!==''">
                    <img src="../../../../public/image/16.png" alt="" v-else>
                    <div class="name">{{ item[0] }}</div>
                    <div class="rate">准确度：<div class="point">{{ item[1] }}</div></div>
                </div>
            </div>
        </div>
        <div class="divider-container">
            <el-divider class="full-divider" direction="vertical"></el-divider>
        </div>
        <div class="right">
            <div class="detail" v-if="detaildata.length!==0">
                <h1>识别结果</h1>
                <h2 v-for="(item, i) in detaildata">
                    <el-tag
                        class="title"
                        type="dark"
                        effect="dark">
                        <span>{{i}}</span>
                    </el-tag>
                    <div v-for="(item2, i2) in item" class="content">{{item2}}</div>
                </h2>
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
import { Message } from 'element-ui';
import axios from '../../../utils/request.js';
export default {

    mounted(){
        this.$store.state.currentPlace = '/scan/plantscan';
    },
    data() {
      return {
        ImageUrl: '',
        disabled: false,
        limit: 1,
        result: [],
        detaildata: [],
        loading: false,
        tips: '暂无识别结果'
      };
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
        handleExceed(){
        Message.error('超出数量了')
        },
        handleAvatarSuccess(file) {
            // 若未登录则发出警告
            if(this.$store.state.token==='')return Message.error('请先进行登录！')
            // 开启加载动画
            this.loading = true

            this.ImageUrl = file.url;
            const formData = new FormData();
            formData.append('file', file.raw);
            formData.append('userid', this.$store.state.userid);

            axios.post('/detect/plantpic', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data;',
                    "token": this.$store.state.token
                },

            })
            .then(res => {
                // 结束加载动画
                this.loading = false

                if(res === '')return Message.error('token已过期，请重新登录')

                if(res.code !== 0) return Message.error(res.mseeage || "识别服务未配置");
            this.result = res.data
                console.log(res)
            })
            .catch(err => {
                console.log(err);
                // 结束加载动画
                this.loading = false
            });
        },
        lookDetail(i){
            if(this.result[i][3]==='')return(
                this.detaildata = [],
                this.tips = '暂无详细数据',
                Message.error("暂无该植物的详情信息")

            )
            this.detaildata = this.result[i][3]
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
.left{
    position: fixed; /* 左侧区域固定定位 */
    top: 75px;
    bottom: 0;
    left: 10px;
    width: 250px; /* 左侧区域宽度 */
    overflow-y: scroll;
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
    max-height: calc(100vh - 20px); /* 右边图片区域最大高度为视口高度减去一定空间 */
}
.upload-box{
    position: absolute;
    left: 50%;
    height: 148px;
    transform: translate(-50%);
    overflow: hidden;
}
.detail h1{
    font-family: '黑体';
    font-size: 22px;
    margin-top: 10px;
    margin-bottom: 15px;
    // align-items: center;
    text-align: center;
}
.image-box{
    position: absolute;
    top: 250px;
    left: 50%;
    height: 148px;
    transform: translate(-50%);
    img{
        height: 148px;
        width: 148px;
        border-radius: 5px;
    }
    .name{
        font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;
        font-size: 16px;
        text-align: center;
    }
    .rate{
        margin-bottom: 10px;
        font-size: 13px;
        text-align: center;
        font-style: italic;
        display: flex;
        justify-content: center;
        .point{
            font-weight: bold;
        }
    }
}

.title {
    margin-left: 10px;
    font-family: '黑体';
    font-size: 16px;
}
.content{
    margin-top: 5px;
    margin-bottom: 5px;
    font-size: 16px;
    font-family: '宋体';
    margin-left: 10px;
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
.loading-text{
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
