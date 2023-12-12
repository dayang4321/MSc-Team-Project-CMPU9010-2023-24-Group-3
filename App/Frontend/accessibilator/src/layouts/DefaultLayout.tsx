import { useRouter } from 'next/router';
import Link from 'next/link';
import React, { FC, Fragment, useContext, useState } from 'react';
import Button from '../components/UI/Button';
import FeedbackForm from '../components/FeedbackForm/FeedbackForm';
import { AuthContext } from '../contexts/AuthContext';
import { IS_DEV_MODE } from '../configs/configs';
import { Menu, Transition } from '@headlessui/react';
import Image from 'next/image';
import MagicEmailForm from '../components/MagicEmailForm/MagicEmailForm';
import { ToastQueue } from '@react-spectrum/toast';

// LayoutProps interface is defined to be used as the properties accepted by the DefaultLayout component
interface LayoutProps {
  children: React.ReactNode;
  variant?: 'regular' | 'slim';
  title?: React.ReactNode;
}

// Define the redirect URL for OAuth2 authentication based on whether the app is in development mode
const redirectUrl = IS_DEV_MODE
  ? 'http://localhost:8080/oauth2/authorization/google?redirect_uri=http://localhost:3000/oauth2/redirect'
  : 'https://hn6noz98uf.execute-api.eu-north-1.amazonaws.com/oauth2/authorization/google?redirect_uri=https://dev.d3gfcwg1uu11c0.amplifyapp.com/oauth2/redirect';

// DefaultLayout is a functional component that uses LayoutProps
const DefaultLayout: FC<LayoutProps> = ({ children, title, variant }) => {
  // AuthContext provides authentication related information
  const { isAuthenticated, user, logout } = useContext(AuthContext);
  // useState hooks for managing local state in the component
  const [isShowingFeedback, setIsShowingFeedback] = useState(false);
  const [isShowingLoginModal, setIsShowingLoginModal] = useState(false);

  const router = useRouter();

  return (
    <>
      <div className='flex min-h-screen flex-col'>
        <nav
          className={`relative flex px-8 shadow ${
            variant === 'slim' ? 'py-2' : 'py-4'
          } `}
        >
          {/* Link component for navigation */}
          <Link className='text-3xl font-extrabold uppercase' href='/'>
            Accessibilator
          </Link>
          {/* Conditional rendering of the title */}
          {title && (
            <p className='absolute left-1/2 top-1/2 inline-block -translate-x-1/2 -translate-y-1/2 text-lg font-medium'>
              {title}
            </p>
          )}
          {/* Conditional rendering based on authentication status */}
          {isAuthenticated ? (
            // User menu for authenticated users
            <Menu as='div' className='relative ml-auto'>
              <div>
                <Menu.Button className='flex rounded-full border-2 border-primary-400 text-sm focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-primary-200'>
                  <span className='sr-only'>Toggle user menu</span>
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
              {/** Render this Transition Layout component once the user is authenticated */}
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
                      href={'/user/presets'}
                      className='block bg-white px-4 py-2 text-sm text-gray-800 hover:bg-primary-100'
                    >
                      My Settings
                    </Link>
                  </Menu.Item>
                  <Menu.Item>
                    <Link
                      href={'/user/documents'}
                      className='block bg-white px-4 py-2 text-sm text-gray-800 hover:bg-primary-100'
                    >
                      Past Documents
                    </Link>
                  </Menu.Item>
                  <Menu.Item
                    as='button'
                    onClick={() => {
                      logout();
                      router.push('/');
                      ToastQueue.neutral('User logged out', {
                        timeout: 2000,
                      });
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
            // Render the Login menu for the users who are not authenticated
            <Menu as='div' className='relative ml-auto'>
              <div>
                <Menu.Button className='btn-primary'>
                  <span className='sr-only'>Toggle login menu</span>
                  Login
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
                    <button
                      onClick={() => {
                        setIsShowingLoginModal(true);
                      }}
                      className='block w-full bg-white px-4 py-2 text-left text-sm text-gray-800 hover:bg-primary-100'
                    >
                      Login with email
                    </button>
                  </Menu.Item>

                  {/** Conditionally render this component only when the application is in development mode */}
                  {IS_DEV_MODE && (
                    <Menu.Item>
                      <Link
                        className='block bg-white px-4 py-2 text-sm text-gray-800 hover:bg-primary-100'
                        href={`${redirectUrl}`}
                        target='_self'
                      >
                        Continue with google
                      </Link>
                    </Menu.Item>
                  )}
                </Menu.Items>
              </Transition>
            </Menu>
          )}
          {/* Display the Send Feedback button */}
          <Button
            role='navigation'
            variant='link'
            className='ml-6 inline-block  px-6 py-2 text-base font-medium text-stone-900 underline'
            text={'Send Feedback'}
            onClick={() => {
              setIsShowingFeedback(true);
            }}
          />
        </nav>
        {children}
        {/* Render the Footer element (currently invisible) */}
        <footer className='invisible p-5'>Footer</footer>
      </div>
      {/* Render the FeedbackForm component */}
      <FeedbackForm
        isShowing={isShowingFeedback}
        onFeedBackClose={() => {
          setIsShowingFeedback(false);
        }}
      />
      {/* Render the MagicEmailForm component */}
      <MagicEmailForm
        isShowing={isShowingLoginModal}
        onFormClose={() => {
          setIsShowingLoginModal(false);
        }}
      />
    </>
  );
};

export default DefaultLayout;
