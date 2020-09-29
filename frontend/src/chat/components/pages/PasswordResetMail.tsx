import React from 'react';
import './PasswordResetMail.css';
import { Logger, usePageTitle } from 'framework';

const PasswordResetMail: React.FC = () => {
  Logger.debug('rendering PasswordResetMail...');
  usePageTitle('パスワードリセット');
  return (
    <div className="PasswordResetMail_content">
      <div className="PasswordResetMail_info">
        <p>メールアドレスが登録されている場合、メールを送信します。</p>
        <p>メール本文に記載しているURLにアクセスし、パスワードをリセットしてください。</p>
      </div>
    </div>
  );
};

export default PasswordResetMail;
