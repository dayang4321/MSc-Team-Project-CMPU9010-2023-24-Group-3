import { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import { useRouter } from 'next/router';
import delay from 'lodash/delay';
import axiosInit from '../../services/axios';
import { ToastQueue } from '@react-spectrum/toast';

/**
 * Defining the functional component named MagicLinkPage
 * @returns Informative toast messages which inform the user about authentication and authorization
 */
const MagicLinkPage = () => {
  const { isAuthenticated, isAuthLoading, setAuth } = useContext(AuthContext);
  const [isValidatingEmail, setIsValidatingEmail] = useState(true);

  const router = useRouter();

  // Async function to validate the magic email
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

  /**
   * Check if the user is authenticated or not
   * If yes, then re-route the user to the logged in perspective of the home page
   * If not, then set the authentication data for the user or make the user return to the home page.
   */
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
