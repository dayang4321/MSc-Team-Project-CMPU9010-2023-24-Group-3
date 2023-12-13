// Importing global styles and necessary modules from various libraries
import '../styles/global.css';
import type { AppProps } from 'next/app';
import { Lexend } from 'next/font/google';
import { defaultTheme, Provider } from '@adobe/react-spectrum';
import { ToastContainer } from '@react-spectrum/toast';
import Script from 'next/script';
import AuthProvider from '../contexts/AuthContext';

// Configuration for Lexend font
const lexend = Lexend({
  subsets: ['latin'], // Specifying the font subset to be used
  variable: '--font-lexend', // Defining a CSS variable for the font
});

// Defining the main App component
export default function App({ Component, pageProps }: AppProps) {
  return (
    <>
      {/* Adobe React Spectrum Provider wraps the application, setting the theme and color scheme */}
      <Provider theme={defaultTheme} colorScheme='light'>
        {/* Applying the Lexend font and setting base font style */}
        <div className={`${lexend.variable} font-sans`}>
          {/* ToastContainer for displaying notifications */}
          <ToastContainer />
          {/* AuthProvider wraps the component to provide authentication context */}
          <AuthProvider>
            <Component {...pageProps} />
          </AuthProvider>
        </div>
      </Provider>
      {/* Script for Hotjar analytics integration */}
      <Script id='accessibilator_hotjar' strategy='afterInteractive'>
        {/* Inline script for configuring and initializing Hotjar */}
        {`(function(h,o,t,j,a,r){
        h.hj=h.hj||function(){(h.hj.q=h.hj.q||[]).push(arguments)};
        h._hjSettings={hjid:3758626,hjsv:6};
        a=o.getElementsByTagName('head')[0];
        r=o.createElement('script');r.async=1;
        r.src=t+h._hjSettings.hjid+j+h._hjSettings.hjsv;
        a.appendChild(r); })(window,document,'https://static.hotjar.com/c/hotjar-','.js?sv=');`}
      </Script>
    </>
  );
}
