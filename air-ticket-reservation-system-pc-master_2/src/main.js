// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
// 引入element UI
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App';
// 引入路由
import router from './router';

// 引入icon
import './assets/icon/iconfont.css'

// 引入echarts
import echarts from 'echarts'
Vue.prototype.$echarts = echarts

import axios from 'axios';
// 修改API基础URL - 确保与后端端口一致
axios.defaults.baseURL = 'http://localhost:8080';

// 设置请求超时时间
axios.defaults.timeout = 10000;

// 设置请求头
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.headers.common['Accept'] = 'application/json';

// 允许携带cookie
axios.defaults.withCredentials = true;

// 添加请求拦截器
axios.interceptors.request.use(
  config => {
    // 从sessionStorage获取token
    const token = sessionStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    console.error("请求拦截器错误", error);
    return Promise.reject(error);
  }
);

// 添加响应拦截器
axios.interceptors.response.use(
  response => {
    // 检查是否需要清除token
    const clearToken = response.headers['clear-token'];
    if (clearToken === 'true') {
      sessionStorage.removeItem('token');
    }
    return response;
  },
  error => {
    // 处理错误响应
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          sessionStorage.removeItem('token');
          router.push('/login');
          break;
        case 403:
          // 权限不足
          console.error('权限不足');
          break;
        case 404:
          // 请求的资源不存在
          console.error('请求的资源不存在');
          break;
        default:
          console.error('请求失败:', error.response.data);
      }
    }
    return Promise.reject(error);
  }
);

Vue.prototype.$axios = axios;
Vue.prototype.pathURL='http://localhost:8080'
Vue.config.productionTip = false;

// 使用element UI
Vue.use(ElementUI);
// 过滤器

// 初始化系统公告
import initAnnouncements from './store/announcementInit';
initAnnouncements();

/* eslint-disable no-new */
new Vue({
    el: '#app',
    router,
    components: { App },
    template: '<App/>',
    data: {
        // 空的实例放到根组件下，所有的子组件都能调用
        Bus: new Vue()
    }
})