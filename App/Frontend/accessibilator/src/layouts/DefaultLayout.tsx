import { useRouter } from 'next/router';
import Link from 'next/link';
import React, { FC, useContext, useState } from 'react';
import Button from '../components/UI/Button';
import FeedbackForm from '../components/FeedbackForm/FeedbackForm';
import { AuthContext } from '../contexts/AuthContext';
import { IS_DEV_MODE } from '../configs/configs';

interface LayoutProps {
  children: React.ReactNode;
  variant?: 'light' | 'dark';
  title?: React.ReactNode;
}

const redirectUrl = IS_DEV_MODE
  ? 'http://localhost:8080/oauth2/authorization/google?redirect_uri=http://localhost:3000/oauth2/redirect'
  : 'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com/oauth2/authorization/google?redirect_uri=https://dev.d3gfcwg1uu11c0.amplifyapp.com/oauth2/redirect';

const DefaultLayout: FC<LayoutProps> = ({ children, title, variant }) => {
  const [isShowingFeedback, setIsShowingFeedback] = useState(false);

  const { isAuthenticated, user } = useContext(AuthContext);

  return (
    <>
      <div className='flex min-h-screen flex-col'>
        <nav
          className={`relative flex px-8 shadow ${
            variant === 'dark' ? 'bg-stone-900 py-2 text-stone-100' : 'py-4'
          } `}
        >
          <Link className='text-3xl font-extrabold uppercase' href='/'>
            Accessibilator
          </Link>
          {title && (
            <p className='absolute left-1/2 top-1/2 inline-block -translate-x-1/2 -translate-y-1/2 text-lg font-medium'>
              {title}
            </p>
          )}
          {isAuthenticated ? (
            <Button
              role='navigation'
              variant='link'
              className={`ml-auto inline-block  px-6 py-2 text-base font-medium underline  ${
                variant === 'dark' ? 'text-stone-100' : 'text-stone-900'
              } `}
              text={'View Profile'}
              onClick={() => {}}
            />
          ) : (
            <Link
              className='btn-primary ml-auto'
              href={`${redirectUrl}`}
              target='_self'
            >
              Login
            </Link>
          )}

          <Button
            role='navigation'
            variant='link'
            className={`ml-6 inline-block  px-6 py-2 text-base font-medium underline  ${
              variant === 'dark' ? 'text-stone-100' : 'text-stone-900'
            } `}
            text={'Send Feedback'}
            onClick={() => {
              setIsShowingFeedback(true);
            }}
          />
        </nav>
        {children}
        <footer className='invisible p-5'>Footer</footer>
      </div>
      <FeedbackForm
        isShowing={isShowingFeedback}
        onFeedBackClose={() => {
          setIsShowingFeedback(false);
        }}
      />
    </>
  );
};

export default DefaultLayout;
