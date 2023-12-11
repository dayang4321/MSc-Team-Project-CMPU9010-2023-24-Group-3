import { useContext, useEffect } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import { useRouter } from 'next/router';
import delay from 'lodash/delay';
import { ToastQueue } from '@react-spectrum/toast';

const GoogleSignInPage = () => {
  const { isAuthenticated, isAuthLoading, setAuth } = useContext(AuthContext);

  const router = useRouter();

  useEffect(() => {
    if (isAuthenticated) {
      delay(() => {
        ToastQueue.positive('Logged in successfully', {
          timeout: 2000,
        });
        router.push('/');
      }, 500);

      return;
    } else {
      if (!!router.query.token && !!router.query.token) {
        setAuth({
          token: String(router.query.token),
          expiry: String(router.query.expiry),
        }).then((val) => {});
      } else {
        ToastQueue.negative('Login failed, please try again', {
          timeout: 2000,
        });
        router.push('/');
      }
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, router.query.token]);

  return (
    <div
      style={{
        width: '100vw',
        height: '100vh',
        position: 'absolute',
        left: 0,
        top: 0,
        background: 'white',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        fontSize: 24,
      }}
    >
      {isAuthenticated && 'Login successful'}
      {isAuthLoading && 'Logging in...'}
    </div>
  );
};

export default GoogleSignInPage;
