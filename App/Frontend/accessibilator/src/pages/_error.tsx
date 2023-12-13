// Importing Sentry from the '@sentry/nextjs' package for error tracking and monitoring.
import * as Sentry from '@sentry/nextjs';

// Importing the Error component from 'next/error' to handle and display errors.
import Error from 'next/error';

/**
 * CustomErrorComponent is a functional component that returns an Error component.
 * It is used to display custom error messages based on the status code.
 *
 * @param {Object} props - The props passed to the component.
 * @returns {JSX.Element} - The rendered Error component with the provided status code.
 */
const CustomErrorComponent = (props) => {
  /**
   * The Error component is rendered with the statusCode passed via props.
   * This statusCode is used to display the relevant error message to the user.
   */
  return <Error statusCode={props.statusCode} />;
};

export default CustomErrorComponent;
