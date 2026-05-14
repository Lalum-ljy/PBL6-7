import axios from 'axios';

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
  // withCredentials: true,  // 先关闭，避免跨域问题
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // 只有当不是FormData时才设置Content-Type为json
    if (!(config.data instanceof FormData)) {
      config.headers['Content-Type'] = 'application/json; charset=utf-8';
    }

    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    console.error('响应错误:', error);
    return Promise.reject(error);
  }
);

export default service;