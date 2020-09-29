import React from 'react';
import {useSystemMessages} from 'framework';

type Props = {
  name: string;
  options?: string[];
}

/**
 * 画面のラベルは本コンポーネント（SystemMessage）を使用して表示する。
 * name属性でメッセージIDを指定する。
 *
 * メッセージに文字列を埋め込む場合は、options属性に埋め込む文字列を配列で指定する。
 * 配列で指定した順番に文字列が埋め込まれる。
 *
 * messages.properties例：
 *   label.welcome=Welcome
 *   label.hoge={0}と{1}のラベル
 *
 * コード例：
 *   <SystemMessage name="label.welcome"/> // Welcome
 *   <SystemMessage name="label.hoge" options={['hoge', 'fuga']} /> // hogeとfugaのラベル
 */
export const SystemMessage: React.FC<Props> = ({ name, options = []}) => {
  const systemMessages = useSystemMessages();

  if(options.length === 0) {
    return (
      <React.Fragment>{systemMessages(name)}</React.Fragment>
    );
  } else {
    return (
      <React.Fragment>{systemMessages(name, ...options)}</React.Fragment>
    );
  }
};
