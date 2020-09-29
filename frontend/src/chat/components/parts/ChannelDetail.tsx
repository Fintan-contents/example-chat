import React from 'react';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faUser} from '@fortawesome/free-solid-svg-icons';
import {Link} from 'react-router-dom';
import './ChannelDetail.css';
import {CloseButton, PositiveButton} from 'chat/components/basics';
import { Logger } from 'framework';

interface Member {
  id: number;
  name: string;
  isOwner: boolean;
}
type Props = {
  channelId: number;
  setShowDetail: (isShow: boolean) => void;
  members: Member[];
}

export const ChannelDetail: React.FC<Props> = ({ channelId, setShowDetail, members }) => {
  Logger.debug('rendering ChannelDetail...', channelId);

  return (
    <React.Fragment>
      <nav className="ChannelDetail_header">
        <h3 className="ChannelDetail_title">詳細</h3>
        <div>
          <CloseButton onClick={() => setShowDetail(false)}/>
        </div>
      </nav>
      <div className="ChannelDetail_subheader">
        <h3 className="ChannelDetail_title">参加者（{members.length}名）</h3>
      </div>
      <ul className="ChannelDetail_members">
        {members.map(member => (
          <li key={member.id}>
            <FontAwesomeIcon icon={faUser} />
            <span className="ChannelDetail_accountName">
              {member.name}
              {member.isOwner && '（オーナー）'}
            </span>
          </li>
        ))}
      </ul>
      <div className="ChannelDetail_invite"><Link to={`/channels/${channelId}/invite_member`}><PositiveButton>招待する</PositiveButton></Link></div>
    </React.Fragment>
  );
};
