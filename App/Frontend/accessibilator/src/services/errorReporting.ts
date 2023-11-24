import * as Sentry from '@sentry/nextjs';

export const reportException = (
  err: unknown,
  breadcrumb: Sentry.Breadcrumb
) => {
  Sentry.addBreadcrumb({
    level: 'error',
    ...breadcrumb,
  });

  Sentry.captureException(err);
};
