import React from 'react';
import {Link} from 'react-router-dom';
import './ChannelSideBar.css';
import {ChannelSideBarItemList} from './ChannelSideBarItemList';

interface Channel {
  id: number;
  name: string;
  owner: number;
  type: string;
  allRead: boolean;
}
type ChannelSideBarProps = {
  channels: Channel[];
  selectedChannel: Channel;
  selectChannel: (channel: Channel) => void;
};

export const ChannelSideBar: React.FC<ChannelSideBarProps> = props => {
  return (
    <aside className="ChannelSideBar_sidebar">
      <ul className="ChannelSideBar_sidebarList">
        <li>
          <h3>
            <span>チャンネル</span>
            <Link to={'/channel_create'}><button className="ChannelSideBar_plusButton"/></Link>
          </h3>
          <ChannelSideBarItemList {...props} type="CHANNEL"/>
        </li>
        <li>
          <h3>システム</h3>
          <ChannelSideBarItemList {...props} type="SYSTEM"/>
        </li>
      </ul>
    </aside>
  );
};