import { FC, useState } from 'react';
import { Switch, SwitchProps } from '@headlessui/react';

interface MyToggleProps extends SwitchProps<any> {}

const MyToggle: FC<MyToggleProps> = ({ ...toggleProps }) => {
  return (
    <Switch
      className='relative inline-flex h-6 w-11 items-center rounded-full ui-checked:bg-stone-500 ui-not-checked:bg-stone-400'
      {...toggleProps}
    >
      <span className='sr-only'>Enable notifications</span>
      <span className='inline-block h-4 w-4 transform rounded-full transition ui-checked:translate-x-6 ui-checked:bg-stone-900 ui-not-checked:translate-x-1 ui-not-checked:bg-stone-100' />
    </Switch>
  );
};

export default MyToggle;
