import { FC } from 'react';
import { Switch, SwitchProps } from '@headlessui/react';

// Interface extending the standard SwitchProps from Headless UI
// with an additional ariaLabel property for accessibility.
interface MyToggleProps extends SwitchProps<any> {
  ariaLabel: string;
}

// Functional component MyToggle, utilizing the extended properties.
const MyToggle: FC<MyToggleProps> = ({ ariaLabel, ...toggleProps }) => {
  return (
    // The Switch component from Headless UI is used to create a toggle switch.
    // className defines the styling of the toggle switch, including dimensions,
    // background color, and rounded corners.
    <Switch
      className='relative inline-flex h-6 w-11 items-center rounded-full ui-checked:bg-green-700 ui-checked:bg-opacity-70 ui-not-checked:bg-stone-400'
      {...toggleProps}
    >
      {/* The span with 'sr-only' class is used for screen readers, making the toggle accessible.
          The ariaLabel prop provides the necessary descriptive label. */}
      <span className='sr-only'>{ariaLabel}</span>

      {/* This span represents the toggle button itself. It changes position and color based on the toggle state.
          The 'transform' and 'transition' classes enable smooth movement and transition effects. */}
      <span className='inline-block h-4 w-4 transform rounded-full transition ui-checked:!translate-x-6 ui-checked:!bg-slate-50 ui-not-checked:translate-x-1 ui-not-checked:bg-stone-500' />
    </Switch>
  );
};

// Export the MyToggle component for use in other parts of the application.
export default MyToggle;
