import React from 'react';
import { MyTextAreaProps } from './interfaces';
import get from 'lodash/get';

// Defining the functional React component named MyTextArea. It accepts the pre-defined MyTextAreaProps as its props.
const MyTextArea: React.FC<MyTextAreaProps> = (props) => {
  // Destructuring props to extract various properties.
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
          // Associates the label with the textarea using the 'id' prop.
          htmlFor={id}
          // Spreads additional label properties if provided.
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
          name={inputRef?.name || name} // Sets the name of the textarea, preferring inputRef.name if available.
          {...inputRef} // Spreads the ref object to the textarea, if provided.
          className={`form-control-textarea  ${className || ''}`} // Applies dynamic class names.
          {...textAreaProps} // Spreads additional textarea properties.
        />
        {children}{' '}
        {/* Renders any children components or elements passed to MyTextArea. */}
      </div>

      {/* Error message display, conditionally rendered if there are errors for this textarea's field. */}
      {get(errors, inputRef?.name || id) && (
        <small className='form-error ml-3'>
          {get(errors, inputRef?.name || id).message}{' '}
          {/* Displays the error message. */}
        </small>
      )}
    </div>
  );
};

export default MyTextArea; // Exports the MyTextArea component for use in other parts of the application.
