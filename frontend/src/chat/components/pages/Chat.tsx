import React, {useCallback, useEffect, useState} from 'react';
import { BackendService } from 'chat/backend';
import { NotificationService, Logger, usePageTitle } from 'framework';
import { ChannelDetail, ChannelContent, ChannelHeader, ChannelSideBar, Loading } from 'chat/components/parts';
import {useHistory, useParams} from 'react-router-dom';
import './Chat.css';

interface Channel {
  id: number;
  name: string;
  owner: number;
  type: string;
  allRead: boolean;
}
const Chat: React.FC = () => {
  const history = useHistory();
  const {channelId} = useParams<{channelId: string}>();

  const [channels, setChannels] = useState<Channel[]>([]);
  const [chatBot, setChatBot] = useState<Channel>();
  const [selectedChannel, setSelectedChannel] = useState<Channel>();
  const [showDetail, setShowDetail] = useState<boolean>(false);

  usePageTitle(selectedChannel && selectedChannel.name);

  Logger.debug('rendering Channel...');

  const selectChannel = useCallback((channel: Channel) => {
    setSelectedChannel(channel);
    window.localStorage.setItem('last_channel', channel.id.toString());
    history.push('/channels/' + channel.id);
  }, [history]);

  const updateChannels = useCallback(() => {
    BackendService.findAllChannel()
      .then(response => {
        Logger.debug(response);
        setChannels(response);

        if(selectedChannel){
          return;
        }
        const filteredChannels = response.filter((channel: Channel) => channel.id === Number(channelId));
        const channel = filteredChannels.length > 0 ? filteredChannels[0] : response[0];
        selectChannel(channel);
        const chatBot = response.filter((channel: Channel) => channel.type === 'SYSTEM')[0];
        setChatBot(chatBot);
      });
  }, [selectedChannel, selectChannel, channelId]);

  const postMessage = (channelId: number, text: string) => {
    BackendService.postMessage(channelId, text);
  };
  const uploadFile = (channelId: number, file: File) => {
    BackendService.postFile(channelId, file);
  };

  const handleShowDetail: React.MouseEventHandler<HTMLButtonElement> = () => {
    setShowDetail(!showDetail);
  };

  useEffect(() => {
    updateChannels();
  }, [updateChannels]);
  useEffect(() => {
    const unsubscribe = NotificationService.subscribe('channelNameChanged', () => {
      updateChannels();
    });
    return unsubscribe;
  }, [updateChannels]);
  useEffect(() => {
    const unsubscribe = NotificationService.subscribe('channelInvited', () => {
      updateChannels();
    });
    return unsubscribe;
  }, [updateChannels]);
  useEffect(() => {
    const unsubscribe = NotificationService.subscribe('message', () => {
      updateChannels();
    });
    return unsubscribe;
  }, [updateChannels]);
  useEffect(() => {
    const unsubscribe = NotificationService.subscribe('channelDeleted', (payload: any)  => {
      // 見ているチャンネルが削除されたらChatBotチャンネルに移動させる
      const {deleteChannelId} = payload as {deleteChannelId: number};
      if (selectedChannel && deleteChannelId === selectedChannel.id) {
        if(chatBot) {
          selectChannel(chatBot);
        } else {
          history.push('/channels/top');
        }
      }
    });
    return unsubscribe;
  }, [chatBot, history, selectChannel, selectedChannel]);

  if(!selectedChannel){
    return <Loading />;
  }

  return (
    <div className="Chat_layout">
      <ChannelSideBar channels={channels} selectedChannel={selectedChannel} selectChannel={selectChannel}/>
      <ChannelHeader channel={selectedChannel} showDetail={handleShowDetail}/>
      <ChannelContentBridge channelId={selectedChannel.id} postMessage={postMessage} uploadFile={uploadFile}/>
      <ChannelDetailBridge channelId={selectedChannel.id} showDetail={showDetail} setShowDetail={setShowDetail}/>
    </div>
  );
};

/**
 * チャンネルのコンテンツ部
 */
interface MessageNotification {
  messageId: number;
  accountId: number;
  userName: string;
  channelId: number;
  text: string;
  type: string;
  sendDateTime: string;
}
type ChannelContentBridgeProps = {
  channelId: number;
  postMessage: (channelId: number, text: string) => void;
  uploadFile: (channelId: number, file: File) => void;
}
const ChannelContentBridge: React.FC<ChannelContentBridgeProps> = props => {
  const [messages, setMessages] = useState<MessageNotification[]>([]);
  const [nextMessageId, setNextMessageId] = useState<number | null>(null);

  useEffect(() => {
    Logger.debug('useEffect ChannelContent');

    const unsubscribe = NotificationService.subscribe('message', (payload: any) => {
      const receivedMessage: MessageNotification = payload as MessageNotification;
      if (receivedMessage.channelId === props.channelId) {
        Logger.debug('receivedMessage:', receivedMessage);
        // messageはこの時点ではまだ最新で無い場合があるため、setMessagesには関数で渡して結合するようにしておく
        // メッセージは降順のため、最新のメッセージは先頭に追加する
        setMessages(pre => [receivedMessage, ...pre]);
      }
    });
    return unsubscribe;
  }, [props.channelId]);

  useEffect(() => {
    Logger.debug('findMessage by channelId:', props.channelId);

    // チャンネルが切り替わったときに初期化する
    setNextMessageId(null);
    setMessages([]);
    BackendService.findMessage(props.channelId, null)
      .then(response => {
        Logger.debug('findMessage response:', response);
        setMessages(response.messages);
        setNextMessageId(response.nextMessageId);
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [props.channelId]); // チャンネルIDが切り替わった場合にのみ実行するため

  useEffect(() => {
    Logger.debug('readMessage:', props.channelId);
    Logger.debug('messages:', messages);
    if (messages.length === 0 || messages[0].channelId !== props.channelId) {
      return;
    }
    const latestMessageId = messages[0].messageId;
    Logger.debug('latestMessageId', latestMessageId);
    BackendService.readMessage(props.channelId, latestMessageId);
  }, [messages, props.channelId]);

  const readMore = () => {
    Logger.debug('findMessage by paging. messageId:', nextMessageId);
    BackendService.findMessage(props.channelId, nextMessageId)
      .then(response => {
        Logger.debug('findMessage response:', response);
        setMessages(pre => [...pre, ...response.messages]);
        setNextMessageId(response.nextMessageId);
      });
  };

  return (
    <ChannelContent {...props} readMore={nextMessageId ? readMore : null} messages={messages}/>
  );
};

/**
 * チャンネルの詳細部
 */
interface Member {
  id: number;
  name: string;
  isOwner: boolean;
}
const ChannelDetailBridge: React.FC<{ showDetail: boolean; setShowDetail: (b: boolean) => void; channelId: number; }> = ({ showDetail, setShowDetail, channelId}) => {
  const [members, setMembers] = useState<Member[]>([]);

  useEffect(() => {
    BackendService.findMembers(channelId)
      .then(response => {
        Logger.debug('findMembers', response);
        setMembers(response);
      });
  }, [channelId]);

  return (
    <div className={`Chat_channel-detail ${showDetail ? 'Chat_channel-detail-shown' : 'Chat_channel-detail-hidden'}`}>
      {members ? <ChannelDetail channelId={channelId} setShowDetail={setShowDetail} members={members}/> :
        <Loading />}
    </div>
  );
};

export default Chat;
