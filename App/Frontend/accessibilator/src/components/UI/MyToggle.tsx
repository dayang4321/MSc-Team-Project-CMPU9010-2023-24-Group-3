import { FC } from 'react';
import { Switch, SwitchProps } from '@headlessui/react';

interface MyToggleProps extends SwitchProps<any> {
  ariaLabel: string;
}

const MyToggle: FC<MyToggleProps> = ({ ariaLabel, ...toggleProps }) => {
  return (
    <Switch
      className='relative inline-flex h-6 w-11 items-center rounded-full ui-checked:bg-green-700 ui-checked:bg-opacity-70 ui-not-checked:bg-stone-400'
      {...toggleProps}
    >
      <span className='sr-only'>{ariaLabel}</span>
      <span className='inline-block h-4 w-4 transform rounded-full transition ui-checked:!translate-x-6 ui-checked:!bg-slate-50 ui-not-checked:translate-x-1 ui-not-checked:bg-stone-500' />
    </Switch>
  );
};

export default MyToggle;
