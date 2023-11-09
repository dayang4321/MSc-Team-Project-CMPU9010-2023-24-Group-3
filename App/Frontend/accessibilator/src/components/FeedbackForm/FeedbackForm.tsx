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
      const feedbackRes = await axiosInit.post('/feedback', data, {
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
    <div className="w-full h-96 flex flex-col justify-center items-center">
      <HashLoader
        loading={true}
        color="#451a03"
        size={'120px'}
        aria-description="Submitting Feedback"
      />
      <p className="text-lg mt-6">Submitting Feedback...</p>
    </div>
  );

  const formContent = (
    <>
      {sendFeedbackSuccess ? (
        <div>
          <p className="text-xl mt-12 mb-6">We received your feedback</p>
          <p className="text-lg mb-10">Your feedback means alot to us🫡</p>
        </div>
      ) : (
        <>
          {' '}
          <form className="mt-6">
            <MyInput
              id="Email"
              label={'Email address (optional)'}
              inputRef={register('email', {
                required: false,
                pattern: {
                  value: /^\S+@\S+$/i,
                  message: 'Enter a valid email address',
                },
              })}
              autoFocus
              groupClasses="mb-4"
              errors={errors}
              className=""
              labelProps={{ className: '' }}
              autoComplete="email"
              defaultValue={''}
              placeholder="e.g jamie@gmail.com"
            ></MyInput>
            <MyTextArea
              id="whatUserLiked"
              label={'What did you like? (*)'}
              className=""
              groupClasses="mb-4"
              placeholder="Share your experience..."
              inputRef={register('whatUserLiked', {
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
              id="whatUserDisliked"
              label={'What can we do better? (*)'}
              className=""
              groupClasses="mb-4"
              placeholder="Share your experience..."
              inputRef={register('whatUserDisliked', {
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
              id="newFeatures"
              label={'Any new feature(s) you would like to see?'}
              className=""
              groupClasses="mb-0"
              placeholder="Share your experience..."
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
          <div className="p-1 flex justify-end mt-5 mb-2">
            <Button
              role="navigation"
              type="button"
              variant="link"
              className="text-yellow-950 border border-yellow-950 mr-10 py-2 px-6 text-base font-medium"
              text={'Cancel'}
              onClick={() => {
                onFeedBackClose();
              }}
            />
            <Button
              type="button"
              className="text-base py-2 px-6 text-zinc-50 font-medium"
              text={'Send'}
              onClick={() => {
                handleSubmit(submitHandler)();
              }}
            />
          </div>
        </>
      )}

      {sendFeedbackError && (
        <p className="text-red-800 bg-red-200 px-4 py-2 rounded-md text-lg mt-3">
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
