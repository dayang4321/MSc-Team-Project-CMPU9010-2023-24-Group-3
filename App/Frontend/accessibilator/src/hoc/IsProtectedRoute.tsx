import React, { useContext, useEffect } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { useRouter } from 'next/router';

// To protect the routes based on the current authentication status
export default function IsProtectedRoute(Component: React.FC) {
  return function IsProtectedRoute(props: any) {
    // Using AuthContext to track and access the authentication state
    const { isAuthenticated, isAuthLoading } = useContext(AuthContext);
    const router = useRouter();

    // useEffect hook to constantly observe changes in authentication state
    useEffect(() => {
      // If the user is not authenticated and not in the loading state, redirect to the homepage.
      if (!isAuthenticated && !isAuthLoading) {
        router.replace('/');
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isAuthenticated, isAuthLoading]);

    // Render nothing if authentication is in the loading state and the user is not authenticated
    if (!isAuthenticated && isAuthLoading) {
      return null;
    }

    // Render the wrapped Component if the user is authenticated or not in the loading state.
    return <Component {...props} />;
  };
}
