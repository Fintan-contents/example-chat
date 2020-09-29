import React from 'react';
import './PasswordResetComplete.css';
import { Logger, usePageTitle } from 'framework';

const PasswordResetComplete: React.FC = () => {
  Logger.debug('rendering PasswordResetComplete...');
  usePageTitle('パスワードリセット');
  return (
    <div className="PasswordResetComplete_content">
      <div className="PasswordResetComplete_info">
        <p>パスワードをリセットしました。</p>
        <p>新しいパスワードでログインしてください</p>
      </div>
    </div>
  );
};

export default PasswordResetComplete;
