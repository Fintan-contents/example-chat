import React from 'react';
import { useInput, Logger } from 'framework';
import { FileUploadDialog } from './FileUploadDialog';
import './MessageForm.css';
import {Input} from 'chat/components/basics';

type Props = {
  channelId: number;
  sendMessage: (channelId: number, text: string) => void;
  uploadFile: (channelId: number, file: File) => void;
}

export const MessageForm: React.FC<Props> = (props: Props) => {
  const [message, messageAttributes, setMessage] = useInput();

  Logger.debug('rendering MessageForm...', props.channelId, message);

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (message !== '') {
      props.sendMessage(props.channelId, message);
      setMessage('');
    }
  };

  const inputFileElementId = 'fileInput';
  const handleUpload = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    const inputFileElement = document.getElementById(inputFileElementId) as HTMLInputElement;
    if (inputFileElement !== null) {
      inputFileElement.value = '';
      inputFileElement.click();
    }
  };

  return (
    <React.Fragment>
      <form className="MessageForm_form" onSubmit={handleSubmit}>
        <Input className="MessageForm_input" type="text" maxLength={500} {...messageAttributes} />
        <button type="submit" className="MessageForm_sendButton" >送信</button>
        <button className="MessageForm_fileButton" onClick={handleUpload}>ファイル</button>
      </form>
      <FileUploadDialog channelId={props.channelId} uploadFile={props.uploadFile} inputElementId={inputFileElementId} />
    </React.Fragment>
  );
};
