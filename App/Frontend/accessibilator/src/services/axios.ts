import Axios, { AxiosResponse } from 'axios';

const axiosInit = Axios.create({
  baseURL: 'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com',
});

// Set the AUTH token for any request
axiosInit.interceptors.request.use(function (config) {
  return config;
});

export default axiosInit;
