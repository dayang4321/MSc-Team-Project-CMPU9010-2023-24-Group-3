// These styles apply to every route in the application
import '../styles/global.css';
import type { AppProps } from 'next/app';
import { Lexend } from 'next/font/google';
import { defaultTheme, Provider } from '@adobe/react-spectrum';
import { ToastContainer } from '@react-spectrum/toast';

const lexend = Lexend({
  subsets: ['latin'],
  variable: '--font-lexend',
});

export default function App({ Component, pageProps }: AppProps) {
  return (
    <Provider theme={defaultTheme} colorScheme='light'>
      <div className={`${lexend.variable} font-sans`}>
        <ToastContainer />
        <Component {...pageProps} />
      </div>
    </Provider>
  );
}
