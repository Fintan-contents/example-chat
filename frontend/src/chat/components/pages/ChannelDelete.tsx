import React, {useEffect, useState} from 'react';
import './ChannelDelete.css';
import {Link, useHistory, useParams} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import { Logger, usePageTitle } from 'framework';
import {Button, Form, Checkbox, Label, NegativeButton, Title, ValidationError} from 'chat/components/basics';
import {Loading} from 'chat/components/parts';

interface Channel {
  id: number;
  name: string;
}

const ChannelDelete: React.FC = () => {
  Logger.debug('rendering ChannelDelete...');

  const history = useHistory();
  const [checked, setCheck] = useState<boolean>(false);

  const {channelId} = useParams<{channelId: string}>();
  const [channel, setChannels] = useState<Channel>();
  const [message, setMessage] = useState<string>('');

  usePageTitle(channel && `${channel.name} | 削除`);

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

  const deleteChannel = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const status = await BackendService.deleteChannel(Number(channelId));
    if(status === 'Forbidden'){
      setMessage('チャンネルオーナー以外はチャンネルを削除できません。');
      return;
    }

    history.push('/channels/top');
  };

  return (
    <div>
      <nav className="ChannelDelete_nav">
        <Title>#{channel.name}を削除する</Title>
      </nav>
      <div className="ChannelDelete_content">
        <ValidationError message={message}/>
        <p className="ChannelDelete_description">
          #{channel.name} を本当に削除しますか？削除後は元に戻すことはできません。
        </p>
        <Form onSubmit={deleteChannel}>
          <Label>
            <Checkbox onChange={() => setCheck(!checked)}/>
              はい、完全に削除します。
          </Label>
          <span className="ChannelDelete_buttonGroup">
            <Link to={'/channels/' + channelId + '/settings'}><Button>キャンセル</Button></Link>
            <NegativeButton type="submit" disabled={!checked}>チャンネルを削除する</NegativeButton>
          </span>
        </Form>
      </div>
    </div>
  );
};

export default ChannelDelete;
