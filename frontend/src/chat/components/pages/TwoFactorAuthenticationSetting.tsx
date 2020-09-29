import React, { useEffect, useState, useContext } from 'react';
import {useInput, stringField, useValidation, Logger, usePageTitle, useSystemMessages} from 'framework';
import './TwoFactorAuthenticationSetting.css';
import {Link} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import {
  Input,
  Label,
  Form,
  PositiveButton,
  NegativeButton,
  ValidationError,
  Title,
  QRCode,
  Button
} from 'chat/components/basics';
import { InputItem, Loading } from 'chat/components/parts';
import LoginContext from 'chat/context/LoginContext';

type FormFields = {
  authenticationCode: string;
};

const TwoFactorAuthenticationSetting: React.FC = () => {
  Logger.debug('rendering TwoFactorAuthenticationSetting...');

  usePageTitle('2要素認証の設定');

  const [authenticationCode, authenticationCodeAttributes, setAuthenticationCode] = useInput('');

  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [isEnabled, setIsEnabled] = useState<boolean>(false);
  const [isWaitActivation, setIsWaitActivation] = useState<boolean>(false);
  const [secretString, setSecretString] = useState<string>('');
  const [formError, setFormError] = useState<string>('');

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    authenticationCode: stringField()
      .required(systemMessages('errors.required', '認証コード'))
  });

  const mailAddress = useContext(LoginContext).user.mailAddress;

  useEffect(() => {
    BackendService.get2FASetting()
      .then(response => {
        if (response === 'ENABLED') {
          setIsEnabled(true);
        } else {
          setIsEnabled(false);
        }
        setIsLoaded(true);
      });
  }, []);

  if(!isLoaded){
    return <Loading />;
  }

  const switch2FASetting= async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (isEnabled) {
      await BackendService.disable2FASetting();
      setIsEnabled(false);
    } else {
      const result = await BackendService.get2FASecretString();
      setIsWaitActivation(true);
      setSecretString(result);
    }
  };

  const enable2FA = async (event: React.FormEvent<HTMLFormElement>) => {
    const status = await BackendService.enable2FASetting(authenticationCode);
    if (status === 'Unauthorized') {
      setFormError(systemMessages('errors.right', '認証コード'));
      return;
    }

    setAuthenticationCode('');
    setIsWaitActivation(false);
    setIsEnabled(true);
  };

  // 2要素認証のワンタイムパスワードを登録するためのQRコードに含まれるデータはURIとなっている。
  // URIの仕様は次のページを参照すること。
  // https://github.com/google/google-authenticator/wiki/Key-Uri-Format
  const input = (secretString !== '') ? `otpauth://totp/chat-example:${mailAddress}?secret=${secretString}&algorithm=HmacSHA1&digits=6&period=30` : '';

  return (
    <div>
      <nav>
        <Title>2要素認証を設定する</Title>
      </nav>
      <div className="TwoFactorAuthenticationSetting_content">
        <label>現在の設定:{ isEnabled ? '有効' : '無効'}</label>
        <div className="TwoFactorAuthenticationSetting_buttonGruop">
          {!isWaitActivation && <Link to={'/account_settings'}><Button type="button">キャンセル</Button></Link>}
          <Form onSubmit={switch2FASetting}>
            {isEnabled ?
              <NegativeButton type="submit">無効にする</NegativeButton>:
              !isWaitActivation && <PositiveButton type="submit">有効にする</PositiveButton> }
          </Form>
        </div>
        {isWaitActivation &&
          <Form onSubmit={handleSubmit({authenticationCode}, enable2FA)}>
            <QRCode input={input} width={300} height={300}/>
            <Label>{secretString}</Label>
            <ValidationError message={formError} />
            <InputItem title="認証コード" error={error.authenticationCode}>
              <Input type="text" maxLength={6} {...authenticationCodeAttributes} />
            </InputItem>
            <div className="TwoFactorAuthenticationSetting_buttonGruop">
              <Link to={'/account_settings'}><Button type="button">キャンセル</Button></Link>
              <PositiveButton type="submit">認証</PositiveButton>
            </div>
          </Form>
        }
      </div>
    </div>
  );
};

export default TwoFactorAuthenticationSetting;
