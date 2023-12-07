import React, { useContext, useEffect } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { useRouter } from 'next/router';

export default function IsProtectedRoute(Component: React.FC) {
  return function IsProtectedRoute(props: any) {
    const { isAuthenticated, isAuthLoading } = useContext(AuthContext);

    const router = useRouter();

    useEffect(() => {
      if (!isAuthenticated && !isAuthLoading) {
        router.replace('/');
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isAuthenticated, isAuthLoading]);

    if (!isAuthenticated && isAuthLoading) {
      return null;
    }

    return <Component {...props} />;
  };
}
