import React from 'react';
import {Link} from 'react-router-dom';
import './ChannelHeader.css';

interface Channel {
  id: number;
  name: string;
  owner: number;
  type: string;
  allRead: boolean;
}
type ChannelHeaderProps = {
  channel: Channel;
  showDetail: (event: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
};

export const ChannelHeader: React.FC<ChannelHeaderProps> = ({channel,showDetail}) => {
  return (
    <div className="ChannelHeader_header">
      <h3>{'#' + channel.name}</h3>
      <nav>
        <ul className="ChannelHeader_navigation">
          <li><button className="ChannelHeader_linkButton" onClick={showDetail}>チャンネル参加者</button></li>
          <li><Link to={'/channels/' + channel.id + '/settings'}>チャンネル設定</Link></li>
          <li><Link to={'/account_settings'}>アカウント設定</Link></li>
        </ul>
      </nav>
    </div>
  );
};
