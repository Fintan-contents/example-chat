import React from 'react';
import {Link} from 'react-router-dom';
import './PageHeader.css';

export const PageHeader: React.FC = () => {
  return (
    <header className="PageHeader_header">
      <Link to="/">
        <h1 className="PageHeader_logo">チャットサービス</h1>
      </Link>
      <nav>
        <ul className="PageHeader_nav">
          <li><Link to="/login">ログイン</Link></li>
          <li><Link to="/signup">サインアップ</Link></li>
        </ul>
      </nav>
    </header>
  );
};
