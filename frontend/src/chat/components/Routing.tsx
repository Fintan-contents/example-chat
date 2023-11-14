import React, { useContext } from 'react';
import { BrowserRouter as Router, Redirect, Route, Switch } from 'react-router-dom';
import {
  Signup,
  SignupMail,
  SignupComplete,
  Chat,
  ChannelRouting,
  Top,
  LoginForm,
  ChannelSettings,
  ChannelRename,
  ChannelCreate,
  ChannelDelete,
  TwoFactorAuthenticationForm,
  AccountSettings,
  TwoFactorAuthenticationSetting,
  AccountDeletion,
  PasswordChange,
  PasswordResetMail,
  PasswordResetMailForm,
  PasswordResetForm,
  PasswordResetComplete,
  ChannelMemberInvite,
  ChannelLeave,
  MessageExportForm,
} from './pages';
import LoginContext from 'chat/context/LoginContext';
import { RouteProps } from 'react-router';
import {PageHeader} from './parts';
import Logout from './pages/Logout';

/**
 * ルーティングには、React Router(https://v5.reactrouter.com/web/guides/quick-start) を使用する。
 * 詳しい使い方についてはドキュメントを参照すること。
 *
 * ルーティングの設定は 本コンポーネント（Routing）に集約する。
 * ログイン状態によって遷移先をコントロールできるように <Route>コンポーネント(https://v5.reactrouter.com/web/api/Route)をラップしたコンポーネントを使用する。
 * ログイン状態にかかわらず参照できるページは、<Route>コンポーネントをそのまま使用する
 */
const Routing: React.FC = () => {
  const usingPageHeaderPaths = [
    '/login',
    '/two-factor-authentication',
    '/password-reset/:token',
    '/password-reset',
    '/password-reset-mail',
    '/password-reset-complete',
    '/signup',
    '/signup-mail',
    '/signup-complete/:token',
    '/',
  ];
  return (
    <Router>
      <Route exact path={usingPageHeaderPaths}>
        <PageHeader/>
      </Route>

      <Switch>
        <ForAnonymousRoute exact path="/login">
          <LoginForm/>
        </ForAnonymousRoute>
        <ForAnonymousRoute exact path="/two-factor-authentication">
          <TwoFactorAuthenticationForm/>
        </ForAnonymousRoute>
        <ProtectedRoute exact path="/account_settings">
          <AccountSettings/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/two-factor-authentication-setting">
          <TwoFactorAuthenticationSetting/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/account-deletion">
          <AccountDeletion/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/password-change">
          <PasswordChange/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channel_create">
          <ChannelCreate/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channels/:channelId/channel_rename">
          <ChannelRename/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channels/:channelId/export">
          <MessageExportForm/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channels/:channelId/channel_delete">
          <ChannelDelete/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channels/:channelId/invite_member">
          <ChannelMemberInvite/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channels/:channelId/leave">
          <ChannelLeave/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channels/:channelId/settings">
          <ChannelSettings/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channels/top">
          <ChannelRouting/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/channels/:channelId">
          <Chat/>
        </ProtectedRoute>
        <ProtectedRoute exact path="/logout">
          <Logout/>
        </ProtectedRoute>
        <ForAnonymousRoute exact path="/password-reset/:token">
          <PasswordResetForm/>
        </ForAnonymousRoute>
        <ForAnonymousRoute exact path="/password-reset">
          <PasswordResetMailForm/>
        </ForAnonymousRoute>
        <ForAnonymousRoute exact path="/password-reset-mail">
          <PasswordResetMail/>
        </ForAnonymousRoute>
        <ForAnonymousRoute exact path="/password-reset-complete">
          <PasswordResetComplete/>
        </ForAnonymousRoute>
        <ForAnonymousRoute exact path="/signup">
          <Signup/>
        </ForAnonymousRoute>
        <ForAnonymousRoute exact path="/signup-mail">
          <SignupMail/>
        </ForAnonymousRoute>
        <ForAnonymousRoute exact path="/signup-complete/:token">
          <SignupComplete/>
        </ForAnonymousRoute>
        <Route exact path="/">
          <Top />
        </Route>
      </Switch>
    </Router>
  );
};

/**
 * ログインが必要な画面に対して設定し、未ログイン状態の場合はログイン画面へ遷移する。
 */
const ProtectedRoute: React.FC<RouteProps> = (props) => {
  const { isLogin } = useContext(LoginContext);
  return (
    <Route {...props}>
      {isLogin ? props.children : <Redirect to="/login" />}
    </Route>
  );
};

/**
 * 未ログイン状態でのみ参照できる画面に対して設定し、ログイン状態の場合はログイン後のトップ画面へ遷移する。
 */
const ForAnonymousRoute: React.FC<RouteProps> = (props) => {
  const { isLogin } = useContext(LoginContext);
  return (
    <Route {...props}>
      {(!isLogin) ? props.children : <Redirect to="/channels/top" />}
    </Route>
  );
};

export default Routing;
