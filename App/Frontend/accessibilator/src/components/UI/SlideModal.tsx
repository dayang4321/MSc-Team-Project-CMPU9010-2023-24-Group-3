import { Dispatch, Fragment, SetStateAction, useState } from 'react';
import { Dialog, Transition } from '@headlessui/react';
import { XMarkIcon } from '@heroicons/react/24/outline';
<svg
  xmlns="http://www.w3.org/2000/svg"
  fill="none"
  viewBox="0 0 24 24"
  strokeWidth={1.5}
  stroke="currentColor"
  className="w-6 h-6"
>
  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
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
        as="div"
        className="fixed inset-0 overflow-hidden z-50"
        open={open}
        onClose={setOpen}
      >
        <div className="absolute inset-0 overflow-hidden">
          <Transition.Child
            as={Fragment}
            enter="ease-in-out duration-500"
            enterFrom="opacity-0"
            enterTo="opacity-100"
            leave="ease-in-out duration-500"
            leaveFrom="opacity-100"
            leaveTo="opacity-0"
          >
            <Dialog.Overlay className="absolute inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
          </Transition.Child>
          <div className="fixed inset-y-0 right-0 pl-5 pl:3 sm:pl-10 max-w-full flex">
            <Transition.Child
              as={Fragment}
              enter="transform transition ease-in-out duration-500 sm:duration-700"
              enterFrom="translate-x-full"
              enterTo="translate-x-0"
              leave="transform transition ease-in-out duration-500 sm:duration-700"
              leaveFrom="translate-x-0"
              leaveTo="translate-x-full"
            >
              <div className="my-dialog relative w-[82vw] max-w-[34.375rem] overflow-y-scroll">
                <Transition.Child
                  as={Fragment}
                  enter="ease-in-out duration-500"
                  enterFrom="opacity-0"
                  enterTo="opacity-100"
                  leave="ease-in-out duration-500"
                  leaveFrom="opacity-100"
                  leaveTo="opacity-0"
                >
                  <div className="absolute z-10 top-4 right-8  pt-4 pr-2 flex sm:-ml-10 sm:pr-4">
                    <button
                      className="p-1 rounded-full bg-primary-50 text-primary focus:outline-none focus:ring-2 focus:ring-primary focus:ring-opacity-50"
                      onClick={() => setOpen(false)}
                    >
                      <span className="sr-only">Close panel</span>
                      <XMarkIcon className="h-6 w-6 " aria-hidden="true" />
                    </button>
                  </div>
                </Transition.Child>
                <div
                  className={`relative min-h-screen flex flex-col flex-1 p-14 sm:p-16 bg-white shadow-xl ${
                    modalContentClasses || ''
                  }`}
                >
                  <div className="">
                    <Dialog.Title
                      as="h2"
                      className="font-semibold font-sans text-2xl px-1"
                    >
                      {title}
                    </Dialog.Title>
                  </div>
                  <div className="pt-8 font-sans px-1 text-base sm:text-sm flex-1 flex flex-col overflow-y-hidden">
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
