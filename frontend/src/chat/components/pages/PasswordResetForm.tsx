import React, {useEffect, useState} from 'react';
import {useHistory, useParams} from 'react-router-dom';
import {useInput, useValidation, stringField, Logger, usePageTitle, useSystemMessages} from 'framework';
import { BackendService } from 'chat/backend';
import './PasswordResetForm.css';
import {Input, Form, PositiveButton, Title, ValidationError} from 'chat/components/basics';
import { InputItem } from 'chat/components/parts';

type RouterParams = {
  token: string;
};

type FormFields = {
  newPassword: string;
  confirmationPassword: string;
};

const PasswordResetForm: React.FC = () => {
  usePageTitle('パスワードリセット');

  const [newPassword, newPasswordAttributes] = useInput('');
  const [confirmationPassword, confirmationPasswordAttributes] = useInput('');

  const [ formError, setFormError ] = useState<string>('');

  const {token} = useParams<RouterParams>();

  const history = useHistory();

  Logger.debug('rendering PasswordResetForm...', newPassword, confirmationPassword);

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    newPassword: stringField()
      .required(systemMessages('errors.required', '新パスワード'))
      .minLength(8, systemMessages('errors.length.min', '新パスワード', '8')),
    confirmationPassword: stringField()
      .required(systemMessages('errors.required', '新パスワード（確認）'))
  });

  useEffect(() => {
    Logger.debug('useEffect PasswordResetForm');
    BackendService.isValidPasswordResetToken(token)
      .then(response => {
        Logger.debug(response);
        if (response === false) {
          setFormError(systemMessages('errors.invalid.url'));
        }
      });
  }, [systemMessages, token]);

  const resetPassword: React.FormEventHandler<HTMLFormElement> = async (event) => {
    if (newPassword !== confirmationPassword) {
      setFormError(systemMessages('errors.match', '新パスワード', '新パスワード（確認）'));
      return;
    }

    await BackendService.resetPassword(token, newPassword);
    history.push('/password-reset-complete');
  };

  return (
    <div className="PasswordResetForm_content">
      <div className="PasswordResetMailForm_form">
        <Title>パスワード再設定画面</Title>
        <Form onSubmit={handleSubmit({newPassword, confirmationPassword}, resetPassword)}>
          <ValidationError message={formError} />
          <InputItem title="新パスワード" error={error.newPassword}>
            <Input type='password' maxLength={50} autoComplete='new-password' {...newPasswordAttributes}/>
          </InputItem>
          <InputItem title="新パスワード（確認）" error={error.confirmationPassword}>
            <Input type='password' maxLength={50} autoComplete='new-password' {...confirmationPasswordAttributes}/>
          </InputItem>
          <PositiveButton type='submit'>登録する</PositiveButton>
        </Form>
      </div>
    </div>
  );
};

export default PasswordResetForm;
