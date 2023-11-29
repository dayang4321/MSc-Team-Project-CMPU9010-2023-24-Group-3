import { useContext, useEffect } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import { useRouter } from 'next/router';
import delay from 'lodash/delay';

const SignInPage = () => {
  const { isAuthenticated, setAuth, user } = useContext(AuthContext);

  const router = useRouter();
  const { token } = router.query;

  useEffect(() => {
    if (isAuthenticated) {
      alert('Sign in successful');

      delay(() => {
        router.push('/');
      }, 1000);

      return;
    } else {
      !!token &&
        setAuth({
          token: String(token),
        }).then((val) => {
          // console.log({ val });
          // console.log({ user, isAuthenticated }, 'redirect log');
        });
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, token]);

  return (
    <div
      style={{
        width: '100vw',
        height: '100vh',
        position: 'absolute',
        left: 0,
        top: 0,
        background: 'white',
      }}
    ></div>
  );
};

export default SignInPage;
