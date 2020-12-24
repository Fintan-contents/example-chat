import React, {useEffect, useState} from 'react';
import {useSelect, stringField, useValidation, Logger, useSystemMessages} from 'framework';
import {Button, Form, Select, PositiveButton, Title, ValidationError} from 'chat/components/basics';
import './ChannelMemberInvite.css';
import {Link, useHistory, useParams} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import {InputItem} from 'chat/components/parts';

interface Member {
  id: number;
  name: string;
}

type FormFields = {
  accountId: string;
};

const ChannelMemberInvite: React.FC = () => {
  Logger.debug('rendering ChannelMemberInvite...');

  const history = useHistory();
  const {channelId} = useParams<{channelId: string}>();
  const [accountId, accountIdAttributes] = useSelect('');
  const [members, setMembers] = useState<Member[]>([]);
  const [formError, setFormError] = useState<string>('');

  useEffect(() => {
    BackendService.findAllMembers(Number(channelId))
      .then(response => {
        Logger.debug(response);
        setMembers(response);
      });
  }, [channelId]);

  const systemMessages = useSystemMessages();
  const { error, handleSubmit } = useValidation<FormFields>({
    accountId: stringField()
      .required(systemMessages('errors.select', '招待するユーザー'))
  });

  const inviteMember = async (event: React.FormEvent<HTMLFormElement>) => {
    const response = await BackendService.postMembers(Number(channelId), Number(accountId));
    if(response === 'Forbidden'){
      setFormError('このチャンネルには招待できません。');
      return;
    }
    history.push('/channels/' + channelId);
  };

  return (
    <div>
      <nav>
        <Title>チャンネルにユーザーを招待する</Title>
      </nav>
      <Form onSubmit={handleSubmit({accountId}, inviteMember, () => setFormError(''))}>
        <ValidationError message={formError} />
        <InputItem title="名前" error={error.accountId}>
          <Select name="members" {...accountIdAttributes}>
            <option value=''/>
            {members.map(member => (
              <option key={member.id} value={member.id}>{member.name}</option>
            )
            )}
          </Select>
        </InputItem>
        <span className="ChannelMemberInvite_buttonGroup">
          <Link to={'/channels/' + channelId}><Button>キャンセル</Button></Link>
          <PositiveButton type="submit">ユーザーを招待する</PositiveButton>
        </span>
      </Form>
    </div>
  );
};

export default ChannelMemberInvite;
