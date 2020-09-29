import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import {useInput, useValidation, stringField, Logger, usePageTitle, useSystemMessages} from 'framework';
import { BackendService } from 'chat/backend';
import './PasswordResetMailForm.css';
import { Input, Form, PositiveButton, Title } from 'chat/components/basics';
import { InputItem } from 'chat/components/parts';

type FormFields = {
  mailAddress: string;
};

const PasswordResetMailForm: React.FC = () => {
  usePageTitle('パスワードリセット');
  const [mailAddress, mailAddressAttributes] = useInput('');

  const [processing, setProcessing] = useState(false);

  const history = useHistory();

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    mailAddress: stringField()
      .required(systemMessages('errors.required', 'メールアドレス'))
      .email(systemMessages('errors.email')),
  });

  Logger.debug('rendering PasswordResetMailForm...', mailAddress);

  const resetPassword: React.FormEventHandler<HTMLFormElement> = async (event) => {
    setProcessing(true);
    try {
      await BackendService.issuePasswordResetToken(mailAddress);
      history.push('/password-reset-mail');
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="PasswordResetMailForm_content">
      <div className="PasswordResetMailForm_form">
        <Title>パスワードリセット画面</Title>
        <Form onSubmit={handleSubmit({mailAddress}, resetPassword)}>
          <InputItem title="メールアドレス" error={error.mailAddress}>
            <Input type='text' maxLength={50} autoComplete='email' {...mailAddressAttributes}/>
          </InputItem>
          <PositiveButton type='submit' disabled={processing}>送信</PositiveButton>
        </Form>
      </div>
    </div>
  );
};

export default PasswordResetMailForm;
