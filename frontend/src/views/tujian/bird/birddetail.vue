<template>
    <div class="container">
        <div class="detail">
            <h1>基本信息</h1>
            <div class="mu">
                <div class="title">
                    <el-tag
                        :key="detaildata.mu"
                        type="dark"
                        effect="dark">
                        <span>目</span>
                    </el-tag>
                </div>
                <h2>{{ this.detaildata.mu+"("+this.detaildata.muEng+")" }}</h2>
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
                <h2>{{ this.detaildata.ke+"("+this.detaildata.keEng+")" }}</h2>
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
                <h2>{{ this.detaildata.name }}</h2>
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
                <h2>{{ this.detaildata.nameLatin }}</h2>
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
                <h2>{{ this.detaildata.introduction }}</h2>
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
                <h2>{{ this.detaildata.characteristic }}</h2>
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
                <h2>{{ this.detaildata.growthAndDistribution }}</h2>
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
                <h2>{{ this.detaildata.livingHabit }}</h2>
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
        <div class="divider-container">
            <el-divider class="full-divider" direction="vertical"></el-divider>
        </div>
        <div class="picture">
            <h1>图片</h1>
            <div class="pic">
                <div class="image-container" v-for="(item, index) in this.detaildata.picurls">
                    <el-image
                        style="width: 150px; height: 150px"
                        :src="item"
                        lazy
                    ></el-image>
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
            detaildata: {}
        }
    },
    mounted() {

        const getname = this.$route.query.name;
        this.$store.state.currentPlace = this.$route.path+"?name="+getname;

        axios.get('/bird/birdDetail', {
            params: {
                name: getname
            }
        })
        .then(response => {
            // 请求成功，处理响应
            this.detaildata = response.data.data;
            console.log(response)
        })
        .catch(error => {
            // 请求失败，处理错误
            console.error('Error:', error);
        });
    }
}
</script>

<style lang="less" scoped>
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
}

.divider-container {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: stretch;
    width: 2px;
    background-color: gray;
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
    // flex-wrap: nowrap;
    width: 25%;
    justify-content: center;
    margin-bottom: 20px;

}
.pic {
    margin: 5px;
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
}
</style>
