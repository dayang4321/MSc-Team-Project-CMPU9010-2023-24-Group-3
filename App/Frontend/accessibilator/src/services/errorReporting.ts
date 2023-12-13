import * as Sentry from '@sentry/nextjs';
import { isAxiosError } from 'axios';

/**
 * This function is used to report exceptions to Sentry.
 * It takes an error object and a breadcrumb for tracking the error's context.
 *
 * @param err - The error object to be reported.
 * @param breadcrumb - A Sentry breadcrumb object providing context about the error.
 */
export const reportException = (
  // 'err' is an error object that could be of any type
  err: unknown,
  // 'breadcrumb' provides context about the error
  breadcrumb: Sentry.Breadcrumb
) => {
  // Adding a breadcrumb to Sentry. Breadcrumbs are used to record events that led to an error.
  Sentry.addBreadcrumb({
    // Setting the severity level of the breadcrumb to 'error'
    level: 'error',
    // Spreading the provided breadcrumb object for context
    ...breadcrumb,
    message: isAxiosError(err)
      ? // If the error is an Axios error, modify the message to include specific details from the Axios error response
        `${breadcrumb?.message}: ${err?.response?.data?.detail || err?.message}`
      : // If not an Axios error, use the original breadcrumb message
        breadcrumb.message,
  });

  // Capturing the exception with Sentry for monitoring and alerting
  Sentry.captureException(err);
};
