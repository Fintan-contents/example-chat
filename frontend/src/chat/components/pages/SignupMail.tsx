import React from 'react';
import './SignupMail.css';
import { Logger, usePageTitle } from 'framework';

const SignupMail: React.FC = () => {
  Logger.debug('rendering SignUpMail...');
  usePageTitle('サインアップ');
  return (
    <div className="SignupMail_content">
      <p>アカウント登録用のメールを送信しました。</p>
      <p>メール本文に記載しているURLにアクセスし、アカウントを認証してください。</p>
    </div>
  );
};

export default SignupMail;
