import * as Sentry from '@sentry/nextjs';
import Error from 'next/error';

const CustomErrorComponent = (props) => {
  return <Error statusCode={props.statusCode} />;
};

export default CustomErrorComponent;
