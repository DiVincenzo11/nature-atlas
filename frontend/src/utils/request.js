import axios from 'axios'
import { Message } from 'element-ui';
import store from '@/store';
import router from '@/router';

// 创建一个axios实例对象，取名为instance
const instance = axios.create({
    // 后端api所处域名
    baseURL: '/api',
    // 指定请求超时的毫秒数。
    timeout: 60000,
    // 选项包括: 'arraybuffer', 'document', 'json', 'text', 'stream'；浏览器专属：'blob'
    responseType: 'json', // 默认值
});

// 为instance对象添加请求拦截器，每个请求都会优先来到此处被拦截做处理后，再发起http请求
instance.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 为instance对象添加响应拦截器，每个响应都会优先来到此处被拦截做处理后，再返回http响应数据
instance.interceptors.response.use(function (response) {
    // http响应码为200时，细致地抽取出服务端返回的api数据
    if (200 == response.status) {
        return response.data;
    }
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    const code = error.response?.status
    if(code === 401){
        Message.error("token已过期，请重新登录")
        // 清空本地存储
        sessionStorage.removeItem('userinfo')
        sessionStorage.removeItem('token')
        store.commit('logout')
        // 跳转登录页面
        router.push({path: '/login'})
    }
    return Promise.reject(error);
});

//  把instance对象暴露出去，供外部使用； 使用default导出整个js文件对象
export default instance
