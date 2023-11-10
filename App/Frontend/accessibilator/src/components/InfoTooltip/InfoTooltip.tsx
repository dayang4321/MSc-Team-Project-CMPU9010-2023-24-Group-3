import React, { FC, PropsWithChildren } from 'react';
import InformationCircleIcon from '@heroicons/react/24/outline/InformationCircleIcon';

type InfoTooltipProps = PropsWithChildren<{
  infoTip: string;
}>;

const InfoTooltip: FC<InfoTooltipProps> = ({ children, infoTip }) => {
  return (
    <div className='relative inline-block'>
      <div className='tooltip absolute -right-6 -top-2' data-tip={infoTip}>
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
