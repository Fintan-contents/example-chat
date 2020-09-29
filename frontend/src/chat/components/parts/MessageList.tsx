import React from 'react';
import { Logger } from 'framework';
import './MessageList.css';
import { DateTime } from 'chat/components/basics';

interface MessageNotification {
  messageId: number;
  accountId: number;
  userName: string;
  channelId: number;
  text: string;
  type: string;
  sendDateTime: string;
}
type Props = {
  messages: MessageNotification[];
}

export const MessageList: React.FC<Props> = ({messages}) => {
  Logger.debug('rendering MessageList...', messages);

  return (
    <ul className="MessageList_list">
      {Array.from(messages).reverse().map((message) =>
        <li key={message.messageId} className="MessageList_listItem">
          <div>
            <div>
              <span className="MessageList_userName">{message.userName}</span>
              <span className="MessageList_sendDateTime"><DateTime value={message.sendDateTime}/></span>
            </div>
            <div>{message.type === 'IMAGE'
              ? <img className="MessageList_img" src={message.text} alt=""/>
              : message.text}
            </div>
          </div>
        </li>
      )}
    </ul>
  );
};

