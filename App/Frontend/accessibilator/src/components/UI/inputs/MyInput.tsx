import React from 'react';
import { MyInputProps } from './interfaces';
import get from 'lodash/get';

// Defining a functional component as MyInput. It is designed to be a reusable input field.
const MyInput: React.FC<MyInputProps> = (props) => {
  // Destructuring props to extract various properties for use in the component.
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
    ...inputProps
  } = props;

  return (
    // Creating a div with a dynamic class name to act as the input group container.
    <div className={`input-group ${groupClasses || ''}`}>
      {/* Conditionally rendering a label if one is provided. */}
      {label && (
        <label
          htmlFor={id}
          // Spreading labelProps to allow for custom properties on the label if custom properties have been applied on the label
          {...(labelProps && { ...labelProps })}
          className={`mb-2 block text-sm font-medium ${
            labelProps?.className || ''
          }`}
        >
          {label}
        </label>
      )}
      {/* Wrapping the input element in a div for styling and positioning. */}
      <div className='relative'>
        <input
          type={'text'}
          id={id}
          name={inputRef?.name || name}
          // Spreading inputRef to bind the input to form data if provided.
          {...inputRef}
          className={`form-control-input ${className || ''}`}
          // Spreading additional inputProps for custom functionality.
          {...inputProps}
        />
        {children}
      </div>

      {/* Displaying error messages dynamically based on the `errors` prop. */}
      {get(errors, inputRef?.name || id) && (
        <small className='form-error ml-3'>
          {get(errors, inputRef?.name || id).message}
        </small>
      )}
    </div>
  );
};

export default MyInput;
