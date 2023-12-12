/**
 * This constant is used to check if the application is running in development mode.
 * It is a common practice to have different configurations or behaviors in development
 * versus production environments.
 *
 * Check the current environment mode.
 * process.env.NODE_ENV is an environment variable typically set in Node.js environments.
 * It holds the current state of the environment where the Node.js application is running.
 */

export const IS_DEV_MODE = process.env.NODE_ENV === 'development';
