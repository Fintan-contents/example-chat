import React from 'react';
import './AccountSettings.css';
import {Link} from 'react-router-dom';
import { Logger, usePageTitle } from 'framework';
import {CloseButton, Title} from 'chat/components/basics';

const AccountSettings: React.FC = () => {
  Logger.debug('rendering AccountSettings...');

  usePageTitle('アカウント設定');

  return (
    <div>
      <nav className="ChannelSettings_nav">
        <Title>アカウント設定</Title>
        <div className="ChannelSettings_close">
          <Link to={'/channels/top'}><CloseButton/></Link>
        </div>
      </nav>
      <ul className="AccountSettings_list">
        <Link to={'/two-factor-authentication-setting'}>
          <li className="AccountSettings_item">
            <h2 className="AccountSettings_title">2要素認証を設定する</h2>
          </li>
        </Link>
        <Link to={'/password-change'}>
          <li className="AccountSettings_item">
            <h2 className="AccountSettings_title">パスワードを変更する</h2>
          </li>
        </Link>
        <Link to={'/account-deletion'}>
          <li className="AccountSettings_item">
            <h2 className="AccountSettings_title">アカウントを削除する</h2>
          </li>
        </Link>
      </ul>
    </div>
  );
};

export default AccountSettings;
