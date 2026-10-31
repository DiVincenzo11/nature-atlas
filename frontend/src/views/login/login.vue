<template>
  <div class="app-container">
    <div class="login-box">
      <h2>登录平台</h2>
      <form>
        <div class="user-box">
          <input type="text" name="" required="" v-model="username" @keypress.enter="login">
          <label>用户名</label>
        </div>
        <div class="user-box">
          <input type="password" name="" required="" v-model="password" @keypress.enter="login">
          <label>密码</label>
        </div>
        <!-- &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; -->
        <a class="a1" @click="handleSubmit" style="cursor: pointer;">
          <span></span>
          <span></span>
          <span></span>
          <span></span>
          登录
        </a>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <a class="a2" @click="signUp_asd" style="cursor: pointer;">注册 </a>
      </form>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { Message } from "element-ui";
import wsService from '@/utils/websocket'

export default {
  name: "Login",
  data() {
    return {
      username: '',
      password: '',
      msg: '',
    }
  },
  async mounted(){

  },
  methods: {
    signUp_asd(){
      this.$router.replace({path: '/register'});

    },
    open1() {
      this.$message({
        showClose: true,
        message: this.msg,
        type: 'warning'
      });
    },
    open2() {
      this.$message({
        showClose: true,
        message: this.msg,
        type: 'success'
      });
    },
    open3() {
      this.$message({
        showClose: true,
        message: this.msg,
        type: 'error'
      });
    },
    login(){
      this.handleSubmit()
    },
    async handleSubmit() {
      if (!this.username) {
        return Message.error("请输入用户名");
      }
      if (!this.password) {
        return Message.error("请输入密码");
      }

      try {
        // 1. 发起登录请求
        const res = await axios.post('/user/login', {
          username: this.username,
          password: this.password,
        });
        if (res.status === 200 && res.data.data) {
          const [userInfo, token] = res.data.data;

          // 2. 保存状态
          sessionStorage.setItem("token", token);
          sessionStorage.setItem("userinfo", JSON.stringify(userInfo));
          this.$store.commit("getuserinfo", userInfo);
          this.$store.state.token = token;

          wsService.init(token, this.$bus);

          Message.success(`欢迎 ${userInfo.username} 上线`);
          await this.$router.replace({path: this.$store.state.currentPlace});
        } else {
          Message.error("账号/密码错误，请重新输入");
        }
      } catch (err) {
        console.error(err);
        Message.error("网络或服务器错误，请稍后重试");
      }
    },
  },
}
;
</script>

<style>

.app-container{
  position: fixed;
  top:0;
  left:0;
  right: 0;
  bottom: 0;
  background: url('../../../public/image/11.png');
  background-repeat: no-repeat;
  background-size: cover;

}
.login-box {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 400px;
  padding: 40px;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, .5);
  box-sizing: border-box;
  box-shadow: 0 15px 25px rgba(0, 0, 0, .6);
  border-radius: 10px;
}

.login-box h2 {
  margin: 0 0 30px;
  padding: 0;
  color: #fff;
  text-align: center;
  font-size: 20px;
}

.login-box .user-box {
  position: relative;
}

.login-box .user-box input {
  width: 100%;
  padding: 10px 0;
  font-size: 16px;
  color: #fff;
  margin-bottom: 30px;
  border: none;
  border-bottom: 1px solid #fff;
  outline: none;
  background: transparent;
}

.login-box .user-box label {
  position: absolute;
  top: 0;
  left: 0;
  padding: 10px 0;
  font-size: 16px;
  color: #fff;
  pointer-events: none;
  transition: .5s;
}

.login-box .user-box input:focus ~ label,
.login-box .user-box input:valid ~ label {
  top: -20px;
  left: 0;
  color: #03e9f4;
  font-size: 12px;
}

.login-box form a {
  position: relative;
  display: inline-block;
  padding: 10px 20px;
  color: #03e9f4;
  font-size: 16px;
  text-decoration: none;
  text-transform: uppercase;
  overflow: hidden;
  transition: .5s;
  margin-top: 40px;
  justify-content: space-between;
  letter-spacing: 4px

}
.login-box form a .a2{
  padding-left: 800px;
}
.login-box a:hover {
  background: #03e9f4;
  color: #fff;
  border-radius: 5px;
  box-shadow: 0 0 5px #03e9f4,
  0 0 25px #03e9f4,
  0 0 50px #03e9f4,
  0 0 100px #03e9f4;
}

.login-box a span {
  position: absolute;
  display: block;
}

.login-box a span:nth-child(1) {
  top: 0;
  left: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #03e9f4);
  animation: btn-anim1 1s linear infinite;
}

@keyframes btn-anim1 {
  0% {
    left: -100%;
  }
  50%, 100% {
    left: 100%;
  }
}

.login-box a span:nth-child(2) {
  top: -100%;
  right: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(180deg, transparent, #03e9f4);
  animation: btn-anim2 1s linear infinite;
  animation-delay: .25s
}

@keyframes btn-anim2 {
  0% {
    top: -100%;
  }
  50%, 100% {
    top: 100%;
  }
}

.login-box a span:nth-child(3) {
  bottom: 0;
  right: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(270deg, transparent, #03e9f4);
  animation: btn-anim3 1s linear infinite;
  animation-delay: .5s
}

@keyframes btn-anim3 {
  0% {
    right: -100%;
  }
  50%, 100% {
    right: 100%;
  }
}

.login-box a span:nth-child(4) {
  bottom: -100%;
  left: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(360deg, transparent, #03e9f4);
  animation: btn-anim4 1s linear infinite;
  animation-delay: .75s
}

@keyframes btn-anim4 {
  0% {
    bottom: -100%;
  }
  50%, 100% {
    bottom: 100%;
  }
}

.app-container{
  background-image: url("https://cdn.pixabay.com/photo/2021/10/23/17/54/background-6735519_1280.png") !important;
  width: 100%;
  /* height: 100vh; */
}
</style>
