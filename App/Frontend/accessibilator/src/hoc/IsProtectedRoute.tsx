import React, { useContext, useEffect } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { useRouter } from 'next/router';

export default function IsProtectedRoute(Component: React.FC) {
  return function IsProtectedRoute(props: any) {
    const { isAuthenticated } = useContext(AuthContext);

    const router = useRouter();

    useEffect(() => {
      if (!isAuthenticated) {
        router.replace('/');
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isAuthenticated]);

    if (!isAuthenticated) {
      return null;
    }

    return <Component {...props} />;
  };
}
