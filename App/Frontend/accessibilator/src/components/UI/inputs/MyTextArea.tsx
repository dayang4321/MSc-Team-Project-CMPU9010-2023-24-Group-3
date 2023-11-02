import React from 'react';
import { MyTextAreaProps } from './interfaces';
import get from 'lodash/get';

const MyTextArea: React.FC<MyTextAreaProps> = (props) => {
  const {
    id,
    errors,
    inputRef,
    label,
    groupClasses,
    labelProps,
    className,
    children,
    name,
    ...textAreaProps
  } = props;

  return (
    <div className={`input-group ${groupClasses || ''}`}>
      {label && (
        <label
          htmlFor={id}
          {...(labelProps && { ...labelProps })}
          className={`mb-2 block text-sm font-semibold ${
            labelProps?.className || ''
          }`}
        >
          {label}
        </label>
      )}
      <div className="relative">
        <textarea
          id={id}
          name={inputRef?.name || name}
          {...inputRef}
          className={`form-control-textarea  ${className || ''}`}
          {...textAreaProps}
        />
        {children}
      </div>

      {get(errors, inputRef?.name || id) && (
        <small className="form-error ml-3">
          {get(errors, inputRef?.name || id).message}
        </small>
      )}
    </div>
  );
};

export default MyTextArea;
