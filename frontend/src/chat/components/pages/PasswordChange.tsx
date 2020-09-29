import React, {useState} from 'react';
import './PasswordChange.css';
import {Link, useHistory} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import {useInput, stringField, useValidation, Logger, usePageTitle, useSystemMessages} from 'framework';
import {ValidationError, Input, Form, Title, Button, PositiveButton} from 'chat/components/basics';
import {InputItem} from 'chat/components/parts';

type FormFields = {
  password: string;
  newPassword: string;
  confirmationPassword: string;
};

const PasswordChange: React.FC = () => {
  Logger.debug('rendering AccountDeletion...');

  usePageTitle('パスワード変更');

  const [password, passwordAttributes ] = useInput('');
  const [newPassword, newPasswordAttributes ] = useInput('');
  const [confirmationPassword, confirmationPasswordAttributes ] = useInput('');

  const [ formError, setFormError ] = useState<string>('');
  const history = useHistory();

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    password: stringField()
      .required(systemMessages('errors.required', 'パスワード')),
    newPassword: stringField()
      .required(systemMessages('errors.required', '新パスワード'))
      .minLength(8, systemMessages('errors.length.min', '新パスワード', '8')),
    confirmationPassword: stringField()
      .required(systemMessages('errors.required', '新パスワード（確認）'))
  });

  const changePassword = async (event: React.FormEvent<HTMLFormElement>) => {
    if (newPassword !== confirmationPassword) {
      setFormError(systemMessages('errors.match', '新パスワード', '新パスワード（確認）'));
      return;
    }

    const status = await BackendService.changePassword(password, newPassword);
    Logger.debug('status: ', status);
    if (status === 'Unauthorized') {
      setFormError(systemMessages('errors.right', 'パスワード'));
      return;
    }

    history.push('/channels/top');
  };

  return (
    <div>
      <nav>
        <Title>パスワードを変更する</Title>
      </nav>
      <div className="PasswordChange_content">
        <Form onSubmit={handleSubmit({password, newPassword, confirmationPassword}, changePassword, () => setFormError(''))}>
          <ValidationError message={formError} />
          <InputItem title="現在のパスワード" error={error.password}>
            <Input type="password" maxLength={50} {...passwordAttributes}/>
          </InputItem>
          <InputItem title="新パスワード" error={error.newPassword}>
            <Input type="password" maxLength={50} {...newPasswordAttributes}/>
          </InputItem>
          <InputItem title="新パスワード（確認）" error={error.confirmationPassword}>
            <Input type="password" maxLength={50} {...confirmationPasswordAttributes}/>
          </InputItem>
          <span className="PasswordChange_buttonGroup">
            <Link to={'/account_settings'}><Button type="button">キャンセル</Button></Link>
            <PositiveButton type="submit">変更</PositiveButton>
          </span>
        </Form>
      </div>
    </div>
  );
};

export default PasswordChange;
