import React, { useState, useCallback, useEffect } from 'react';
import SystemMessagesContext, { SystemMessages, ContextType } from 'framework/context/SystemMessagesContext';

type Props = {
  children: React.ReactNode;
  getSystemMessages: () => Promise<SystemMessages>;
};

/**
 * フロントエンドではSPAの初期処理でREST APIからメッセージ一覧を取得する。
 * 取得したメッセージ一覧は WithSystemMessages コンポーネントのステートに保存する。
 * REST APIからのメッセージ一覧の取得はこの1度のみとなる。
 * 以降はブラウザのタブを閉じたり、本アプリケーション以外のウェブサイトへ移動するまでは
 * WithSystemMessagesコンポーネントのステートに保存されたメッセージを参照する。
 *
 * WithSystemMessagesコンポーネントはすべての画面から参照できるようにするため、ルートとなるコンポーネントに準ずる位置へ配置する。
 * メッセージはコンテキスト(SystemMessagesContext)を経由して子孫コンポーネントから参照できるようにする。
 */
const WithSystemMessages: React.FC<Props> = ({ children, getSystemMessages }) => {

  const [systemMessages, setSystemMessages] = useState<SystemMessages>({});

  const systemMessagesLoader = useCallback(() => {
    return getSystemMessages().then(setSystemMessages);
  }, [getSystemMessages, setSystemMessages]);

  useEffect(() => {
    systemMessagesLoader();
  }, [systemMessagesLoader]);

  const value: ContextType = [systemMessages, systemMessagesLoader];

  return (
    <SystemMessagesContext.Provider value={value}>
      {children}
    </SystemMessagesContext.Provider>
  );
};

export default WithSystemMessages;
