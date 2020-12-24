import React, {useEffect, useState} from 'react';
import './MessageExportForm.css';
import {Link, useParams} from 'react-router-dom';
import { BackendService } from 'chat/backend';
import { Logger, usePageTitle, useDownloader } from 'framework';
import {Button, Form, PositiveButton, Title} from 'chat/components/basics';
import {Loading} from 'chat/components/parts';

interface Channel {
  id: number;
  name: string;
}

const MessageExportForm: React.FC = () => {

  Logger.debug('rendering MessageExportForm...');

  const {channelId} = useParams<{channelId: string}>();
  const [channel, setChannels] = useState<Channel>();

  const [processing, setProcessing] = useState(false);

  const download = useDownloader();

  usePageTitle(channel && `${channel.name} | エクスポート`);

  useEffect(() => {
    BackendService.findChannel(Number(channelId))
      .then(response => {
        Logger.debug(response);
        setChannels(response);
      });
  }, [channelId]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    setProcessing(true);
    try {
      event.preventDefault();

      const { fileKey } = await BackendService.exportMessageHistory(channelId);

      const blob = await BackendService.downloadMessageHistory(channelId, fileKey);
      const filename = `${channel && channel.name}-history-archive.csv`;
      download(blob, filename);
    } finally {
      setProcessing(false);
    }
  };

  if(!channel) {
    return <Loading />;
  }

  return (
    <div>
      <nav className="MessageExportForm_nav">
        <Title>#{channel.name}のメッセージ履歴をエクスポートする</Title>
      </nav>
      <Form onSubmit={handleSubmit}>
        <p>#{channel.name}の全てのメッセージをCSV形式でエクスポートします。</p>
        <span className="MessageExportForm_buttonGroup">
          <Link to={'/channels/' + channelId + '/settings'}><Button>キャンセル</Button></Link>
          <PositiveButton type="submit" disabled={processing}>エクスポート</PositiveButton>
        </span>
      </Form>
    </div>
  );
};

export default MessageExportForm;
