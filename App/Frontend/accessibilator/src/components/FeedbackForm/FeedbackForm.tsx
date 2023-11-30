import React, { FC, useState } from 'react';
import MyModal from '../UI/MyModal';
import MyInput from '../UI/inputs/MyInput';
import { SubmitHandler, useForm } from 'react-hook-form';
import MyTextArea from '../UI/inputs/MyTextArea';
import Button from '../UI/Button';
import axiosInit from '../../services/axios';
import HashLoader from 'react-spinners/HashLoader';
import delay from 'lodash/delay';

interface FeedbackFormProps {
  isShowing: boolean;
  onFeedBackClose: () => void;
}

interface FeedbackFormFields {
  email: string;
  whatUserLiked: string;
  whatUserDisliked: string;
  newFeatures: string;
}

const FeedbackForm: FC<FeedbackFormProps> = ({
  isShowing,
  onFeedBackClose,
}) => {
  const {
    handleSubmit,
    register,
    reset,
    formState: { errors },
  } = useForm<FeedbackFormFields>({
    reValidateMode: 'onChange',
    shouldUnregister: false,
  });

  const [isSubmitLoading, setIsSubmitLoading] = useState(false);
  const [sendFeedbackError, setSendFeedbackError] = useState<null | string>('');
  const [sendFeedbackSuccess, setSendFeedbackSuccess] =
    useState<boolean>(false);

  const submitHandler: SubmitHandler<FeedbackFormFields> = async (data) => {
    setIsSubmitLoading(true);
    try {
      const feedbackRes = await axiosInit.post('/api/feedback/save', data, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (feedbackRes.data) {
        setSendFeedbackSuccess(true);
        setSendFeedbackError(null);
        reset();
      }
    } catch (error) {
      // TODO: Toast Error
      setSendFeedbackSuccess(false);
      setSendFeedbackError(
        `An Error Occurred, Please try again ${
          error?.message ? `(Message: ${error?.message})` : ''
        }`
      );
    } finally {
      delay(setIsSubmitLoading, 1000, false);
    }
  };

  const onModalClose = () => {
    setSendFeedbackSuccess(false);
    setSendFeedbackError('');
    onFeedBackClose();
  };

  const loader = (
    <div className='flex h-96 w-full flex-col items-center justify-center'>
      <HashLoader
        loading={true}
        color='#451a03'
        size={'120px'}
        aria-description='Submitting Feedback'
      />
      <p className='mt-6 text-lg'>Submitting Feedback...</p>
    </div>
  );

  const formContent = (
    <>
      {sendFeedbackSuccess ? (
        <div>
          <p className='mb-6 mt-12 text-xl'>We received your feedback</p>
          <p className='mb-10 text-lg'>Your feedback means alot to us🫡</p>
        </div>
      ) : (
        <>
          {' '}
          <form className='mt-6'>
            <MyInput
              id='Email'
              label={'Email address (optional)'}
              inputRef={register('email', {
                required: false,
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
              placeholder='e.g jamie@gmail.com'
            ></MyInput>
            <MyTextArea
              id='whatUserLiked'
              label={'What did you like? (*)'}
              className=''
              groupClasses='mb-4'
              placeholder='Share your experience...'
              inputRef={register('whatUserLiked', {
                required: true,
                minLength: {
                  value: 10,
                  message: 'Tell us a bit more',
                },
                maxLength: {
                  value: 400,
                  message: 'In fewer words maybe? (Max 400 chars.)',
                },
              })}
              defaultValue={''}
              errors={errors}
              rows={4}
            />
            <MyTextArea
              id='whatUserDisliked'
              label={"What did'nt you like? (*)"}
              className=''
              groupClasses='mb-4'
              placeholder='Share your experience...'
              inputRef={register('whatUserDisliked', {
                required: true,
                minLength: {
                  value: 10,
                  message: 'Tell us a bit more',
                },
                maxLength: {
                  value: 400,
                  message: 'In fewer words maybe? (Max 400 chars.)',
                },
              })}
              defaultValue={''}
              errors={errors}
              rows={4}
            />
            <MyTextArea
              id='newFeatures'
              label={'Any new feature(s) you would like to see?'}
              className=''
              groupClasses='mb-0'
              placeholder='Share your experience...'
              inputRef={register('newFeatures', {
                minLength: {
                  value: 10,
                  message: 'Tell us a bit more',
                },
                maxLength: {
                  value: 400,
                  message: 'In fewer words maybe? (Max 500 chars.)',
                },
              })}
              defaultValue={''}
              errors={errors}
              rows={4}
            />
          </form>
          <div className='mb-2 mt-5 flex justify-end p-1'>
            <Button
              role='navigation'
              type='button'
              variant='link'
              className='mr-10 border border-yellow-950 px-6 py-2 text-base font-medium text-yellow-950'
              text={'Cancel'}
              onClick={() => {
                onFeedBackClose();
              }}
            />
            <Button
              type='button'
              className='px-6 py-2 text-base font-medium text-zinc-50'
              text={'Send'}
              onClick={() => {
                handleSubmit(submitHandler)();
              }}
            />
          </div>
        </>
      )}

      {sendFeedbackError && (
        <p className='mt-3 rounded-md bg-red-200 px-4 py-2 text-lg text-red-800'>
          {sendFeedbackError}
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
          : sendFeedbackSuccess
          ? 'Thank you!'
          : 'Send us your feedback'
      }
    >
      {isSubmitLoading ? loader : formContent}
    </MyModal>
  );
};

export default FeedbackForm;
