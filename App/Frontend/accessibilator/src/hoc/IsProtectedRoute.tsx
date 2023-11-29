import React, { useContext, useEffect } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { redirect } from 'next/navigation';

export default function IsProtectedRoute(Component: React.FC) {
  return function IsProtectedRoute(props: any) {
    const { isAuthenticated } = useContext(AuthContext);

    useEffect(() => {
      if (!isAuthenticated) {
        return redirect('/');
      }
    }, [isAuthenticated]);

    if (!isAuthenticated) {
      return null;
    }

    return <Component {...props} />;
  };
}
