import { useRouter } from 'next/router';
import Link from 'next/link';
import React, { FC, useState } from 'react';
import Button from '../components/UI/Button';
import FeedbackForm from '../components/FeedbackForm/FeedbackForm';

interface LayoutProps {
  children: React.ReactNode;
  variant?: 'light' | 'dark';
  title?: React.ReactNode;
}

const DefaultLayout: FC<LayoutProps> = ({ children, title, variant }) => {
  const [isShowingFeedback, setIsShowingFeedback] = useState(false);

  return (
    <>
      <div className='flex min-h-screen flex-col'>
        <nav
          className={`relative flex px-8 ${
            variant === 'dark' ? 'bg-stone-900 py-2 text-stone-100' : 'py-5'
          } `}
        >
          <Link className='text-2xl font-extrabold uppercase' href='/'>
            Accessibilator
          </Link>
          {title && (
            <p className='absolute left-1/2 top-1/2 inline-block -translate-x-1/2 -translate-y-1/2 text-lg font-medium'>
              {title}
            </p>
          )}

          <Button
            role='navigation'
            variant='link'
            className={`ml-auto inline-block  px-6 py-2 text-base font-medium underline  ${
              variant === 'dark' ? 'text-stone-100' : 'text-stone-900'
            } `}
            text={'Send Feedback'}
            onClick={() => {
              setIsShowingFeedback(true);
            }}
          />
        </nav>
        {children}
        <footer className='p-5'>Footer</footer>
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
