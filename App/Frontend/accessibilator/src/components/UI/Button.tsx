import React, { ReactNode } from 'react';
import BeatLoader from 'react-spinners/BeatLoader';
import { LoaderSizeMarginProps } from 'react-spinners/helpers/props';

export type BtnProps = {
  text: ReactNode;
  variant?: 'primary' | 'link';
  icon?: ReactNode;
  loading?: boolean;
  loaderProps?: LoaderSizeMarginProps;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;

export type Ref = any;

const Button = React.forwardRef<Ref, BtnProps>((props, ref) => {
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

  const isDisabled = !!(loading || disabled);

  let btnContent = (
    <>
      {icon && <span className={`inline-block ${text ? '' : ''}`}>{icon}</span>}
      <span>{text}</span>
    </>
  );

  if (!!loading) {
    btnContent = (
      <BeatLoader
        loading={loading}
        margin={'0.25rem'}
        color="#ffffff"
        size={'0.75rem'}
        {...loaderProps}
      />
    );
  }

  return (
    <button
      disabled={isDisabled || loading}
      className={`btn ${variant === 'link' ? 'btn-link' : 'btn-primary'} ${
        (loading || isDisabled) && 'pointer-events-none bg-opacity-60'
      } ${
        className || ''
      } transition-all duration-200 hover:scale-105 active:scale-95`}
      ref={ref}
      {...btnProps}
    >
      {btnContent}
    </button>
  );
});
export default Button;
