<template>
    <div class="container">
        <div class="detail">
            <h1>基本信息</h1>
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
                        :key="detaildata.knowledge"
                        type="dark"
                        effect="dark">
                        <span>相关知识</span>
                    </el-tag>
                </div>
                <h2>{{ this.detaildata.knowledge}}</h2>
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
                        :key="detaildata.history"
                        type="dark"
                        effect="dark">
                        <span>历史</span>
                    </el-tag>
                </div>
                <h2>{{ this.detaildata.history }}</h2>
            </div>
            <div class="mu">
                <div class="title">
                    <el-tag
                        :key="detaildata.owner"
                        type="dark"
                        effect="dark">
                        <span>收养人士</span>
                    </el-tag>
                </div>
                <h2>{{ this.detaildata.owner }}</h2>
            </div>
            <div class="mu">
                <div class="title">
                    <el-tag
                        :key="detaildata.notice"
                        type="dark"
                        effect="dark">
                        <span>注意事项</span>
                    </el-tag>
                </div>
                <h2>{{ this.detaildata.notice }}</h2>
            </div>
            <div class="mu">
                <div class="title">
                    <el-tag
                        :key="detaildata.size"
                        type="dark"
                        effect="dark">
                        <span>体型</span>
                    </el-tag>
                </div>
                <h2>{{ this.detaildata.size }}</h2>
            </div>
            <div class="mu">
                <div class="title">
                    <el-tag
                        :key="detaildata.maoLong"
                        type="dark"
                        effect="dark">
                        <span>狗毛</span>
                    </el-tag>
                </div>
                <h2>{{ this.detaildata.maoLong }}</h2>
            </div>
            <div class="mu">
                <div class="title">
                    <el-tag
                        :key="detaildata.nextname"
                        type="dark"
                        effect="dark">
                        <span>别称</span>
                    </el-tag>
                </div>
                <h2>{{ this.detaildata.nextname }}</h2>

            </div>
        </div>
        <div class="divider-container">
            <el-divider class="full-divider" direction="vertical"></el-divider>
        </div>
        <div class="picture">
            <h1>图片</h1>
            <div class="pic">
                <div class="image-container">
                    <el-image
                        style="width: 350px; height: auto"
                        :src="this.detaildata.imageUrl"
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
        axios.get('/dog/dogdata', {
            params: {
                name: getname
            }
        })
        .then(response => {
            // 请求成功，处理响应
            this.detaildata = response.data.data;
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
    justify-content: center; /* Horizontally center the image */
    align-items: center; /* Vertically center the image */
    width: 100%; /* Set width to 100% to fill the container */
    margin-bottom: 20px;

}
.pic {
    text-align: center;
    display: flex;
}
</style>
