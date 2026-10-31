<template>
    <div class="container">
      <div class="container-item">
        <div class="logo" @click="gotohome">
          <img src="../../../../public/image/动悉物语.png" alt="">
        </div>
        <el-menu :default-active="$route.path" class="el-menu-demo" mode="horizontal" @select="" background-color="#87B15F" text-color="#fff" active-text-color="#ffd04b" router>
          <el-menu-item index="/">首页</el-menu-item>
          <el-submenu index="/scan">
            <template slot="title">图像识别</template>
            <el-menu-item index="/scan/birdscan"><span class="iconfont icon-niao"></span>鸟类识别</el-menu-item>
            <el-menu-item index="/scan/plantscan"><span class="iconfont icon-zhiwu"></span>植物识别</el-menu-item>
          </el-submenu>
          <el-menu-item index="/character">特征识别</el-menu-item>
          <el-submenu index="/tujian">
            <template slot="title">图鉴</template>
            <el-menu-item index="/tujian/bird"><span class="iconfont icon-niao"></span>鸟图鉴</el-menu-item>
            <el-menu-item index="/tujian/cat"><span class="iconfont icon-mao"></span>猫图鉴</el-menu-item>
            <el-menu-item index="/tujian/dog"><span class="iconfont icon-gou"></span>狗图鉴</el-menu-item>
            <el-menu-item index="/tujian/turtle"><span class="iconfont icon-wugui"></span>爬行图鉴</el-menu-item>
            <el-menu-item index="/tujian/liangqi"><span class="iconfont icon-qingwa"></span>两栖图鉴</el-menu-item>
          </el-submenu>
          <el-submenu index="/lesson">
            <template slot="title">小课堂</template>
            <el-menu-item index="/lesson/tuiwen"><span class="iconfont icon-wenzhang"></span>科普文章</el-menu-item>
            <el-menu-item index="/lesson/video"><span class="iconfont icon-shipin"></span>科普视频</el-menu-item>
          </el-submenu>
          <el-menu-item index="/expert/chat">专家咨询</el-menu-item>
          <el-menu-item index="/setting">我的</el-menu-item>
        </el-menu>

        <div class="search-box">
          <div class="search-icon" @click="search">🔍</div>
          <el-autocomplete
            class="search"
            v-model="state"
            :fetch-suggestions="querySearch"
            placeholder="请输入内容"
            :trigger-on-focus="false"
            @select="handleSelect"
            @keyup.enter.native="search"
          ></el-autocomplete>
        </div>
        <div class="demo-basic--circle">
          <div class="block">
            <div @click="ahead">
              <el-avatar size="50" :src="this.headportrait" class="headPortrait"></el-avatar>
            </div>
            <div class="logout" @click="logout" v-if="headportrait!=='https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"><h1 class="logout-text">退出</h1></div>
          </div>
        </div>
      </div>
    </div>
</template>


<script>
import axios from 'axios';
import wsService from '@/utils/websocket'

export default {
    async mounted(){
      this.initial()
      this.getAll()
    },

    data(){
      return{
        state: '',
        headportrait: '',
        searchData: []
      }
    },
    methods: {
      handleOpen(key, keyPath) {
        console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        console.log(key, keyPath);
      },
      querySearch(queryString, cb) {
        var searchData = this.searchData;
        var results = queryString ? searchData.filter(this.createFilter(queryString)) : searchData;
        // 调用 callback 返回建议列表的数据
        cb(results);
      },
      createFilter(queryString) {
        return (searchData) => {
          return (searchData.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
        };
      },
      handleSelect(item) {
        console.log(item);
        this.search()
      },
      ahead(){
        // 未登录
        if(this.$store.state.username==""){

          this.$router.replace({path: '/login'});
        }
        else if(this.$store.state.currentPlace!="/setting"){

          this.$router.replace({path: '/setting'});
        }
      },
      gotohome(){
        if(this.$store.state.currentPlace === '/')return
        this.$router.replace({path: '/'})
      },
      logout(){
        sessionStorage.removeItem('userinfo')
        sessionStorage.removeItem('token')
        this.$store.commit('logout')
        this.initial()

        wsService.disconnect()

        axios.post('/user/logout', null, {
          headers: { Authorization: 'Bearer ' + sessionStorage.getItem('token') }
        });

        this.$router.replace({ path: '/login' })
      },
      initial(){
        this.headportrait = this.$store.state.headportrait;
      },
      getAll(){
        //console.log("请求路径:", axios.defaults.baseURL + '/getAll') // 输出完整路径
        axios.get('/getAll')
        .then(res => {
          this.searchData = JSON.parse(JSON.stringify(res.data.data).replace(/name/g,'value'))
        })
        .catch(res => {
          console.log(res)
        })
      },
      search(){
        if(this.$store.state.currentPlace==='/search')return (
          this.$router.replace({
            path: '/search',
            query: {
              key: this.state
            }
          }),
          this.$router.go(0)
        )
        this.$router.push({
          path: '/search',
          query: {
            key: this.state
          }
        })
      }
    },
    watch:{
      "$store.state.headportrait":{
            handler:function(newVal,oldVal){
                this.initial()
            }
        }
    }
  }
</script>


<style>
.container{
  position: fixed;
  display: flex;
  width: 100%;
  height: auto;
}

.container-item{
  position: relative;
  display: flex;
}

.logo {
  position: absolute;
  left: 1300px;
  top: 2px;
  /* bottom: 0px; */
  z-index: 999;
  img{
    width: auto;
    height: 50px;
    cursor: pointer;
  }
}

.el-menu-demo{
  position: absolute;
  width: 100vmax;
  left: 0px;
  display: flex;
}

.search-box{
  position: absolute;
  display: flex;
  align-items: center;
  left: 700px;
  top: 10px;
  z-index: 999;
  .search-icon{
    position: relative;
    left: 300px;
    font-size: 20px;
    z-index: 1000;
    cursor: pointer;
  }
  .search{
    width: 300px;
  }
}

.demo-basic--circle{
  position: absolute;
  left: 1200px;
  top: 10px;
  cursor: pointer;
  z-index: 999;
}

.iconfont{
  padding: 10px;
}
.block{
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 50px;
  height: auto;
}
.logout{
  width: 50px;
  height: 50px;
  border: 1px solid lightgray;
  border-radius: 10px;
  background-color: white;
  text-align: center;
  line-height: 50px;
  display: none;
}
.logout::before{
  content: '';
    width: 30px;
    height: 30px;
    clip-path: polygon(50% 40%,0 0,100% 0);
    background-color: rgb(180, 180, 210);
    position: absolute;
    top: 45px;
    left: 50%;
    transform: translate(-50%);
    bottom: -38px;
}

.block:hover .logout{
  display: block;
}
.logout-text:hover{
  color: #ab4a4a;
}


</style>
