import React, {useEffect, useState} from 'react';
import {useInput, stringField, useValidation, Logger, usePageTitle, useSystemMessages} from 'framework';
import {Button, Form, Input, PositiveButton, Title, ValidationError} from 'chat/components/basics';
import './ChannelRename.css';
import {Link, useHistory, useParams} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import {InputItem, Loading} from 'chat/components/parts';

interface Channel {
  id: number;
  name: string;
}

type FormFields = {
  channelName: string;
};

const ChannelRename: React.FC = () => {
  Logger.debug('rendering ChannelRename...');

  const history = useHistory();
  const [channelName, channelNameAttributes] = useInput('');

  const {channelId} = useParams<{channelId: string}>();

  const [channel, setChannels] = useState<Channel>();

  usePageTitle(channel && `${channel.name} | 変更`);

  const [formError, setFormError] = useState<string>('');

  useEffect(() => {
    BackendService.findChannel(Number(channelId))
      .then(response => {
        Logger.debug(response);
        setChannels(response);
      });
  }, [channelId]);

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    channelName: stringField()
      .required(systemMessages('errors.required', 'チャンネル名'))
      .maxLength(50, systemMessages('errors.length.max', 'チャンネル名', '50'))
  });

  const changeChannelName = async (event: React.FormEvent<HTMLFormElement>) => {
    const response = await BackendService.putChannel(Number(channelId), channelName);
    if (response === 'conflict')  {
      setFormError(systemMessages('errors.conflict', 'チャンネル名'));
      return;
    }
    history.push('/channels/' + channelId);
  };

  if(!channel) {
    return <Loading />;
  }

  return (
    <div>
      <nav className="ChannelRename_nav">
        <Title>#{channel.name}のチャンネル名を変更する</Title>
      </nav>
      <Form onSubmit={handleSubmit({channelName}, changeChannelName, () => setFormError(''))}>
        <ValidationError message={formError}/>
        <InputItem title="名前" error={error.channelName}>
          <Input type="text" maxLength={50} {...channelNameAttributes}/>
        </InputItem>
        <span className="ChannelRename_buttonGroup">
          <Link to={'/channels/' + channelId + '/settings'}><Button type="button">キャンセル</Button></Link>
          <PositiveButton type="submit">チャンネル名を変更する</PositiveButton>
        </span>
      </Form>
    </div>
  );
};

export default ChannelRename;
