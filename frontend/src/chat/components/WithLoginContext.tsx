import React, { useEffect, useState } from 'react';
import { BackendService } from 'chat/backend';
import LoginContext, { LoginSwitch } from 'chat/context/LoginContext';
import { Logger } from 'framework';
import {Loading} from 'chat/components/parts';

type Props = {
  children: React.ReactNode;
}

function refreshCsrfToken(): Promise<void> {
  return BackendService.getCsrfToken().then(({ csrfTokenHeaderName, csrfTokenValue}) => {
    Logger.debug('csrfTokenHeaderName:', csrfTokenHeaderName, 'csrfTokenValue:', csrfTokenValue);
    BackendService.setCsrfTokenHeaderName(csrfTokenHeaderName);
    BackendService.setCsrfTokenValue(csrfTokenValue);
  });
}

const anonymousUser = { accountId: '', userName: '', mailAddress: '' };

/**
 * ログイン情報（ログイン状態やアカウント名など）は、ログイン時にWithLoginContextコンポーネントのステートに保存する。
 * 以降はログアウトを行ったり、ブラウザのタブを閉じたり、本アプリケーション以外のウェブサイトへ移動するまでは
 * WithLoginContextコンポーネントのステートに保存されたログイン情報を参照する。
 *
 * WithLoginContextコンポーネントはすべての画面から参照できるようにするため、ルートとなるコンポーネントに準ずる位置へ配置する。
 * ログイン情報はコンテキスト（LoginContext）を経由して子孫コンポーネントから参照する。
 */
const WithLoginContext: React.FC<Props> = ({ children }) => {

  const [user, setUser] = useState(anonymousUser);
  const isLogin = user !== anonymousUser;
  Logger.debug('isLogin:', isLogin);
  const [initialized, setInitialized] = useState(false);
  useEffect(() => {
    refreshCsrfToken().then(() => {
      return BackendService.getUser()
        .then(user => setUser(user))
        .catch(() => setUser(anonymousUser))
        .finally(() => setInitialized(true));
    });
  }, []);

  const loginSwitch: LoginSwitch = {
    login: async () => {
      Logger.debug('login');
      await refreshCsrfToken();
      const user = await BackendService.getUser();
      setUser(user);
    },
    logout: async () => {
      Logger.debug('logout');
      await BackendService.logout();
      await refreshCsrfToken();
      setUser(anonymousUser);
    },
    isLogin,
    user,
  };

  if (initialized === false) {
    return <Loading />;
  }

  return (
    <LoginContext.Provider value={loginSwitch}>
      {children}
    </LoginContext.Provider>
  );
};

export default WithLoginContext;
