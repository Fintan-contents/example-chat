import React, {useEffect, useState} from 'react';
import './ChannelLeave.css';
import {Link, useHistory, useParams} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import { Logger, usePageTitle } from 'framework';
import {Button, Form, NegativeButton, Title, ValidationError} from 'chat/components/basics';
import {Loading} from 'chat/components/parts';

interface Channel {
  id: number;
  name: string;
}

const ChannelLeave: React.FC = () => {
  Logger.debug('rendering ChannelDelete...');

  const history = useHistory();
  const {channelId} = useParams<{channelId: string}>();
  const [message, setMessage] = useState<string>('');

  const [channel, setChannels] = useState<Channel>();

  usePageTitle(channel && `${channel.name} | 退出`);

  useEffect(() => {
    BackendService.findChannel(Number(channelId))
      .then(response => {
        Logger.debug(response);
        setChannels(response);
      });
  }, [channelId]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const status = await BackendService.deleteMembers(Number(channelId));
    if(status === 'Forbidden'){
      setMessage('自分がオーナーのチャンネル、もしくはChatBotからは退出できません。');
      return;
    }
    history.push('/channels/top');
  };

  if(!channel) {
    return <Loading />;
  }

  return (
    <div>
      <nav className="ChannelLeave_nav">
        <Title>#{channel.name}チャンネルから退出する</Title>
      </nav>
      <div className="ChannelLeave_content">
        <ValidationError message={message}/>
        <p className="ChannelLeave_description">
          #{channel.name}チャンネルから退出しますか？<br />
          退出すると、再度招待されない限りこのチャンネルを参照できなくなります。
        </p>
        <Form onSubmit={handleSubmit}>
          <span className="ChannelLeave_buttonGroup">
            <Link to={'/channels/' + channelId + '/settings'}><Button>キャンセル</Button></Link>
            <NegativeButton type="submit">チャンネルから退出する</NegativeButton>
          </span>
        </Form>
      </div>
    </div>
  );
};

export default ChannelLeave;
