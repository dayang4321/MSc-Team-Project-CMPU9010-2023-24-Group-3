import { AxiosRequestConfig } from 'axios';
import { createContext, useCallback, useEffect, useState } from 'react';
import { STORAGE_KEYS } from '../configs/constants';
import axiosInit from '../services/axios';

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
  fetchUser: () => void;
  logout: () => void;
}

export const AuthContext = createContext<IAuthContext>({
  user: null,
  token: null,
  isAuthenticated: false,
  isAuthLoading: true,
  setAuth: async ({ token, expiry, user }): Promise<User | null> =>
    Promise.reject(null),
  fetchUser: () => {},
  logout: () => {},
});

const AuthProvider = ({ children }) => {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);

  const [isAuthLoading, setIsAuthLoading] = useState(true);

  const clearAuth = useCallback(() => {
    localStorage.removeItem(STORAGE_KEYS.USER);
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.EXPIRY);
    setToken(null);
    setUser(null);
  }, []);

  const fetchUser = useCallback(
    async (user: User | null = null, config?: AxiosRequestConfig) => {
      let currUser = user;

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

  // check if token and user exists in storage
  useEffect(() => {
    setIsAuthLoading(true);
    const storedToken = localStorage.getItem(STORAGE_KEYS.TOKEN);
    const expiry = localStorage.getItem(STORAGE_KEYS.EXPIRY) || '0';
    const storedUser = localStorage.getItem(STORAGE_KEYS.USER);

    const isTokenValid = Number(expiry) > Date.now();

    // If token is expired sign user out
    if (!isTokenValid) {
      clearAuth();
      setIsAuthLoading(false);
      return;
    } else if (isTokenValid && storedToken && storedUser) {
      setToken(storedToken);
      fetchUser(JSON.parse(storedUser));
      return;
    } else if (isTokenValid && storedToken && !storedUser) {
      setToken(storedToken);
      fetchUser()
        .then((user) => {
          if (!!user) {
            const { userId, email, username } = user;
            setUser({ userId, email, username });
          } else {
            setUser(null);
          }
          setIsAuthLoading(false);
        })
        .catch((err) => {
          clearAuth();
          setIsAuthLoading(false);
        });
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthLoading,
        isAuthenticated: !isAuthLoading && !!token && !!user,
        setAuth: async ({ token, expiry, user }) => {
          localStorage.setItem(STORAGE_KEYS.TOKEN, token);
          localStorage.setItem(STORAGE_KEYS.EXPIRY, `${Date.parse(expiry)}`);
          setToken(token);
          return await fetchUser(user);
        },
        fetchUser,
        logout: async () => {
          try {
            //  TODO: await userSignOutApi()
          } finally {
            clearAuth();
            //  TODO: TOAST MSG ('user logged out')
          }
        },
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
