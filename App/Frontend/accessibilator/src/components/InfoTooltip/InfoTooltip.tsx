import React, { FC, PropsWithChildren } from 'react';
import InformationCircleIcon from '@heroicons/react/24/outline/InformationCircleIcon';

type InfoTooltipProps = PropsWithChildren<{
  infoTip: string;
  position?: string;
}>;

const InfoTooltip: FC<InfoTooltipProps> = ({ children, infoTip, position }) => {
  const positionClass = `daisy-tooltip-${position || 'right'}`;

  return (
    <div className='relative inline-block'>
      <div
        className={`daisy-tooltip ${positionClass} absolute -right-6 -top-2 z-40 inline-flex`}
        data-tip={infoTip}
      >
        <span className='inline-flex items-center justify-center'>
          <InformationCircleIcon
            className='h-5 w-5'
            aria-description='Show Tooltip'
          />
        </span>
      </div>
      {children}
    </div>
  );
};

export default InfoTooltip;
