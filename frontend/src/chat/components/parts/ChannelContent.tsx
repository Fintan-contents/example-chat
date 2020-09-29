import React, {useRef} from 'react';
import {Logger} from 'framework';
import {MessageList} from './MessageList';
import {MessageForm} from './MessageForm';
import './ChannelContent.css';

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
  readMore: (() => void) | null;
  channelId: number;
  postMessage: (channelId: number, text: string) => void;
  uploadFile: (channelId: number, file: File) => void;
}

export const ChannelContent: React.FC<Props> = ({ channelId, messages, readMore, postMessage, uploadFile }) => {
  Logger.debug('rendering ChannelContent...', channelId);

  const scrollRef = useRef<HTMLDivElement | null>(null);

  const sendMessage = (channelId: number, text: string) => {
    postMessage(channelId, text);

    // 送信したメッセージを受け取ってからスクロールさせるため少し待つ
    setTimeout(() => {
      scrollRef!.current!.scrollIntoView({
        block: 'end',
      });
    }, 300);
  };

  return (
    <React.Fragment>
      <div className="ChannelContent_messageList" >
        <div ref={scrollRef}>
          <p className="ChannelContent_paging">
            {readMore && (
              <button className="ChannelContent_moreReadButton"
                onClick={() => readMore()} >
                もっと読み込む
              </button>
            )}
          </p>
          <MessageList messages={messages} />
        </div>
      </div>
      <div className="ChannelContent_form">
        <MessageForm channelId={channelId}
          sendMessage={sendMessage}
          uploadFile={uploadFile}/>
      </div>
    </React.Fragment>
  );
};

