import React from 'react';

export type SystemMessages = {
  [key: string]: string;
};

// REST APIからメッセージを読み込むための関数の型。
// 将来的に他言語対応をしたとき、ユーザーが自身の言語を設定した場合にこの関数を使ってメッセージを再読み込みする、といった用途を想定している。
// Promiseを返すようにしているのは、メッセージ読み込み後に何か処理をしたい場合に対応できるようにするため。
type SystemMessagesLoader = () => Promise<void>;

export type ContextType = [SystemMessages, SystemMessagesLoader];

export default React.createContext<ContextType>({} as ContextType);

