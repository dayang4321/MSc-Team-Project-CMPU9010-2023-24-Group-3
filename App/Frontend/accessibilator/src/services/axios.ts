import Axios, { AxiosError, isAxiosError } from 'axios';
import { STORAGE_KEYS } from '../configs/constants';

const axiosInit = Axios.create({
  baseURL: 'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com',
});

const getTokenApi = async () => {
  const accessTokenRes = Axios.get(
    'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com/getToken'
  );
  return accessTokenRes;
};

// Set the AUTH token for any request
axiosInit.interceptors.request.use(async function (config) {
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    try {
      const accessTokenRes = await getTokenApi();
      localStorage.setItem(STORAGE_KEYS.TOKEN, `${accessTokenRes.data}`);
      config.headers.Authorization = `Bearer ${accessTokenRes.data}`;
    } catch (error) {
      console.log(error);
      //TODO: Toast error
    }
  }

  return config;
});

// Response interceptor for API calls
axiosInit.interceptors.response.use(
  (response) => {
    return response;
  },
  async function (error: Error | AxiosError) {
    console.log({ genericError: error });

    if (isAxiosError(error)) {
      const originalRequest = { ...error.config, _retry: undefined };
      if (error?.response.status === 403 && !originalRequest._retry) {
        originalRequest._retry = true;
        try {
          const accessTokenRes = await getTokenApi();
          Axios.defaults.headers.common[
            'Authorization'
          ] = `Bearer ${accessTokenRes.data}`;
          localStorage.setItem(STORAGE_KEYS.TOKEN, `${accessTokenRes.data}`);
          return axiosInit(originalRequest);
        } catch (error) {
          console.log(error);
          //TODO: Toast error
          Promise.reject(error);
        }
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInit;
