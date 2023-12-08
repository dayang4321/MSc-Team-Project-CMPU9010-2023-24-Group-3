import { useContext, useEffect } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import { useRouter } from 'next/router';
import delay from 'lodash/delay';

const SignInPage = () => {
  const { isAuthenticated, isAuthLoading, setAuth } = useContext(AuthContext);

  const router = useRouter();

  useEffect(() => {
    if (isAuthenticated) {
      delay(() => {
        // TODO: Toast sign in success
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
        // TODO: Toast sign in failure
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

export default SignInPage;
