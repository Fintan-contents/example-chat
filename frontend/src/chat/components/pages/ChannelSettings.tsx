import React, {useEffect, useState} from 'react';
import './ChannelSettings.css';
import {Link, useParams} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import { Logger, usePageTitle } from 'framework';
import {CloseButton, Title} from 'chat/components/basics';
import {Loading} from 'chat/components/parts';

interface Channel {
  id: number;
  name: string;
}

const ChannelSettings: React.FC = () => {
  Logger.debug('rendering ChannelSettings...');

  const {channelId} = useParams<{channelId: string}>();
  const [channel, setChannels] = useState<Channel>();

  usePageTitle(channel && `${channel.name} | 設定`);

  useEffect(() => {
    BackendService.findChannel(Number(channelId))
      .then(response => {
        Logger.debug(response);
        setChannels(response);
      });
  }, [channelId]);

  if(!channel) {
    return <Loading />;
  }

  return (
    <div>
      <nav className="ChannelSettings_nav">
        <Title>#{channel.name}のチャンネル設定</Title>
        <div className="ChannelSettings_close">
          <Link to={'/channels/' + channelId}><CloseButton/></Link>
        </div>
      </nav>
      <ul className="ChannelSettings_list">
        <li className="ChannelSettings_item">
          <Link to={'/channels/' + channelId + '/channel_rename'}>
            <h2 className="ChannelSettings_itemTitle">このチャンネルの名前を変更する</h2>
            <div>
              チャンネル名はいつでも変更することができます。
              ただし、チームメンバーが混乱してしまうかもしれませんので、
              チャンネル名の変更は控えめにお願いします。
            </div>
          </Link>
        </li>
        <li className="ChannelSettings_item">
          <Link to={'/channels/' + channelId + '/export'}>
            <h2 className="ChannelSettings_itemTitle">チャンネルのメッセージ履歴をエクスポートする</h2>
            <div>
              チャンネルに投稿されたメッセージの履歴をエクスポートします。
            </div>
          </Link>
        </li>
        <li className="ChannelSettings_item">
          <Link to={'/channels/' + channelId + '/leave'}>
            <h2 className="ChannelSettings_itemTitle">このチャンネルから退出する</h2>
            <div>
              チャンネルを退出すると、再度招待されない限りこのチャンネルを参照できなくなります。
            </div>
          </Link>
        </li>
        <li className="ChannelSettings_item">
          <Link to={'/channels/' + channelId + '/channel_delete'}>
            <h2 className="ChannelSettings_itemTitle">このチャンネルを削除する</h2>
            <div>
              チャンネルを削除すると、すべてのメッセージが完全に削除されます。削除後は元に戻すことはできません。
            </div>
          </Link>
        </li>
      </ul>
    </div>
  );
};

export default ChannelSettings;
