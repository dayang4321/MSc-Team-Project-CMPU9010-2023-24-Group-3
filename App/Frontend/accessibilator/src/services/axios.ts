import Axios, { AxiosError, isAxiosError } from 'axios';
import { STORAGE_KEYS } from '../configs/constants';

const axiosInit = Axios.create({
  baseURL: 'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com',
});

const getTokenApi = async () => {
  try {
    const accessTokenRes = await Axios.get<{
      token: string;
      expiry: string;
    }>('https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com/getToken');

    return Promise.resolve(accessTokenRes);
  } catch (err) {
    return Promise.reject(err);
  }
};

// Set the AUTH token for any request
axiosInit.interceptors.request.use(async function (config) {
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
  const expiry = localStorage.getItem(STORAGE_KEYS.EXPIRY) || '0';

  const isTokenValid = Number(expiry) > Date.now();

  if (!!token && isTokenValid) {
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    try {
      const accessTokenRes = await getTokenApi();
      localStorage.setItem(STORAGE_KEYS.TOKEN, `${accessTokenRes.data.token}`);
      localStorage.setItem(
        STORAGE_KEYS.EXPIRY,
        `${Date.parse(accessTokenRes.data.expiry)}`
      );
      config.headers.Authorization = `Bearer ${accessTokenRes.data.token}`;
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
    if (isAxiosError(error)) {
      const originalRequest = { ...error.config, _retry: false };
      if (error?.response?.status === 403 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const accessTokenRes = await getTokenApi();
          console.log({ accessTokenRes });

          localStorage.setItem(
            STORAGE_KEYS.TOKEN,
            `${accessTokenRes.data.token}`
          );
          localStorage.setItem(
            STORAGE_KEYS.EXPIRY,
            `${Date.parse(accessTokenRes.data.expiry)}`
          );
          Axios.defaults.headers.common[
            'Authorization'
          ] = `Bearer ${accessTokenRes.data.token}`;
          return axiosInit.request(originalRequest);
        } catch (err) {
          //TODO: Toast error
          return Promise.reject(err);
        }
      } else {
        return Promise.reject(error);
      }
    } else {
      return Promise.reject(error);
    }
  }
);

export default axiosInit;
