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

export interface MyInputProps
  extends React.InputHTMLAttributes<HTMLInputElement>,
    RHFInputProps {
  id: string | `${string}.${string}` | `${string}.${number}`;
}

export interface MyTextAreaProps
  extends React.TextareaHTMLAttributes<HTMLTextAreaElement>,
    RHFInputProps {
  id: string | `${string}.${string}` | `${string}.${number}`;
}

export interface MySelectProps<T extends object>
  extends Omit<SelectProps<T>, 'children'> {
  label?: ReactNode;
  description?: string;
  errorMessage?: string | ((validation: ValidationResult) => string);
  items: Iterable<T>;
}

export interface MySliderProps<T> extends SliderProps<T> {
  label?: ReactNode;
  thumbLabels?: string[];
  outputValFormat?: (val: number) => string;
}
export interface MyRadioGroupProps extends RadioGroupProps {
  label: ReactNode;
  options: Array<{ name: string; content: ReactNode }>;
}
