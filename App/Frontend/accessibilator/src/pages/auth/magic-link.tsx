import { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import { useRouter } from 'next/router';
import delay from 'lodash/delay';
import axiosInit from '../../services/axios';
import { ToastQueue } from '@react-spectrum/toast';

const MagicLinkPage = () => {
  const { isAuthenticated, isAuthLoading, setAuth } = useContext(AuthContext);
  const [isValidatingEmail, setIsValidatingEmail] = useState(true);

  const router = useRouter();

  const validateMagicEmail = async (email: string, magicToken: string) => {
    setIsValidatingEmail(true);
    try {
      const tokenRes = await axiosInit.get<{ token: string; expiry: string }>(
        '/auth/validate',
        {
          params: {
            email,
            token: magicToken,
          },
        }
      );

      setIsValidatingEmail(false);
      return Promise.resolve(tokenRes);
    } catch (error) {
      setIsValidatingEmail(false);
      return Promise.reject(error);
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      delay(() => {
        ToastQueue.positive('Logged in successfully', {
          timeout: 2000,
        });
        router.push('/');
      }, 250);
      return;
    } else {
      if (!!router.query.token && !!router.query.email) {
        validateMagicEmail(
          String(router.query.email),
          String(router.query.token)
        ).then((tokenRes) => {
          setAuth({
            token: tokenRes.data.token,
            expiry: tokenRes.data.expiry,
          })
            .then(() => {})
            .catch((err) => {
              delay(() => {
                ToastQueue.negative('Login failed, Please try again', {
                  timeout: 2000,
                });
                router.push('/');
              }, 250);
            });
        });
      } else {
        router.push('/');
        return;
      }
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, router.query.token]);

  return (
    <div className='absolute left-0 top-0 flex h-screen w-screen flex-col items-center justify-center bg-white text-2xl'>
      {isAuthenticated && 'Login successful'}
      {isAuthLoading && 'Logging in...'}
      {isValidatingEmail && 'Validating Magic Link...'}
    </div>
  );
};

export default MagicLinkPage;
