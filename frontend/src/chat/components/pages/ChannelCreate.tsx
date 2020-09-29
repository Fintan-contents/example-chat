import React, { useState } from 'react';
import {useInput, stringField, useValidation, Logger, useSystemMessages} from 'framework';
import {Button, Form, Input, PositiveButton, Title, ValidationError} from 'chat/components/basics';
import './ChannelCreate.css';
import {Link, useHistory} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import {InputItem} from 'chat/components/parts';

type FormFields = {
  channelName: string;
};

const ChannelCreate: React.FC = () => {
  Logger.debug('rendering ChannelCreate...');

  const history = useHistory();
  const [channelName, channelNameAttributes] = useInput('');
  const [formError, setFormError] = useState<string>('');

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    channelName: stringField()
      .required(systemMessages('errors.required', 'チャンネル名'))
      .maxLength(50, systemMessages('errors.length.max', 'チャンネル名', '50'))
  });

  const createChannel = async (event: React.FormEvent<HTMLFormElement>) => {
    const response = await BackendService.postChannel(channelName);
    if (response === 'conflict')  {
      setFormError(systemMessages('errors.conflict', 'チャンネル名'));
      return;
    }
    history.push('/channels/top');
  };

  return (
    <div>
      <nav>
        <Title>チャンネルを作成する</Title>
      </nav>
      <div className="ChannelCreate_content">
        <p>チャンネルとはチームがコミュニケーションを取る場所です。特定のトピックに基づいてチャンネルを作ると良いでしょう (例: #マーケティング)。</p>
        <Form onSubmit={handleSubmit({channelName}, createChannel, () => setFormError(''))}>
          <ValidationError message={formError}/>
          <InputItem title="名前" error={error.channelName}>
            <Input type="text" maxLength={50} {...channelNameAttributes} placeholder={'例：計画-予算'}/>
          </InputItem>
          <div className="ChannelCreate_buttonGroup">
            <Link to={'/channels/top'}><Button type="button">キャンセル</Button></Link>
            <PositiveButton type="submit">作成</PositiveButton>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default ChannelCreate;
