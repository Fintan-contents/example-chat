import React from 'react';
import moment from 'moment';

type Props = {
  value: string;
};

/**
 * 日時を表示する際は、Moment.js（https://momentjs.com/）を使用してユーザーのローカルタイムへ変換する。
 *   moment.utc(dateTime).local().format('YYYY/MM/DD HH:mm')
 *
 * ユーザーのタイムゾーンはユーザーが使用している端末の環境設定に依存する。
 *
 * 変数dateTimeの値はMoment.jsが解釈可能な形式であれば何でも受け入れられるが、
 * 本アプリケーションではフロントエンドとバックエンド間での日時のやりとりにはISO 8601形式の文字列(yyyy-mm-ddThh:mm:ss)を使用する。
 *
 * 前述の変換処理を日時を表示する箇所すべてで行うのは煩雑なため、本コンポーネント（DateTime）を用意している。
 *
 * 次のようにして日時の表示ができる。
 *   <DateTime value={dateTime}/>
 */
export const DateTime: React.FC<Props> = ({ value }) => {
  return (
    <React.Fragment>
      {moment.utc(value).local().format('YYYY/MM/DD HH:mm')}
    </React.Fragment>
  );
};
