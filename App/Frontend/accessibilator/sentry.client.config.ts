import * as Sentry from '@sentry/nextjs';
import { IS_DEV_MODE } from './src/configs/configs';
import { isAxiosError } from 'axios';
import { ExtraErrorData } from '@sentry/integrations';

Sentry.init({
  dsn: 'https://8087ab730cec649fc6344eb2a569ec0e@o4506276416520192.ingest.sentry.io/4506276426547200',

  // Adjust this value in production, or use tracesSampler for greater control
  tracesSampleRate: 1,

  // Setting this option to true will print useful information to the console while you're setting up Sentry.
  debug: IS_DEV_MODE ? true : false,

  replaysOnErrorSampleRate: 1.0,

  // This sets the sample rate to be 10%. You may want this to be 100% while
  // in development and sample at a lower rate in production
  replaysSessionSampleRate: 0.1,

  // You can remove this option if you're not planning to use the Sentry Session Replay feature:
  integrations: [
    new Sentry.Replay({
      // Additional Replay configuration goes in here, for example:
      maskAllText: true,
      blockAllMedia: true,
    }),
    new ExtraErrorData({ depth: 10 }),
  ],

  environment: IS_DEV_MODE ? 'development' : 'production',
  beforeSend: function (event, hint) {
    const exception = hint.originalException;

    if (!!exception && isAxiosError(exception)) {
      event.fingerprint = [
        '{{ default }}',
        String(exception?.response?.status),
        String(exception?.response?.data?.detail || exception.message),
        String(exception?.config?.url),
      ];
    }

    return event;
  },
});
