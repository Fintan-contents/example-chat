import React, {useEffect} from 'react';
import { BackendService } from 'chat/backend';
import { Logger, usePageTitle } from 'framework';
import {useHistory} from 'react-router-dom';
import {Loading} from 'chat/components/parts';

interface Channel {
  id: number;
  name: string;
  owner: number;
  type: string;
  allRead: boolean;
}

const ChannelRouting: React.FC = () => {
  const history = useHistory();

  usePageTitle('チャンネル移動');

  Logger.debug('rendering ChannelRouting...');

  useEffect(() => {
    BackendService.findAllChannel()
      .then(response => {
        Logger.debug(response);

        const lastChannelId = window.localStorage.getItem('last_channel');
        if (lastChannelId) {
          history.push('/channels/' + lastChannelId);
        } else {
          const channel: Channel = response[0];
          window.localStorage.setItem('last_channel', channel.id.toString());
          history.push('/channels/' + channel.id);
        }
      });
  }, [history]);

  return <Loading />;
};

export default ChannelRouting;
