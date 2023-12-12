import Axios, { AxiosError, isAxiosError } from 'axios';
import { STORAGE_KEYS } from '../configs/constants';
import { IS_DEV_MODE } from '../configs/configs';

// Determine the API URL based on whether the application is in development mode.
const apiUrl = IS_DEV_MODE
  ? 'http://localhost:8080' // Use localhost for development.
  : 'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com'; // Use AWS API Endpoint URL for production.

// Initialize Axios with the base URL for API requests.
const axiosInit = Axios.create({
  baseURL: apiUrl,
});

// Function to get a new authentication token from the server.
const getTokenApi = async () => {
  try {
    // Attempt to get a new token.
    const accessTokenRes = await Axios.get<{
      token: string;
      expiry: string;
    }>(`${apiUrl}/auth/token`);

    return Promise.resolve(accessTokenRes);
  } catch (err) {
    // If there's an error, reject the promise with the error.
    return Promise.reject(err);
  }
};

/**
 * Axios request interceptor to set the Authorization header
 * Call the use function from the axios interceptors to set the authorization headers
 */
axiosInit.interceptors.request.use(async function (config) {
  // Retrieve the current token and its expiry from localStorage.
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
  const expiry = localStorage.getItem(STORAGE_KEYS.EXPIRY) || '0';

  // Check if the token is still valid.
  const isTokenValid = Number(expiry) > Date.now();

  if (!!token && isTokenValid) {
    // If the token is valid, set the Authorization header.
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    // If the token is not valid, request a new token.
    try {
      const accessTokenRes = await getTokenApi();
      // Store the new token and its expiry in localStorage.
      localStorage.setItem(STORAGE_KEYS.TOKEN, `${accessTokenRes.data.token}`);
      localStorage.setItem(
        STORAGE_KEYS.EXPIRY,
        `${Date.parse(accessTokenRes.data.expiry)}`
      );
      // Set the Authorization header with the new token.
      config.headers.Authorization = `Bearer ${accessTokenRes.data.token}`;
    } catch (error) {
      console.log(error);
      // TODO: Handle error, such as displaying a notification to the user.
    }
  }

  return config;
});

// Axios response interceptor for handling API responses and errors.
axiosInit.interceptors.response.use(
  (response) => {
    // If the response is successful, just return it.
    return response;
  },
  async function (error: Error | AxiosError) {
    // Handle Axios errors.
    if (isAxiosError(error)) {
      const originalRequest = { ...error.config, _retry: false };
      // If the error is a 403 (Forbidden), attempt to refresh the token.
      if (error?.response?.status === 403 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          // Request a new token.
          const accessTokenRes = await getTokenApi();

          // Update localStorage with the new token and expiry.
          localStorage.setItem(
            STORAGE_KEYS.TOKEN,
            `${accessTokenRes.data.token}`
          );
          localStorage.setItem(
            STORAGE_KEYS.EXPIRY,
            `${Date.parse(accessTokenRes.data.expiry)}`
          );
          // Update the Axios default header with the new token.
          Axios.defaults.headers.common[
            'Authorization'
          ] = `Bearer ${accessTokenRes.data.token}`;
          // Retry the original request with the new token.
          return axiosInit.request(originalRequest);
        } catch (err) {
          // TODO: Toast error
          // TODO: Handle error, such as displaying a notification to the user.
          return Promise.reject(err);
        }
      } else {
        // If the error is not a 403, or the request has already been retried, reject the promise.
        return Promise.reject(error);
      }
    } else {
      // Error handling for errors which are not generated from within Axios
      return Promise.reject(error);
    }
  }
);

// Export the customized Axios instance.
export default axiosInit;
