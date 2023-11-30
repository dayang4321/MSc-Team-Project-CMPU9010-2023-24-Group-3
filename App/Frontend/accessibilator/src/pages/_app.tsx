// These styles apply to every route in the application
import '../styles/global.css';
import type { AppProps } from 'next/app';
import { Lexend } from 'next/font/google';
import { defaultTheme, Provider } from '@adobe/react-spectrum';
import { ToastContainer } from '@react-spectrum/toast';
import Script from 'next/script';
import AuthProvider from '../contexts/AuthContext';

const lexend = Lexend({
  subsets: ['latin'],
  variable: '--font-lexend',
});

export default function App({ Component, pageProps }: AppProps) {
  return (
    <>
      <Provider theme={defaultTheme} colorScheme='light'>
        <div className={`${lexend.variable} font-sans`}>
          <ToastContainer />
          <AuthProvider>
            <Component {...pageProps} />
          </AuthProvider>
        </div>
      </Provider>
      <Script id='accessibilator_hotjar' strategy='afterInteractive'>
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
