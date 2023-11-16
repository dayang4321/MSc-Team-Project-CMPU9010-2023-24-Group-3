import { ReactNode } from 'react';
import { Label, Radio, RadioGroup } from 'react-aria-components';
import { MyRadioGroupProps } from './interfaces';

export default function MyRadioGroup({
  label,
  options,
  ...props
}: MyRadioGroupProps) {
  return (
    <RadioGroup className='flex w-full flex-col gap-3' {...props}>
      <Label className=''>{label}</Label>

      <div className='flex gap-3'>
        {options.map((option) => {
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

function RadioOption({
  name,
  children,
}: {
  name: string;
  children?: ReactNode;
}) {
  return (
    <Radio
      value={name}
      className={({ isFocusVisible, isSelected, isPressed }) => `
      group relative flex cursor-default rounded-[0.25rem] border border-solid bg-clip-padding px-2 py-2 
      ${
        isFocusVisible
          ? 'ring-yellow-800-600 ring-2 ring-offset-1 ring-offset-slate-50/80'
          : ''
      }
      ${
        isSelected
          ? 'border-yellow-800/80 bg-yellow-600/30 text-yellow-800'
          : 'border-transparent'
      }
      ${isPressed && !isSelected ? 'bg-yellow-600/10' : ''}
      ${!isSelected && !isPressed ? 'bg-transparent' : ''}
    `}
    >
      <div className='group-selected:text-yellow-800 flex items-center text-slate-600'>
        {children}
      </div>
    </Radio>
  );
}
