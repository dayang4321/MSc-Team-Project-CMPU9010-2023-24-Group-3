import { ReactNode } from 'react';
import {
  RadioGroupProps,
  SelectProps,
  SliderProps,
  ValidationResult,
} from 'react-aria-components';
import {
  DeepMap,
  FieldError,
  FieldValues,
  UseFormRegisterReturn,
} from 'react-hook-form';

/**
 * RHFInputProps: These are the props for a React Hook Form input component.
 *
 * @param name - Optional. The name of the input field which supports nested object keys.
 * @param label - Optional. The label text for the input field.
 * @param errors - Optional. Error messages for the field, typically passed from React Hook Form.
 * @param inputRef - Optional. Ref object from useFormRegister, used for registering the input in React Hook Form.
 * @param groupClasses - Optional. Additional CSS classes for the input group.
 * @param labelProps - Optional. Additional properties for the label element.
 */
interface RHFInputProps {
  name?: string | `${string}.${string}` | `${string}.${number}`;
  label?: string;
  errors?: DeepMap<FieldValues, FieldError>;
  inputRef?: UseFormRegisterReturn;
  groupClasses?: string;
  labelProps?: {
    [x: string]: string | object;
    className: string;
  };
}

/**
 * MyInputProps: Extended properties for a custom input component.
 *
 * @extends React.InputHTMLAttributes<HTMLInputElement> - Standard HTML input attributes.
 * @extends RHFInputProps - Props specific to React Hook Form.
 * @param id - Mandatory. Unique identifier for the input.
 */
export interface MyInputProps
  extends React.InputHTMLAttributes<HTMLInputElement>,
    RHFInputProps {
  id: string | `${string}.${string}` | `${string}.${number}`;
}

/**
 * MyTextAreaProps: Extended properties for a custom textarea component.
 *
 * @extends React.TextareaHTMLAttributes<HTMLTextAreaElement> - Standard HTML textarea attributes.
 * @extends RHFInputProps - Props specific to React Hook Form.
 * @param id - Mandatory. Unique identifier for the textarea.
 */
export interface MyTextAreaProps
  extends React.TextareaHTMLAttributes<HTMLTextAreaElement>,
    RHFInputProps {
  id: string | `${string}.${string}` | `${string}.${number}`;
}

/**
 * MySelectProps: Extended properties for a custom select component.
 *
 * @extends Omit<SelectProps<T>, 'children'> - Inherits from SelectProps but omits 'children'.
 * @param label - Optional. Label for the select component.
 * @param description - Optional. Description for the select component.
 * @param errorMessage - Optional. Error message or a function returning an error message.
 * @param items - Mandatory. Iterable of options for the select component.
 */
export interface MySelectProps<T extends object>
  extends Omit<SelectProps<T>, 'children'> {
  label?: ReactNode;
  description?: string;
  errorMessage?: string | ((validation: ValidationResult) => string);
  items: Iterable<T>;
}

/**
 * MySliderProps: Extended properties for a custom slider component.
 *
 * @extends SliderProps<T> - Standard properties for a slider component.
 * @param label - Optional. Label for the slider.
 * @param thumbLabels - Optional. Labels for the thumbs on the slider.
 * @param outputValFormat - Optional. Function to format the output value.
 */
export interface MySliderProps<T> extends SliderProps<T> {
  label?: ReactNode;
  thumbLabels?: string[];
  outputValFormat?: (val: number) => string;
}

/**
 * MyRadioGroupProps: Extended properties for a custom radio group component.
 *
 * @extends RadioGroupProps - Standard properties for a radio group component.
 * @param label - Mandatory. Label for the radio group.
 * @param options - Mandatory. Array of options for the radio buttons, each with a name and content.
 */
export interface MyRadioGroupProps extends RadioGroupProps {
  label: ReactNode;
  options: Array<{ name: string; content: ReactNode }>;
}
