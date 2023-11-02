import React, { FC } from 'react';
import MyModal from '../UI/MyModal';
import MyInput from '../UI/inputs/MyInput';
import { SubmitHandler, useForm } from 'react-hook-form';
import MyTextArea from '../UI/inputs/MyTextArea';

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
      title="Send us your feedback"
    >
      <div>
        <MyInput
          id="Email"
          label={'Email'}
          inputRef={register('email', {
            required: false,
          })}
          groupClasses="mb-4"
          errors={errors}
          className="pl-10 text-base"
          labelProps={{ className: 'text-base' }}
          autoComplete="email"
          defaultValue={''}
        ></MyInput>
        <MyTextArea
          id="whatUserLiked"
          label={'What did you like about our application?'}
          className=""
          groupClasses="mb-0"
          placeholder="Tell us a little about yourself..."
          inputRef={register('whatUserLiked', {
            minLength: {
              value: 10,
              message: 'Tell us a bit more',
            },
            maxLength: {
              value: 500,
              message: 'In fewer words maybe? (Max 500 chars.)',
            },
          })}
          defaultValue={''}
          errors={errors}
          rows={8}
        />
      </div>
    </MyModal>
  );
};

export default FeedbackForm;
