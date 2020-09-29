import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import {useInput, useValidation, stringField, Logger, usePageTitle, useSystemMessages} from 'framework';
import { BackendService } from 'chat/backend';
import './Signup.css';
import { Input, Form, PositiveButton, Title, ValidationError } from 'chat/components/basics';
import { InputItem } from 'chat/components/parts';

type FormFields = {
  userName: string;
  mailAddress: string;
  password: string;
};

const Signup: React.FC = () => {
  usePageTitle('サインアップ');
  const [userName, userNameAttributes] = useInput('');
  const [mailAddress, mailAddressAttributes] = useInput('');
  const [password, passwordAttributes] = useInput('');

  const [processing, setProcessing] = useState(false);

  const history = useHistory();

  const [formError, setFormError] = useState<string>('');

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    userName: stringField()
      .required(systemMessages('errors.required', 'ユーザ名')),
    mailAddress: stringField()
      .required(systemMessages('errors.required', 'メールアドレス'))
      .email(systemMessages('errors.email')),
    password: stringField()
      .required(systemMessages('errors.required', 'パスワード'))
      .minLength(8, systemMessages('errors.length.min', 'パスワード', '8')),
  });

  Logger.debug('rendering Signup...', mailAddress, password, error);

  const signup: React.FormEventHandler<HTMLFormElement> = async (event) => {
    setProcessing(true);
    try {
      const response = await BackendService.registerAccount(userName, mailAddress, password);
      if (response === 'conflict')  {
        setFormError(systemMessages('errors.conflict', 'ユーザ名'));
        return;
      }
      history.push('/signup-mail');
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="Signup_content">
      <div className="Signup_form">
        <Title>アカウント登録</Title>
        <Form onSubmit={handleSubmit({ userName, mailAddress, password }, signup, () => setFormError(''))}>
          <ValidationError message={formError}/>
          <InputItem title="ユーザー名" error={error.userName}>
            <Input type='text' maxLength={50} {...userNameAttributes}/>
          </InputItem>
          <InputItem title="メールアドレス" error={error.mailAddress}>
            <Input type='text' maxLength={50} autoComplete='email' {...mailAddressAttributes}/>
          </InputItem>
          <InputItem title="パスワード" error={error.password}>
            <Input type='password' maxLength={50} autoComplete='new-password' {...passwordAttributes}/>
          </InputItem>
          <PositiveButton type='submit' disabled={processing}>登録する</PositiveButton>
        </Form>
      </div>
    </div>
  );
};

export default Signup;
