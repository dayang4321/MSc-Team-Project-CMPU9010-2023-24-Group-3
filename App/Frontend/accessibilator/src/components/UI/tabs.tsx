import React, { FC } from 'react';
import { Tab, TabPanel } from 'react-aria-components';
import type { TabPanelProps, TabProps } from 'react-aria-components';

export const MyTab: FC<TabProps> = ({ ...props }) => {
  return (
    <Tab
      {...props}
      className={({ isSelected }) => `
        w-full cursor-pointer py-2.5 text-center text-lg font-medium outline-none ring-yellow-700 transition-colors focus-visible:ring-2
        ${
          isSelected
            ? 'border-b-2 border-b-yellow-800 bg-white text-yellow-800'
            : 'hover:bg-yellow-600/10 pressed:bg-yellow-600/10'
        }
      `}
    />
  );
};

export const MyTabPanel: FC<TabPanelProps> = ({ ...props }) => {
  return (
    <TabPanel
      className='mt-2 min-h-0 flex-1 overflow-auto p-0 pb-8 outline-none ring-yellow-700 focus-visible:ring-2'
      {...props}
    />
  );
};
