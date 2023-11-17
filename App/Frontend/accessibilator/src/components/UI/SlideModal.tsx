import { Dispatch, Fragment, SetStateAction, useState } from 'react';
import { Dialog, Transition } from '@headlessui/react';
import { XMarkIcon } from '@heroicons/react/24/outline';
<svg
  xmlns='http://www.w3.org/2000/svg'
  fill='none'
  viewBox='0 0 24 24'
  strokeWidth={1.5}
  stroke='currentColor'
  className='h-6 w-6'
>
  <path strokeLinecap='round' strokeLinejoin='round' d='M6 18L18 6M6 6l12 12' />
</svg>;

interface SlideModalProps {
  open: boolean;
  setOpen: Dispatch<SetStateAction<boolean>>;
  title: string | JSX.Element | React.ReactNode;
  modalContentClasses?: string;
  children: React.ReactNode;
}

const SlideModal: React.FC<SlideModalProps> = (props) => {
  const { open, setOpen, title, modalContentClasses, children } = props;

  return (
    <Transition.Root show={open} as={Fragment}>
      <Dialog
        as='div'
        className='fixed inset-0 z-50 overflow-hidden'
        open={open}
        onClose={setOpen}
      >
        <div className='absolute inset-0 overflow-hidden'>
          <Transition.Child
            as={Fragment}
            enter='ease-in-out duration-500'
            enterFrom='opacity-0'
            enterTo='opacity-100'
            leave='ease-in-out duration-500'
            leaveFrom='opacity-100'
            leaveTo='opacity-0'
          >
            <Dialog.Overlay className='absolute inset-0 bg-gray-500 bg-opacity-75 transition-opacity' />
          </Transition.Child>
          <div className='pl:3 fixed inset-y-0 right-0 flex max-w-full pl-5 sm:pl-10'>
            <Transition.Child
              as={Fragment}
              enter='transform transition ease-in-out duration-500 sm:duration-700'
              enterFrom='translate-x-full'
              enterTo='translate-x-0'
              leave='transform transition ease-in-out duration-500 sm:duration-700'
              leaveFrom='translate-x-0'
              leaveTo='translate-x-full'
            >
              <div className='my-dialog relative w-[82vw] max-w-[34.375rem] overflow-y-scroll'>
                <Transition.Child
                  as={Fragment}
                  enter='ease-in-out duration-500'
                  enterFrom='opacity-0'
                  enterTo='opacity-100'
                  leave='ease-in-out duration-500'
                  leaveFrom='opacity-100'
                  leaveTo='opacity-0'
                >
                  <div className='absolute right-8 top-2 z-10  flex pr-2 pt-4 sm:-ml-10 sm:pr-4'>
                    <button
                      className='text-primary focus:ring-primary rounded-full bg-primary-50 p-1 focus:outline-none focus:ring-2 focus:ring-opacity-50'
                      onClick={() => setOpen(false)}
                    >
                      <span className='sr-only'>Close panel</span>
                      <XMarkIcon className='h-6 w-6 ' aria-hidden='true' />
                    </button>
                  </div>
                </Transition.Child>
                <div
                  className={`relative flex max-h-screen min-h-screen flex-1 flex-col overflow-hidden bg-white py-8 shadow-xl ${
                    modalContentClasses || ''
                  }`}
                >
                  <div className=''>
                    <Dialog.Title
                      as='h2'
                      className='px-16 font-sans text-2xl font-medium'
                    >
                      {title}
                    </Dialog.Title>
                  </div>
                  <div className='sm:text-md flex flex-1 flex-col overflow-y-hidden pt-8 font-sans text-base'>
                    {children}
                  </div>
                </div>
              </div>
            </Transition.Child>
          </div>
        </div>
      </Dialog>
    </Transition.Root>
  );
};

export default SlideModal;
