import { Dialog, Transition } from '@headlessui/react';
import { FC, Fragment, PropsWithChildren, ReactNode } from 'react';
import { XMarkIcon } from '@heroicons/react/24/outline';
interface MyModalProps {
  title: ReactNode;
  isOpen: boolean;
  onModalClose: () => void;
}

const MyModal: FC<PropsWithChildren<MyModalProps>> = ({
  title,
  isOpen,
  onModalClose,
  children,
}) => {
  return (
    <>
      <Transition appear show={isOpen} as={Fragment}>
        <Dialog as='div' static className='relative z-10' onClose={() => {}}>
          <Transition.Child
            as={Fragment}
            enter='ease-out duration-300'
            enterFrom='opacity-0'
            enterTo='opacity-100'
            leave='ease-in duration-200'
            leaveFrom='opacity-100'
            leaveTo='opacity-0'
          >
            <div className='fixed inset-0 bg-black/25' />
          </Transition.Child>

          <div className='fixed inset-0 overflow-y-auto'>
            <div className='flex min-h-full items-center justify-center p-4 text-center'>
              <Transition.Child
                as={Fragment}
                enter='ease-out duration-300'
                enterFrom='opacity-0 scale-95'
                enterTo='opacity-100 scale-100'
                leave='ease-in duration-200'
                leaveFrom='opacity-100 scale-100'
                leaveTo='opacity-0 scale-95'
              >
                <Dialog.Panel className='relative w-full max-w-2xl transform overflow-hidden rounded-2xl bg-white p-8 pb-6 pt-10 text-left align-middle shadow-xl transition-all'>
                  <div className='absolute right-8 top-4 z-10  flex pr-2 pt-4 sm:-ml-10 sm:pr-4'>
                    <button
                      className='rounded-full bg-transparent p-1 text-stone-900 focus:outline-none focus:ring-2 focus:ring-yellow-600 focus:ring-opacity-50'
                      onClick={onModalClose}
                    >
                      <span className='sr-only'>Close panel</span>
                      <XMarkIcon className='h-6 w-6 ' aria-hidden='true' />
                    </button>
                  </div>
                  <Dialog.Title
                    as='h3'
                    className='text-3xl font-medium leading-6 text-gray-900'
                  >
                    {title}
                  </Dialog.Title>

                  <div className='mt-2'>{children}</div>
                </Dialog.Panel>
              </Transition.Child>
            </div>
          </div>
        </Dialog>
      </Transition>
    </>
  );
};

export default MyModal;
