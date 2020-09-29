import React from 'react';
import './ChannelSideBarItemList.css';

interface Channel {
  id: number;
  name: string;
  owner: number;
  type: string;
  allRead: boolean;
}
type Props = {
  channels: Channel[];
  selectedChannel: Channel;
  selectChannel: (channel: Channel) => void;
  type: string;
};
export const ChannelSideBarItemList: React.FC<Props> = ({ channels, selectedChannel, selectChannel, type }) => {
  const filteredChannels: [Channel, string][] = channels.filter(c => c.type === type).map(channel => {
    const classes = ['ChannelSideBarItemList_item'];
    const selected = channel.id === selectedChannel.id;
    if (selected) {
      classes.push('ChannelSideBarItemList_selected');
    }
    if (!channel.allRead) {
      classes.push('ChannelSideBarItemList_unread');
    }
    const className = classes.join(' ');
    return ([channel, className]);
  });
  return (
    <ul className="ChannelSideBarItemList_list">
      {filteredChannels.map(([channel, className]) => (
        <li key={channel.id} className={className} onClick={() => selectChannel(channel)}>
          <span className="ChannelSideBarItemList_channelName">#{channel.name}</span>
        </li>
      ))}
    </ul>
  );
};

