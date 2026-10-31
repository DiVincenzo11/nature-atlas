<template>
    <div class="container">
        <div class="left">
            <div class="tap">
                <el-tag
                    :key="null"
                    type="dark"
                    effect="dark">
                    <span class="list-title">科普视频列表</span>
                </el-tag>
            </div>

            <div class="pic">
                <div class="image-container" v-for="(item, index) in this.detaildata" @click="changeVideo(item.cover, item.url)">
                    <div class="title">
                        <span class="video-title">{{ item.title }}</span>
                    </div>
                    <el-image
                        style="width: 200px; height: 200px"
                        :src="item.cover"
                    ></el-image>
                </div>
            </div>
        </div>
        <div class="divider-container">
            <el-divider class="full-divider" direction="vertical"></el-divider>
        </div>
        <div class="right">
            <div class="background" v-if="this.src===''">
                <img src="https://img.51miz.com/Element/00/77/75/98/a7d7f38b_E777598_1cc23f7c.png">
            </div>
            <div class="show" v-if="this.src!=''">
                <div class="video-container">
                    <Video :src="src" :second="3" />
                </div>
            </div>
        </div>
    </div>

  </template>
  <script>
  import Video from '@/views/videoutil.vue'
  import axios from "axios";
  export default {
    name: 'VideoPlay',
    components: {
      Video
    },
    data () {
      return {
        detaildata: [],
        src: '',
        poster: ''
      }
    },

    mounted() {
        this.$store.state.currentPlace = '/lesson/video';
        axios.get('/video')
        .then(response => {
            this.detaildata = response.data.data;
        })
        .catch(error => {
            // 请求失败，处理错误
            console.error('Error:', error);
        });
    },

    methods: {
        changeVideo(cover, url){
            this.src = url;
            this.poster = cover
        }
    }
  }
  </script>

<style lang="less" scoped>
.background{
    width: 150px;
    height: auto;
    text-align: center;
}
.container {
  display: flex;
}

.left {
  margin-left: 20px;
  margin-right: 20px;
  width: 60vh;
  overflow: auto;
  max-height: 100vh;
}

.image-container {
  position: relative;
  text-align: center;
  margin-bottom: 20px;
}

.title{
    margin-bottom: 5px;
}

.video-title {
  font-size: 14px;
}

.list-title {
  font-size: 16px;
}

.tap {
  text-align: center;
  margin-top: 10px;
  margin-bottom: 20px;
}

.divider-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: stretch;
  width: 2px;
  background-color: gray;
}

.right {
    width: calc(100% - 420px);
    margin-left: auto;
    margin-right: 20px;
    position: fixed;
    top: 0;
    bottom: 0;
    right: 0;
    overflow-y: auto;
    display: flex;
    justify-content: center;
    align-items: center;


}
.video-container {

    text-align: center;
}
.show{
    text-align: center;
}

/* Center the images */
.image-container el-image {
  display: flex;
  justify-content: center;
  align-items: center;
}



</style>
