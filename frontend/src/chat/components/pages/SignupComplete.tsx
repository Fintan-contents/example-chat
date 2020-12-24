import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './SignupComplete.css';
import { BackendService } from 'chat/backend';
import { Logger, usePageTitle } from 'framework';
import {Loading} from 'chat/components/parts';

type RouterParams = { token: string; };

const SignupComplete: React.FC = () => {
  Logger.debug('rendering SignUpMail...');
  usePageTitle('サインアップ');

  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const {token} = useParams<RouterParams>();
  const [ message, setMessage ] = useState<string>('アカウント登録が完了しました。ログインしてサービスをご利用ください。');

  useEffect(() => {
    Logger.debug('useEffect SignupComplete', token);
    BackendService.verifyAccount(token)
      .then(response => {
        if (response === 'conflict') {
          setMessage('既に同じユーザー名が登録されています。再度サインアップしてください。');
        }
        if (response === 'invalid') {
          setMessage('無効なトークンです。再度サインアップしてください。');
        }
      })
      .finally(() => setIsLoaded(true));
  }, [token]);

  if(!isLoaded){
    return <Loading />;
  }

  return (
    <div className="SignupComplete_content">
      <p>{message}</p>
    </div>
  );
};

export default SignupComplete;
