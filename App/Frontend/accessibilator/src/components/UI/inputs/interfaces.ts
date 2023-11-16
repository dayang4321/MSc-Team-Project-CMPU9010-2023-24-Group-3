import { SelectProps, ValidationResult } from 'react-aria-components';
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
  label?: string;
  description?: string;
  errorMessage?: string | ((validation: ValidationResult) => string);
  items: Iterable<T>;
}
