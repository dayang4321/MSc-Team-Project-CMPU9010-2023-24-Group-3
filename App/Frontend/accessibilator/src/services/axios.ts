import Axios, { AxiosResponse } from 'axios';

const axiosInit = Axios.create({ baseURL: 'http://127.0.0.1:8080' });

// Set the AUTH token for any request
axiosInit.interceptors.request.use(function (config) {
  return config;
});

export default axiosInit;
