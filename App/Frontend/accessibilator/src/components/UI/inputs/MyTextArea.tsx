import React from 'react';
import { MyTextAreaProps } from './interfaces';
import get from 'lodash/get';

/**
 * Defining the functional React component named MyTextArea.
 * It accepts the pre-defined MyTextAreaProps as its props.
 */

/**
 * MyTextArea functional component
 * @param props: Extends the properties from MyTextAreaProps
 * @returns MyTextArea component
 */
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

  // Returning the JSX code of the component.
  return (
    <div className={`input-group ${groupClasses || ''}`}>
      {/* Conditional rendering for label. It is rendered only if 'label' prop is provided. */}
      {label && (
        <label
          // Associates the label with the textarea using the 'id' prop
          htmlFor={id}
          // Extends the label's properties if provided
          {...(labelProps && { ...labelProps })}
          className={`mb-2 block text-sm font-medium ${
            labelProps?.className || ''
          }`}
        >
          {label}
        </label>
      )}
      <div className='relative'>
        <textarea
          id={id}
          name={inputRef?.name || name}
          {...inputRef}
          className={`form-control-textarea  ${className || ''}`}
          {...textAreaProps}
        />
        {/* Renders any children components or elements passed to MyTextArea. */}
        {children}{' '}
      </div>

      {/* Error handling code to display the message, conditionally rendered if there are errors for this textarea's field. */}
      {get(errors, inputRef?.name || id) && (
        <small className='form-error ml-3'>
          {get(errors, inputRef?.name || id).message}{' '}
        </small>
      )}
    </div>
  );
};

export default MyTextArea;
