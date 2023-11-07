import React, { FC } from 'react';
import MyModal from '../UI/MyModal';
import MyInput from '../UI/inputs/MyInput';
import { SubmitHandler, useForm } from 'react-hook-form';
import MyTextArea from '../UI/inputs/MyTextArea';
import Button from '../UI/Button';

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
    setValue: setFormValue,
    formState: { errors },
  } = useForm<FeedbackFormFields>({
    reValidateMode: 'onChange',
    shouldUnregister: false,
  });

  const submitHandler: SubmitHandler<FeedbackFormFields> = async (data) => {
    const { email, whatUserLiked, whatUserDisliked, newFeatures } = data;
  };

  return (
    <MyModal
      isOpen={isShowing}
      onModalClose={onFeedBackClose}
      title={'Send us your feedback'}
    >
      <div className="mt-6">
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
      </div>

      <div className="p-1 flex justify-end mt-5 mb-2">
        <Button
          role="navigation"
          variant="link"
          className="text-yellow-950 border border-yellow-950 mr-10 py-2 px-6 text-base font-medium"
          text={'Cancel'}
          onClick={() => {}}
        />
        <Button
          className="text-base py-2 px-6 text-zinc-50 font-medium"
          text={'Send'}
          onClick={() => {}}
        />
      </div>
    </MyModal>
  );
};

export default FeedbackForm;
