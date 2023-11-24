import * as Sentry from '@sentry/nextjs';

Sentry.init({
  dsn: 'https://8087ab730cec649fc6344eb2a569ec0e@o4506276416520192.ingest.sentry.io/4506276426547200',

  // Adjust this value in production, or use tracesSampler for greater control
  tracesSampleRate: 1,

  // Setting this option to true will print useful information to the console while you're setting up Sentry.
  debug: false,
});
