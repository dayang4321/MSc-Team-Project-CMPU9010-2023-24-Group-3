import React, { ReactNode } from 'react';
import BeatLoader from 'react-spinners/BeatLoader';
import { LoaderSizeMarginProps } from 'react-spinners/helpers/props';

// Define the properties structure for the Button component
export type BtnProps = {
  text: ReactNode;
  variant?: 'primary' | 'link';
  icon?: ReactNode;
  loading?: boolean;
  loaderProps?: LoaderSizeMarginProps;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;

export type Ref = any;

// Defining the Button component using forwardRef for passing the object by reference
const Button = React.forwardRef<Ref, BtnProps>(function Button(props, ref) {
  const {
    text,
    icon,
    variant,
    className,
    loading,
    disabled,
    loaderProps,
    ...btnProps
  } = props;

  // Determine if the button should be disabled
  const isDisabled = !!(loading || disabled);

  // Define the content of the button, either the text/icon or the loader
  let btnContent = (
    <>
      {icon && <span className={`inline-block ${text ? '' : ''}`}>{icon}</span>}
      <span>{text}</span>
    </>
  );

  // Replace button content with loader if in loading state
  if (!!loading) {
    btnContent = (
      <BeatLoader
        loading={loading}
        margin={'0.25rem'}
        color='#ffffff'
        size={'0.75rem'}
        {...loaderProps}
      />
    );
  }

  return (
    <button
      // Disable button when loading or explicitly disabled
      disabled={isDisabled || loading}
      className={`btn ${variant === 'link' ? 'btn-link' : 'btn-primary'} ${
        (loading || isDisabled) && 'pointer-events-none bg-opacity-60'
      } ${
        className || ''
      } transition-transform duration-150 active:scale-95 hover:scale-105`}
      ref={ref}
      {...btnProps}
    >
      {btnContent}
    </button>
  );
});

export default Button;
