import { AxiosRequestConfig } from 'axios';
import { createContext, useCallback, useEffect, useState } from 'react';
import { STORAGE_KEYS } from '../configs/constants';
import axiosInit from '../services/axios';

// Define the interface shape for the authentication context
interface IAuthContext {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isAuthLoading: boolean;
  setAuth: ({
    token,
    expiry,
    user,
  }: {
    token: string;
    expiry: string;
    user?: User;
  }) => Promise<User | null>;
  fetchUser: (
    user?: User | null,
    config?: AxiosRequestConfig
  ) => Promise<User | null>;
  logout: () => void;
}

// Create the authentication context with default values and type as IAuthContext
export const AuthContext = createContext<IAuthContext>({
  user: null,
  token: null,
  isAuthenticated: false,
  isAuthLoading: true,
  setAuth: async ({ token, expiry, user }): Promise<User | null> =>
    Promise.reject(null),
  fetchUser: (user?: User | null, config?: AxiosRequestConfig) =>
    Promise.resolve(null),
  logout: () => {},
});

// Define the component for Authentication Provider
const AuthProvider = ({ children }) => {
  // State for the auth token, user, and loading state
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [isAuthLoading, setIsAuthLoading] = useState(true);

  // Function to clear authentication data from the localStorage and component state
  const clearAuth = useCallback(() => {
    localStorage.removeItem(STORAGE_KEYS.USER);
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.EXPIRY);
    setToken(null);
    setUser(null);
  }, []);

  // Define the function to fetch the user data
  const fetchUser = useCallback(
    async (user: User | null = null, config?: AxiosRequestConfig) => {
      let currUser = user;
      // If the user data is not provided, fetch it
      if (!currUser) {
        setIsAuthLoading(true);
        try {
          const fetchUserRes = await axiosInit.get<{ user: User | null }>(
            '/api/user/me',
            {
              params: {
                ...config?.params,
              },
              ...config,
            }
          );

          currUser = fetchUserRes.data.user;
          !!currUser &&
            localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(currUser));
          setUser(currUser);
          setIsAuthLoading(false);
          return Promise.resolve(currUser);
        } catch (error) {
          setIsAuthLoading(false);
          return Promise.reject(null);
        }
      } else {
        localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(currUser));
        setUser(currUser);
        return Promise.resolve(currUser);
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    []
  );

  // Check if token and user exist in the storage on component mount
  useEffect(() => {
    setIsAuthLoading(true);
    const storedToken = localStorage.getItem(STORAGE_KEYS.TOKEN);
    const expiry = localStorage.getItem(STORAGE_KEYS.EXPIRY) || '0';
    const storedUser = localStorage.getItem(STORAGE_KEYS.USER);

    const isTokenValid = Number(expiry) > Date.now();

    // If token is expired sign user out and clear the authentication data
    if (!isTokenValid) {
      clearAuth();
      setIsAuthLoading(false);
      return;
    } else if (isTokenValid && storedToken && storedUser) {
      // If the token and the user data are valid, set them in state
      setToken(storedToken);
      fetchUser(JSON.parse(storedUser));
      setIsAuthLoading(false);
      return;
    } else if (isTokenValid && storedToken && !storedUser) {
      // If only token is valid, fetch the user data
      setToken(storedToken);
      fetchUser()
        .then((user) => {
          // Handle successful user data fetching
          if (!!user) {
            const { userId, email, username } = user;
            setUser({ userId, email, username });
          } else {
            setUser(null);
          }
          setIsAuthLoading(false);
        })
        .catch((err) => {
          // Error handling while fetching user data
          clearAuth();
          setIsAuthLoading(false);
        });
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Provide the authentication context to child components
  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthLoading,
        isAuthenticated: !isAuthLoading && !!token && !!user,
        setAuth: async ({ token, expiry, user }) => {
          // Setting the authentication data
          localStorage.setItem(STORAGE_KEYS.TOKEN, token);
          localStorage.setItem(STORAGE_KEYS.EXPIRY, `${Date.parse(expiry)}`);
          setToken(token);
          return await fetchUser(user);
        },
        fetchUser: fetchUser,
        logout: async () => {
          // Async function to handle the user logout
          try {
            // TODO: Implement user sign out API call
          } finally {
            clearAuth();
            // TODO: Display logout toast message
          }
        },
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
