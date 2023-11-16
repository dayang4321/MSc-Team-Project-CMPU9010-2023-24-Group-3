import Axios, { AxiosError, isAxiosError } from 'axios';
import { STORAGE_KEYS } from '../configs/constants';

const axiosInit = Axios.create({
  baseURL: 'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com',
});

const getTokenApi = async () => {
  const accessTokenRes = Axios.get<{
    token: string;
    expiry: string;
  }>('https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com/getToken');
  return accessTokenRes;
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
    console.log({ genResponse: response });
    return response;
  },
  function (error: Error | AxiosError) {
    console.log({ genError: error });
    if (isAxiosError(error)) {
      const originalRequest = { ...error.config, _retry: false };
      if (error?.response.status === 403 && !originalRequest._retry) {
        originalRequest._retry = true;

        getTokenApi()
          .then((accessTokenRes) => {
            Axios.defaults.headers.common[
              'Authorization'
            ] = `Bearer ${accessTokenRes.data.token}`;
            localStorage.setItem(
              STORAGE_KEYS.TOKEN,
              `${accessTokenRes.data.token}`
            );
            localStorage.setItem(
              STORAGE_KEYS.EXPIRY,
              `${Date.parse(accessTokenRes.data.expiry)}`
            );
            const delayRetryRequest = new Promise<void>((resolve) => {
              setTimeout(() => {
                resolve();
              }, 500);
            });

            return delayRetryRequest.then(() =>
              axiosInit.request(originalRequest)
            );
          })
          .catch((err) => {
            console.log(err, 'get token err');
            //TODO: Toast error
            Promise.reject(err);
          });
      } else {
        return Promise.reject(error);
      }
    } else {
      return Promise.reject(error);
    }
  }
);

export default axiosInit;
