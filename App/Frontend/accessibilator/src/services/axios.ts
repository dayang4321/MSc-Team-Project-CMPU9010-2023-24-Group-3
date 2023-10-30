import Axios, { AxiosResponse } from 'axios';

const axiosInit = Axios.create({
  baseURL: 'http://ec2-51-20-70-141.eu-north-1.compute.amazonaws.com:8080',
});

// Set the AUTH token for any request
axiosInit.interceptors.request.use(function (config) {
  return config;
});

export default axiosInit;
