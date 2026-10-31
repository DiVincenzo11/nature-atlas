<template>
    <div class="container">
        <div class="background">
            <img src="../../../../public/image/动悉物语大logo.png" width="600px">
        </div>
        <div class="right">
            <div class="item" v-for="(item, index) in this.detaildata">
                <div class="content" @click="redirectToPage(item.url)">
                    <el-tag
                        :key="null"
                        effect="dark"
                        class="content-tag"
                        style="width: 300px;"
                        >
                        {{ item.introduction }}

                    </el-tag>
                    <div class="pic">
                        <img class="img" :src="item.picurl" width="300px" height="168.75px">
                    </div>
                </div>

                <el-divider></el-divider>
            </div>
        </div>
        <!-- <a href="https://mp.weixin.qq.com/s/-vhIsDsOtA-bY6GKZBiYnA" target="_blank">跳转到外部网页</a> -->
    </div>
</template>

<script>
import axios from "axios";
export default {
    name: 'tuiwen',

    data () {
      return {
        detaildata: [],
      }
    },

    mounted() {
        this.$store.state.currentPlace = '/lesson/tuiwen';
        axios.get('/tuiwen')
        .then(response => {
            this.detaildata = response.data.data;
        })
        .catch(error => {
            // 请求失败，处理错误
            console.error('Error:', error);
        });
    },

    methods: {
        redirectToPage(url) {
            // 使用 JavaScript 实现页面跳转
            window.location.href = url;
        }
    }
  }
</script>

<style lang="less" scoped>

.container{
    position: relative; /* 设置相对定位 */
}

.background{
    position: fixed;
    margin-left: 20px;
}

.right{
    position: absolute; /* 设置绝对定位 */
    top: 0; /* 从顶部开始 */
    left: 650px; /* 左侧固定宽度 */
    display: flex;
    flex-wrap: wrap; /* 允许换行 */
    justify-content: center; /* 居中对齐 */
    margin-top: 50px;
    overflow-y: auto;
    max-height: 100vh;
}

.item {
    width: 50%; /* 一行显示两个图片 */
    display: flex;
    flex-direction: column;
}

.content {
    display: flex;
    flex-direction: column;
    align-items: center; /* 垂直居中 */
}

.pic {
    text-align: center;
}

.content-tag{
    text-align: center;;
    font-size: 14px;
}


</style>
