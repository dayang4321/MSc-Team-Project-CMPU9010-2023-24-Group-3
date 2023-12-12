import { useContext, useEffect } from 'react';
import { AuthContext } from '../../contexts/AuthContext';
import { useRouter } from 'next/router';
import delay from 'lodash/delay';
import { ToastQueue } from '@react-spectrum/toast';

// Defining the GoogleSignInPage functional component
const GoogleSignInPage = () => {
  // Use AuthContext to access authentication-related state and actions
  const { isAuthenticated, isAuthLoading, setAuth } = useContext(AuthContext);

  const router = useRouter();

  // Defining the useEffect hook to handle authentication status changes and routing the user back to the homepage if necessary
  useEffect(() => {
    // If the user is authenticated, show a success message and redirect to the homepage
    if (isAuthenticated) {
      delay(() => {
        ToastQueue.positive('Logged in successfully', {
          timeout: 2000,
        });
        router.push('/');
      }, 500);

      return;
    } else {
      // If there is a token in the query parameters, set the authentication state
      if (!!router.query.token && !!router.query.token) {
        setAuth({
          token: String(router.query.token),
          expiry: String(router.query.expiry),
        }).then((val) => {});
      } else {
        // If no token is present, show a failure message and redirect to the homepage
        ToastQueue.negative('Login failed, please try again', {
          timeout: 2000,
        });
        router.push('/');
      }
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, router.query.token]);

  // Render the sign-in page UI
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
      {/* Display messages based on the authentication status */}
      {isAuthenticated && 'Login successful'}
      {isAuthLoading && 'Logging in...'}
    </div>
  );
};

export default GoogleSignInPage;
