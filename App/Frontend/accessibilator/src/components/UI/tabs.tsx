import React, { FC } from 'react';
import { Tab, TabPanel } from 'react-aria-components';
import type { TabPanelProps, TabProps } from 'react-aria-components';

// Define the MyTab component, a custom tab component.
export const MyTab: FC<TabProps> = ({ ...props }) => {
  return (
    <Tab
      {...props}
      // Apply dynamic styling based on the `isSelected` state.
      className={({ isSelected }) => `
        w-full cursor-pointer py-2.5 text-center text-lg font-medium outline-none ring-yellow-700 transition-colors focus-visible:ring-2
        ${
          // If the tab is selected, apply these styles (border bottom, background color, text color).
          isSelected
            ? 'border-b-2 border-b-yellow-800 bg-white text-yellow-800'
            : // If the tab is not selected, apply hover and pressed styles.
              'hover:bg-yellow-600/10 pressed:bg-yellow-600/10'
        }
      `}
    />
  );
};

// Defining the MyTabPanel component, a custom tab panel component.
export const MyTabPanel: FC<TabPanelProps> = ({ ...props }) => {
  // The component returns a TabPanel with customized styles.
  return (
    <TabPanel
      className='mt-2 min-h-0 flex-1 overflow-auto p-0 pb-8 outline-none ring-yellow-700 focus-visible:ring-2'
      {...props}
    />
  );
};
