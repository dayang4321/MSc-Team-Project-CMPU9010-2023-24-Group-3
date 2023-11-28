import { AxiosRequestConfig } from 'axios';
import { useRouter } from 'next/router';
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
    user,
  }: {
    token: string;
    user?: User;
  }) => Promise<User | null>;
  setUser: (user: User) => void;
  fetchUser: () => void;
  logout: () => void;
}

export const AuthContext = createContext<IAuthContext>({
  user: null,
  token: null,
  isAuthenticated: false,
  isAuthLoading: true,
  setAuth: async ({ token, user }): Promise<User | null> =>
    Promise.reject(null),
  setUser: (user) => {},
  fetchUser: () => {},
  logout: () => {},
});

const AuthProvider = ({ children }) => {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);

  const [isAuthLoading, setIsAuthLoading] = useState(true);

  const router = useRouter();

  const clearAuth = useCallback(() => {
    localStorage.removeItem(STORAGE_KEYS.USER);
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    setToken(null);
    setUser(null);
    //return router.replace('/')
  }, []);

  const fetchUser = useCallback(
    async (user: User | null = null, config?: AxiosRequestConfig) => {
      let currUser = user;

      if (!currUser) {
        try {
          const fetchUserRes = await axiosInit.get<User>('/api/user/me', {
            params: {
              ...config?.params,
            },
            ...config,
          });

          currUser = fetchUserRes.data;
          localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(currUser));
          setUser(currUser);
          setIsAuthLoading(false);
          return Promise.resolve(currUser);
        } catch (error) {
          // if (error === 'Access Denied') {
          //   clearAuth();
          // }
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

  //before first paint, check if token exists in storage
  useEffect(() => {
    setIsAuthLoading(true);
    const storedToken = localStorage.getItem(STORAGE_KEYS.TOKEN);
    !!storedToken ? setToken(storedToken) : setIsAuthLoading(false);
  }, [router.route]);

  //if token is found, get the user assigned to found token
  useEffect(() => {
    if (!!token) {
      fetchUser().finally(() => {
        setIsAuthLoading(false);
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token, router.route]);

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthLoading,
        isAuthenticated: !isAuthLoading && !!token && !!user,
        setAuth: async ({ token, user }) => {
          localStorage.setItem(STORAGE_KEYS.TOKEN, token);
          setToken(token);
          return await fetchUser(user);
        },
        setUser,
        fetchUser,
        logout: async () => {
          try {
            //  await userSignOutApi()
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
