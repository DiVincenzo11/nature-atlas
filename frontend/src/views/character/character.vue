<template>
    <div class="container">
        <div class="left">
            <div class="detail">
                <h1>特征选择</h1>
            </div>
            <div class="choose">
                <div class="choose-item">
                    <el-tag
                        effect="dark"
                        class="category-tag"
                        >
                        鸟喙

                    </el-tag>
                    &nbsp;
                    <el-select v-model="selectedMouth" clearable placeholder="请选择" @change="updateMouth">
                        <el-option
                            v-for="item in optionsmouth"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <div class="choose-item">
                    <el-tag
                        effect="dark"
                        class="category-tag"
                        >
                        羽冠

                    </el-tag>
                    &nbsp;
                    <el-select v-model="selectedCrest" clearable placeholder="请选择" @change="updateCrest">
                        <el-option
                            v-for="item in optionscrest"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <div class="choose-item">
                    <el-tag
                        effect="dark"
                        class="category-tag"
                        >
                        脖子

                    </el-tag>
                    &nbsp;
                    <el-select v-model="selectedNeck" clearable placeholder="请选择" @change="updateNeck">
                        <el-option
                            v-for="item in optionsneck"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <div class="choose-item">
                    <el-tag
                        effect="dark"
                        class="category-tag"
                        >
                        背部

                    </el-tag>
                    &nbsp;
                    <el-select v-model="selectedBack" clearable placeholder="请选择" @change="updateBack">
                        <el-option
                            v-for="item in optionsback"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <div class="choose-item">
                    <el-tag
                        effect="dark"
                        class="category-tag"
                        >
                        鸟爪

                    </el-tag>
                    &nbsp;
                    <el-select v-model="selectedLeg" clearable placeholder="请选择" @change="updateLeg">
                        <el-option
                            v-for="item in optionsleg"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>

            </div>
            <div class="img">
                <img src="../../../public/image/bird.png" width="150px" height="auto">
            </div>
        </div>

        <div class="divider-container">
            <el-divider class="full-divider" direction="vertical"></el-divider>
        </div>
        <div class="right">
            <div class="picture">
            <h1>查询结果</h1><p style="font-size:12px;color:#67746d">演示图鉴 · 封面为通用插画，非物种实拍</p>
            <div class="pic">
                <div class="image-container" v-for="(item, index) in this.detaildata" v-if="item[1]!=null" @click="getdetail(item[0])">
                    <el-image
                        style="width: 150px; height: 150px"
                        :src="item[1]"
                        lazy
                    >
                    </el-image>
                    <span class="zhong-span">{{ item[0] }}</span>
                </div>
            </div>
        </div>
        </div>

    </div>
</template>

