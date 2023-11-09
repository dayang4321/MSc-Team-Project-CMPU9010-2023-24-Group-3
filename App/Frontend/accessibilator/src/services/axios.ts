import Axios, { AxiosResponse } from 'axios';
import { STORAGE_KEYS } from '../configs/constants';

const axiosInit = Axios.create({
  // baseURL: 'http://127.0.0.1:8080',
  baseURL: 'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com',
});

// Set the AUTH token for any request
axiosInit.interceptors.request.use(async function (config) {
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    const accessTokenRes = await Axios.get(
      'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com/getToken'
    );
    localStorage.setItem(STORAGE_KEYS.TOKEN, `${accessTokenRes.data}`);
    config.headers.Authorization = `Bearer ${accessTokenRes.data}`;
  }

  return config;
});

// Response interceptor for API calls
axiosInit.interceptors.response.use(
  (response) => {
    return response;
  },
  async function (error) {
    const originalRequest = error.config;
    if (error.response.status === 403 && !originalRequest._retry) {
      originalRequest._retry = true;
      const accessTokenRes = await Axios.get(
        'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com/getToken'
      );
      Axios.defaults.headers.common[
        'Authorization'
      ] = `Bearer ${accessTokenRes.data}`;
      localStorage.setItem(STORAGE_KEYS.TOKEN, `${accessTokenRes.data}`);
      return axiosInit(originalRequest);
    }
    return Promise.reject(error);
  }
);

export default axiosInit;
