import service from './axios';

// 登录接口
export const login = (data) => {
  return service.post('/user/login', data);
};

// 注册接口
export const register = (data) => {
  return service.post('/user/register', data);
};

// 退出登录接口
export const logout = () => {
  return service.post('/logout');
};

// 获取用户信息接口
export const getUserInfo = () => {
  return service.get('/user/info');
};