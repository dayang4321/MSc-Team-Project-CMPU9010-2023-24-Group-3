import { useRouter } from 'next/router';
import Link from 'next/link';
import React, { FC, Fragment, useContext, useState } from 'react';
import Button from '../components/UI/Button';
import FeedbackForm from '../components/FeedbackForm/FeedbackForm';
import { AuthContext } from '../contexts/AuthContext';
import { IS_DEV_MODE } from '../configs/configs';
import { Menu, Transition } from '@headlessui/react';
import Image from 'next/image';

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

  const { isAuthenticated, user, logout } = useContext(AuthContext);

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
          {IS_DEV_MODE ? (
            isAuthenticated ? (
              <Menu as='div' className='relative ml-auto'>
                <div>
                  <Menu.Button className='flex rounded-full border-2 border-primary-400 text-sm focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-primary-200'>
                    <span className='sr-only'>Open user menu</span>
                    <div className='inline-block h-9 w-9 overflow-hidden rounded-full'>
                      <Image
                        width={64}
                        height={64}
                        src={`https://ui-avatars.com/api/?name=${user?.email}&size=64&font-size=0.62&length=1&bold=true&background=431407&color=fafafa`}
                        className='rounded-full '
                        alt='profile name initials'
                      />
                    </div>
                  </Menu.Button>
                </div>
                <Transition
                  as={Fragment}
                  enter='transition ease-out duration-100'
                  enterFrom='transform opacity-0 scale-95'
                  enterTo='transform opacity-100 scale-100'
                  leave='transition ease-in duration-75'
                  leaveFrom='transform opacity-100 scale-100'
                  leaveTo='transform opacity-0 scale-95'
                >
                  <Menu.Items className='absolute right-0 z-[200] mt-2 w-48 origin-top-right divide-y-2 rounded-md bg-white py-1 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none'>
                    <Menu.Item>
                      <Link
                        href={'/profile'}
                        className='block bg-white px-4 py-2 text-sm text-gray-800 hover:bg-primary-100'
                      >
                        Your Presets
                      </Link>
                    </Menu.Item>
                    <Menu.Item
                      as='button'
                      onClick={() => {
                        logout();
                        //router.push('/');
                      }}
                      className={
                        'w-full bg-white px-4 py-2 text-left text-sm text-gray-800 file:block hover:bg-primary-100'
                      }
                    >
                      Log Out
                    </Menu.Item>
                  </Menu.Items>
                </Transition>
              </Menu>
            ) : (
              <Link
                className='btn-primary ml-auto'
                href={`${redirectUrl}`}
                target='_self'
              >
                Login
              </Link>
            )
          ) : null}

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
