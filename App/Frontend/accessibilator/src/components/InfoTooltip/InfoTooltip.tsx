import React, { FC, PropsWithChildren } from 'react';
import InformationCircleIcon from '@heroicons/react/24/outline/InformationCircleIcon';

// Type definition for the InfoTooltip component's props
type InfoTooltipProps = PropsWithChildren<{
  infoTip: string;
  position?: string;
}>;

// Defining the InfoTooltip component
const InfoTooltip: FC<InfoTooltipProps> = ({ children, infoTip, position }) => {
  // Default class for tooltip position, can be overridden based on the 'position' prop
  let positionClass = `daisy-tooltip-${position || 'right'}`;

  // Switch statement to determine the positionClass based on the 'position' prop
  switch (position) {
    case 'left':
      positionClass = 'daisy-tooltip-left';
      break;
    case 'right':
      positionClass = 'daisy-tooltip-right';
      break;
    case 'top':
      positionClass = 'daisy-tooltip-top';
      break;
    case 'bottom':
      positionClass = 'daisy-tooltip-bottom';
      break;

    default:
      positionClass = 'daisy-tooltip-right';
      break;
  }

  // Render the tooltip component
  return (
    <div className='relative inline-block'>
      {/* Tooltip container with dynamic positioning */}
      <div
        className={`daisy-tooltip ${positionClass} absolute -right-6 -top-2 z-40 inline-flex`}
        data-tip={infoTip}
      >
        <span className='inline-flex items-center justify-center'>
          {/* Tooltip icon */}
          <InformationCircleIcon
            className='h-5 w-5'
            aria-description='Show Tooltip'
          />
        </span>
      </div>
      {/* Children elements wrapped by the tooltip */}
      {children}
    </div>
  );
};

export default InfoTooltip;
