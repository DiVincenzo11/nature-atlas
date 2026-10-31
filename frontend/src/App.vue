<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
import router from './router';
import {Message} from 'element-ui'
import WebSocketService from '@/utils/websocket.js'

export default {
  name: 'app',
  components: {
    router,
  },
  mounted() {
    // 页面加载时，如果本地 sessionStorage 里有 token，就用它去 init WebSocket
    const token = sessionStorage.getItem('token');
    const userinfo = JSON.parse(sessionStorage.getItem('userinfo'));

    if (token && userinfo && userinfo.userId) {
      WebSocketService.init(token, this.$bus);
    }

    // 订阅全局下线事件
    this.$bus.$on('global-offline', msg => {
      // 弹全局提示
      Message.warning(msg);
      // 清理状态并跳登录
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('userinfo');
      if (this.$route.path !== '/login') {
        this.$router.replace({path: '/login'});
      }
    })
  }
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
  border: 0;
  background: #F6F6F6;
}
</style>


