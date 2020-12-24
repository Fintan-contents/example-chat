import React, {useRef, useState} from 'react';
import {useInput, Logger, useSystemMessages} from 'framework';
import { FileUploadDialog } from './FileUploadDialog';
import './MessageForm.css';
import {Input, Modal} from 'chat/components/basics';

type Props = {
  channelId: number;
  sendMessage: (channelId: number, text: string) => void;
  uploadFile: (channelId: number, file: File) => void;
}

export const MessageForm: React.FC<Props> = (props: Props) => {
  const [message, messageAttributes, setMessage] = useInput();
  const [imgSrc, setImgSrc] = useState<string>();
  // file要素は非制御コンポーネントのため、Refを使って値を参照する
  const fileInput: React.RefObject<HTMLInputElement> = useRef<HTMLInputElement>(null);
  const [open, setOpen] = useState<boolean>(false);

  const systemMessages = useSystemMessages();
  const notAllowFileExtensionMessage = systemMessages('errors.upload.notAllowFileExtension');
  const fileUnreadMessage = systemMessages('errors.upload.fileUnread');

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
  const handleUploadFileChange = () => {
    if (fileInput.current !== null && fileInput.current.files !== null && fileInput.current.files.length > 0) {
      const file = fileInput.current.files[0];
      const extension = file.name.split('.').pop();
      const allowExtension = ['jpg', 'jpeg', 'png', 'gif'];
      if(!extension || !allowExtension.includes(extension.toLowerCase())) {
        // エラーを表示する場所がないため、alert()する
        window.alert(notAllowFileExtensionMessage);
        return;
      }
      const image = new Image();
      image.src = URL.createObjectURL(file);
      image.onerror = (errorEvent) => {
        window.alert(fileUnreadMessage);
        setOpen(false);
      };
      setImgSrc(image.src);
      setOpen(true);
    }
  };

  const modalClose = () => {
    setOpen(false);
  };

  return (
    <React.Fragment>
      <form className="MessageForm_form" onSubmit={handleSubmit}>
        <Input className="MessageForm_input" type="text" maxLength={500} {...messageAttributes} />
        <button type="submit" className="MessageForm_sendButton" >送信</button>
        <button className="MessageForm_fileButton" onClick={handleUpload}>ファイル</button>
        <input type="file" id={inputFileElementId} className="MessageForm_inputFile" ref={fileInput} accept={'image/*'} onChange={handleUploadFileChange} />
      </form>
      <Modal open={open}>
        <FileUploadDialog
          channelId={props.channelId}
          fileInput={fileInput}
          imgSrc={imgSrc ? imgSrc : ''}
          uploadFile={props.uploadFile}
          modalClose={modalClose}
        />
      </Modal>
    </React.Fragment>
  );
};
