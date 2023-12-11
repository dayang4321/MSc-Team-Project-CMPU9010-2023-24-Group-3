import { ReactNode } from 'react';
import { Label, Radio, RadioGroup } from 'react-aria-components';
import { MyRadioGroupProps } from './interfaces';

// Defining a Custom functional component called MyRadioGroup
export default function MyRadioGroup({
  label,
  options,
  ...props
}: MyRadioGroupProps) {
  // Render the radio group with a label and multiple radio options.
  return (
    <RadioGroup className='flex w-full flex-col gap-3' {...props}>
      <Label className=''>{label}</Label>

      <div className='flex gap-3'>
        {/* Mapping through each option to create a radio button. */}
        {options.map((option) => {
          // Returning a RadioOption component for each option.
          return (
            <RadioOption key={option.name} name={option.name}>
              {option.content}
            </RadioOption>
          );
        })}
      </div>
    </RadioGroup>
  );
}

// Defining the Subcomponent to render individual radio options
function RadioOption({
  name,
  children,
}: {
  name: string;
  children?: ReactNode;
}) {
  // Render a single radio button with custom styling.
  return (
    // The Radio component is styled based on different states like focus, selection, press, and disable.
    <Radio
      value={name}
      className={({ isFocusVisible, isSelected, isPressed, isDisabled }) => `
      group relative flex cursor-default rounded-[0.25rem] border border-solid bg-clip-padding px-2 py-2 
      ${
        // Apply styles when the radio button is focused.
        isFocusVisible
          ? 'ring-yellow-800-600 ring-2 ring-offset-1 ring-offset-slate-50/80'
          : ''
      }
      ${
        // Styles for when the radio button is selected and not disabled.
        isSelected && !isDisabled
          ? 'border-yellow-800/80 bg-yellow-600/30 text-yellow-800'
          : 'border-transparent'
      }
      // Styles for when the radio button is pressed but not selected.
      ${isPressed && !isSelected ? 'bg-yellow-600/10' : ''}
      // Default styles when the radio button is neither selected nor pressed.
      ${!isSelected && !isPressed ? 'bg-transparent' : ''}
      // Styles for disabled state.
      ${isDisabled ? 'bg-gray-200 text-gray-600' : ''}
    `}
    >
      {/* Container for radio button content with conditional styling. */}
      <div className='flex items-center text-slate-600 group-disabled:text-gray-500 group-selected:text-yellow-800 '>
        {children}
      </div>
    </Radio>
  );
}
