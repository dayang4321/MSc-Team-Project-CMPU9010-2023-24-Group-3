import React, { FC, useState } from 'react';
import MyModal from '../UI/MyModal';
import MyInput from '../UI/inputs/MyInput';
import { SubmitHandler, useForm } from 'react-hook-form';
import Button from '../UI/Button';
import axiosInit from '../../services/axios';
import HashLoader from 'react-spinners/HashLoader';
import delay from 'lodash/delay';

interface MagicEmailFormProps {
  isShowing: boolean;
  onFormClose: () => void;
}

interface MagicEmailFormFields {
  email: string;
}

const MagicEmailForm: FC<MagicEmailFormProps> = ({
  isShowing,
  onFormClose,
}) => {
  const {
    handleSubmit,
    register,
    reset,
    formState: { errors },
    getValues,
  } = useForm<MagicEmailFormFields>({
    reValidateMode: 'onChange',
    shouldUnregister: false,
  });

  const [isSubmitLoading, setIsSubmitLoading] = useState(false);
  const [sendMagicEmailError, setSendMagicEmailError] = useState<null | string>(
    ''
  );
  const [sendMagicEmailSuccess, setSendMagicEmailSuccess] =
    useState<boolean>(false);

  const submitHandler: SubmitHandler<MagicEmailFormFields> = async (data) => {
    setIsSubmitLoading(true);
    try {
      const feedbackRes = await axiosInit.post('/auth/login', data, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (feedbackRes.data) {
        setSendMagicEmailSuccess(true);
        setSendMagicEmailError(null);
      }
    } catch (error) {
      // TODO: Toast Error
      setSendMagicEmailSuccess(false);
      setSendMagicEmailError(
        `An Error Occurred, Please try again ${
          error?.message ? `(Message: ${error?.message})` : ''
        }`
      );
    } finally {
      delay(setIsSubmitLoading, 1000, false);
    }
  };

  const onModalClose = () => {
    reset();
    onFormClose();
    setSendMagicEmailSuccess(false);
    setSendMagicEmailError('');
  };

  const loader = (
    <div className='flex h-96 w-full flex-col items-center justify-center'>
      <HashLoader
        loading={true}
        color='#451a03'
        size={'120px'}
        aria-description='Sending Login Link'
      />
      <p className='mt-6 text-lg'>Sending Login Link...</p>
    </div>
  );

  const formContent = (
    <>
      {sendMagicEmailSuccess ? (
        <div>
          <p className='mb-6 mt-12 text-lg'>
            We have sent a link to{' '}
            <span className='font-semibold'>{getValues('email')}</span> to log
            you in!
          </p>
        </div>
      ) : (
        <>
          <p className='mt-6'>
            We&apos;ll send you a link to log you in automatically
          </p>
          <form className='mt-10'>
            <MyInput
              id='Email'
              label={'Email address (*)'}
              inputRef={register('email', {
                required: 'Enter an email address',
                pattern: {
                  value: /^\S+@\S+$/i,
                  message: 'Enter a valid email address',
                },
              })}
              autoFocus
              groupClasses='mb-4'
              errors={errors}
              className=''
              labelProps={{ className: '' }}
              autoComplete='email'
              defaultValue={''}
              placeholder='jamie@gmail.com'
            ></MyInput>
          </form>
          <div className='mb-2 mt-10 flex justify-end p-1'>
            <Button
              role='navigation'
              type='button'
              variant='link'
              className='mr-10 border border-yellow-950 px-6 py-2 text-base font-medium text-yellow-950'
              text={'Cancel'}
              onClick={() => {
                onFormClose();
              }}
            />
            <Button
              type='button'
              className='px-6 py-2 text-base font-medium text-zinc-50'
              text={'Send Link'}
              onClick={() => {
                handleSubmit(submitHandler)();
              }}
            />
          </div>
        </>
      )}

      {sendMagicEmailError && (
        <p className='mt-3 rounded-md bg-red-200 px-4 py-2 text-lg text-red-800'>
          {sendMagicEmailError}
        </p>
      )}
    </>
  );

  return (
    <MyModal
      isOpen={isShowing}
      onModalClose={onModalClose}
      title={
        isSubmitLoading
          ? ''
          : sendMagicEmailSuccess
          ? 'Check your email inbox!'
          : 'Log in with your email'
      }
    >
      {isSubmitLoading ? loader : formContent}
    </MyModal>
  );
};

export default MagicEmailForm;
