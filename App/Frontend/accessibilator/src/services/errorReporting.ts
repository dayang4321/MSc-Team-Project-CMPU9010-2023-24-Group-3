import * as Sentry from '@sentry/nextjs';
import { isAxiosError } from 'axios';

export const reportException = (
  err: unknown,
  breadcrumb: Sentry.Breadcrumb
) => {
  Sentry.addBreadcrumb({
    level: 'error',
    ...breadcrumb,
    message: isAxiosError(err)
      ? `${breadcrumb?.message}: ${err?.response?.data?.detail || err?.message}`
      : breadcrumb.message,
  });

  Sentry.captureException(err);
};
