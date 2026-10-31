import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import './plugins/element.js'
import 'normalize.css'
import '@/assets/css/base.css'
import ElementUI from 'element-ui'
import VueRouter from 'vue-router'
import { Message } from 'element-ui'
import axios from "axios";

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.prototype.$bus = new Vue();

axios.defaults.baseURL = '/api'
axios.interceptors.request.use(config => { const token=sessionStorage.getItem('token'); if(token){config.headers.Authorization='Bearer '+token;config.headers.token=token;} return config; })
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
