import React, {useContext, useState} from 'react';
import {Logger, stringField, useInput, usePageTitle, useSystemMessages, useValidation} from 'framework';
import LoginContext from 'chat/context/LoginContext';
import {Link, useHistory} from 'react-router-dom';
import './LoginForm.css';
import {BackendService} from 'chat/backend';
import {Form, Input, PositiveButton, Title, ValidationError} from 'chat/components/basics';
import {InputItem} from 'chat/components/parts';

type FormFields = {
  mailAddress: string;
  password: string;
};

const LoginForm: React.FC = () => {
  usePageTitle('ログイン');

  const [mailAddress, mailAddressAttributes] = useInput('');
  const [password, passwordAttributes] = useInput('');

  const [formError, setFormError] = useState<string>('');

  const loginContext = useContext(LoginContext);

  const history = useHistory();

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    mailAddress: stringField()
      .required(systemMessages('errors.required', 'メールアドレス'))
      .email(systemMessages('errors.email')),
    password: stringField()
      .required(systemMessages('errors.required', 'パスワード'))
      .minLength(8, systemMessages('errors.length.min', 'パスワード', '8')),
  });

  Logger.debug('rendering LoginForm...', mailAddress, password);

  const login: React.FormEventHandler<HTMLFormElement> = async (event) => {
    const status = await BackendService.login(mailAddress, password);
    if (status === 'Unauthorized') {
      setFormError(systemMessages('errors.login'));
      return;
    }

    if (status === 'WAITING_2FA') {
      Logger.debug('WAITING_2FA...');
      history.push('/two-factor-authentication');
      return;
    }

    await loginContext.login();
    history.push('/channels/top');
  };

  return (
    <div className="LoginForm_content">
      <div className="LoginForm_form">
        <Title>アカウント認証</Title>
        <Form onSubmit={handleSubmit({ mailAddress, password }, login, () => setFormError(''))}>
          <ValidationError message={formError}/>
          <InputItem title="メールアドレス" error={error.mailAddress}>
            <Input type="text" maxLength={50} {...mailAddressAttributes}/>
          </InputItem>
          <InputItem title="パスワード" error={error.password}>
            <Input type="password" maxLength={50} {...passwordAttributes}/>
          </InputItem>
          <PositiveButton type="submit">ログイン</PositiveButton>
          <div className="LoginForm_link">
            <Link to="/password-reset">パスワードを忘れた方はこちら</Link>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default LoginForm;
