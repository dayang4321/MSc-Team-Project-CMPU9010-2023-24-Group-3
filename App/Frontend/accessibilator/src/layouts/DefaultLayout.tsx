import { useRouter } from 'next/router';
import Link from 'next/link';
import React, { FC, useState } from 'react';
import Button from '../components/UI/Button';
import FeedbackForm from '../components/FeedbackForm/FeedbackForm';

interface LayoutProps {
  children: React.ReactNode;
  variant?: 'light' | 'dark';
}

const DefaultLayout: FC<LayoutProps> = ({ children, variant }) => {
  const router = useRouter();

  const [isShowingFeedback, setIsShowingFeedback] = useState(false);

  return (
    <>
      <div className="min-h-screen flex flex-col">
        <nav
          className={`px-8 flex  ${
            variant === 'dark' ? 'bg-stone-900 text-stone-100 py-2' : 'py-5'
          } `}
        >
          <Link
            className="text-2xl uppercase font-extrabold"
            href="/"
            // onClick={() => {
            //   router.push('/');
            // }}
          >
            Accessibilator
          </Link>
          <Button
            role="navigation"
            variant="link"
            className={`ml-auto inline-block  underline py-2 px-6 text-base font-medium  ${
              variant === 'dark' ? 'text-stone-100' : 'text-stone-900'
            } `}
            text={'Send Feedback'}
            onClick={() => {
              setIsShowingFeedback(true);
            }}
          />
        </nav>
        {children}
        <footer className="p-5">Footer</footer>
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
