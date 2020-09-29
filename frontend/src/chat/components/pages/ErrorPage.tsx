import React, { useState, useEffect } from 'react';
import './ErrorPage.css';

type Props = {
  children: React.ReactNode;
};

const ErrorPage: React.FC<Props> = ({ children }) => {
  const [error, setError] = useState(false);
  useEffect(() => {
    // return falseをすることでデフォルトのハンドリングを行わなくなる
    window.addEventListener('error', (event: ErrorEvent) => {
      console.error(event);
      setError(true);
      return false;
    });

    window.addEventListener('unhandledrejection', (event: PromiseRejectionEvent) => {
      console.error(event);
      setError(true);
      return false;
    });
  }, []);
  if (error) {
    return (
      <div className="ErrorPage_page">
        <p>エラーが発生しました。しばらく経ってからやり直してください。</p>
        <p><a href="/">トップページへ戻る</a></p>
      </div>
    );
  }
  return (
    <React.Fragment>
      {React.Children.only(children)}
    </React.Fragment>
  );
};

export default ErrorPage;

