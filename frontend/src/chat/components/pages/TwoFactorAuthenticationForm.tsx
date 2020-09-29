import React, { useContext, useState } from 'react';
import {useInput, stringField, useValidation, Logger, usePageTitle, useSystemMessages} from 'framework';
import LoginContext from 'chat/context/LoginContext';
import { useHistory } from 'react-router-dom';
import './TwoFactorAuthenticationForm.css';
import { BackendService } from 'chat/backend';
import { Input, Form, PositiveButton, Title, ValidationError } from 'chat/components/basics';
import { InputItem } from 'chat/components/parts';

type FormFields = {
  authenticationCode: string;
};

const TwoFactorAuthenticationForm: React.FC = () => {
  usePageTitle('ログイン');
  const [authenticationCode, authenticationCodeAttributes] = useInput('');

  const [formError, setFormError] = useState<string>('');

  const loginContext = useContext(LoginContext);

  const history = useHistory();

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    authenticationCode: stringField()
      .required(systemMessages('errors.required', '認証コード'))
  });

  Logger.debug('rendering TwoFactorForm...', authenticationCode);

  const authenticate = async (event: React.FormEvent<HTMLFormElement>) => {
    const status = await BackendService.verify2FA(authenticationCode);
    if (status === 'Unauthorized') {
      setFormError(systemMessages('errors.right', '認証コード'));
      return;
    }

    await loginContext.login();
    history.replace('/channels/top');
  };

  return (
    <div className="TwoFactorAuthenticationForm_content">
      <Title>2要素認証</Title>
      <Form onSubmit={handleSubmit({authenticationCode}, authenticate)}>
        <ValidationError message={formError}/>
        <InputItem title="認証コード" error={error.authenticationCode}>
          <Input type="text" maxLength={6} {...authenticationCodeAttributes}/>
        </InputItem>
        <PositiveButton type="submit">認証</PositiveButton>
      </Form>
    </div>
  );
};

export default TwoFactorAuthenticationForm;
