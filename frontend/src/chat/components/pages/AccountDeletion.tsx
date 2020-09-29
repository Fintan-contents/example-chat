import React, {useState} from 'react';
import './AccountDeletion.css';
import {Link} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import {useInput, Logger, usePageTitle, useSystemMessages, useValidation, stringField} from 'framework';
import {Button, Form, Input, Checkbox, NegativeButton, Title, ValidationError} from 'chat/components/basics';
import {InputItem} from 'chat/components/parts';

const AccountDeletion: React.FC = () => {
  Logger.debug('rendering AccountDeletion...');

  usePageTitle('アカウント削除');

  const [password, passwordAttributes] = useInput('');
  const [formError, setFormError] = useState<string>('');

  const [checked, setCheck] = useState<boolean>(false);

  Logger.debug('checked:', checked);

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<{password: string}>({
    password: stringField()
      .required(systemMessages('errors.required', 'パスワード'))
      .minLength(8, systemMessages('errors.length.min', 'パスワード', '8')),
  });

  const deleteAccount = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const status = await BackendService.deleteAccount(password);
    if (status === 'Unauthorized') {
      setFormError(systemMessages('errors.right', 'パスワード'));
      return;
    }

    window.location.href = '/';
  };

  return (
    <div>
      <nav>
        <Title>アカウントを削除する</Title>
      </nav>
      <div className="AccountDeletion_content">
        <ValidationError message={formError} />
        <p className="AccountDeletion_description">
          アカウントを本当に削除しますか？削除後は元に戻すことはできません。
        </p>
        <Form onSubmit={handleSubmit({password}, deleteAccount, () => setFormError(''))}>
          <label>
            <Checkbox onChange={() => setCheck(!checked)}/>
              はい、完全に削除します。
          </label>
          <InputItem title="パスワード" error={error.password}>
            <Input type="password" maxLength={50} {...passwordAttributes}/>
          </InputItem>
          <span className="AccountDeletion_buttonGroup">
            <Link to={'/account_settings'}><Button type="button">キャンセル</Button></Link>
            <NegativeButton type="submit" disabled={!checked}>アカウントを削除する</NegativeButton>
          </span>
        </Form>
      </div>
    </div>
  );
};

export default AccountDeletion;