<script>
import axios from "axios";
export default {
    data() {
        return {
            selectedMouth:"",
            selectedBack:"",
            selectedCrest:"",
            selectedLeg:"",
            selectedNeck:"",
            detaildata: [],
            mouth: "",
            crest: "",
            neck: "",
            back: "",
            leg: "",
            optionsmouth: [{
                value: '黄褐色',
                label: '黄褐色'
            }, {
                value: '非黄褐色',
                label: '非黄褐色'
            }, {
                value: '',
                label: '不清楚'
            },
            ],
            optionscrest: [{
                value: '具羽冠',
                label: '有羽冠'
            }, {
                value: '不具羽冠',
                label: '没有羽冠'
            }, {
                value: '',
                label: '不清楚'
            },
            ],
            optionsneck: [{
                value: '脖纯色',
                label: '脖子纯色'
            }, {
                value: '脖非纯色',
                label: '脖子非纯色'
            }, {
                value: '',
                label: '不清楚'
            },
            ],
            optionsback: [{
                value: '背纯色',
                label: '背部纯色'
            }, {
                value: '背非纯色',
                label: '背部非纯色'
            }, {
                value: '',
                label: '不清楚'
            },
            ],
            optionsleg: [{
                value: '腿黑色',
                label: '鸟爪黑色'
            }, {
                value: '腿非黑色',
                label: '鸟爪非黑色'
            }, {
                value: '',
                label: '不清楚'
            },
            ]
        }
    },
    mounted() {
        this.$store.state.currentPlace = '/character'
        axios.post('/bird/birdchar', {
            "mouth":this.mouth,
            "crest":this.crest,
            "neck":this.neck,
            "back":this.back,
            "leg":this.leg
        })
        .then(response => {
            // 请求成功，处理响应
            console.log(response)
            this.detaildata = response.data.data;
        })
        .catch(error => {
            // 请求失败，处理错误
            console.error('Error:', error);
        });
    },

    methods: {
        update(){
            axios.post('/bird/birdchar', {
            "mouth":this.mouth,
            "crest":this.crest,
            "neck":this.neck,
            "back":this.back,
            "leg":this.leg
        })
        .then(response => {
            // 请求成功，处理响应
            this.detaildata = response.data.data;

        })
        .catch(error => {
            // 请求失败，处理错误
            console.error('Error:', error);
        });
        },

        getdetail(name) {
            this.$router.push({
                path: "/tujian/birddetail",
                query: {
                    name: name,
                },
            });
        },

        updateMouth() {
            this.mouth = this.selectedMouth;
            this.update();
        },
        updateCrest() {
            this.crest = this.selectedCrest;
            this.update()
        },
        updateBack() {
            this.back = this.selectedBack;
            this.update()
        },
        updateNeck() {
            this.neck = this.selectedNeck;
            this.update()
        },
        updateLeg() {
            this.leg = this.selectedLeg;
            this.update()
        }
    }
}
</script>

<style lang="less" scoped>
.img{
    padding-top: 10px;
    text-align: center;
}
.left{
    position: fixed; /* 左侧区域固定定位 */
    top: 75px;
    bottom: 0;
    left: 10px;
    width: 250px; /* 左侧区域宽度 */

}
.choose-item{

    margin-left: 20px;
    margin-top: 10px;
    margin-bottom: 10px;

}

.container {
    display: flex;
    position: relative; /* 添加相对定位以使绝对定位的元素相对于父容器定位 */
}
.detail h1{
    font-family: '黑体';
    font-size: 22px;
    margin-top: 10px;
    margin-bottom: 15px;
    // align-items: center;
    text-align: center;
}
.picture h1{
    font-family: '黑体';
    font-size: 22px;
    margin-left: 0%;
    margin-top: 10px;
    margin-bottom: 15px;
}
.detail {
    flex: 1;
}

.picture {
    flex: 1;
    text-align: center; /* 如果需要图片居中显示，可以添加这一行 */
    display: flex;
    flex-direction: column;
    position: relative;
    height: auto;
    width: 100%;
}


.title {
    margin-left: 10px;
}
.title span {
   font-family: '黑体';
   font-size: 16px;
}
.mu{
    margin-top: 5px;
    margin-bottom: 5px;
}
.mu h2{
    font-size: 16px;
    font-family: '宋体';
    margin-left: 10px;
    margin-top: 5px;
}
.image-container {
    display: flex;
    flex-direction: column;
    width: 20%;
    margin-bottom: 20px;
    align-items: center;
}
.pic {
    text-align: center;
    display: flex;
    flex-wrap: wrap; /* 允许换行 */
}
.zhong-span {
    width: 100%; /* 让 span 占据整个宽度 */
    text-align: center; /* 文字居中对齐 */
    margin-top: 10px; /* 适当的上边距 */
}
.right {
  flex: 1; /* 右边图片区域可伸缩 */
  position: absolute;
  top: 0;
    bottom: 0;
    right: 0;
    left: 255px; /* 左侧区域宽度 */
  max-height: calc(100vh - 20px); /* 右边图片区域最大高度为视口高度减去一定空间 */
}
.category-tag {
  font-size: 14px; /* 设置标签字号为16像素 */
  background-color: burlywood;
}
</style>
