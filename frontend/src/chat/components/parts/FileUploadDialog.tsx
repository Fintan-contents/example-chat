import React from 'react';
import {CloseButton, PositiveButton} from 'chat/components/basics';
import './FileUploadDialog.css';
import { Logger } from 'framework';

type Props = {
  channelId: number;
  fileInput: React.RefObject<HTMLInputElement>
  imgSrc: string;
  uploadFile: (channelId: number, file: File) => void;
  modalClose: () => void;
}

export const FileUploadDialog: React.FC<Props> = ({channelId, fileInput, imgSrc, uploadFile, modalClose}) => {
  Logger.debug('rendering FileUploadForm...', fileInput);

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    if (fileInput.current !== null && fileInput.current.files !== null && fileInput.current.files.length > 0) {
      uploadFile(channelId, fileInput.current.files[0]);
      modalClose();
    }
  };

  return (
    <React.Fragment>
      <div className="FileUploadDialog_header">
        <h2 className="FileUploadDialog_title">アップロードファイル</h2>
        <span className="FileUploadDialog_close">
          <CloseButton onClick={() => modalClose()} />
        </span>
      </div>
      <form className="FileUploadDialog_form" onSubmit={handleSubmit}>
        <img className="FileUploadDialog_image" src={imgSrc} alt="alt" />
        <div className="FileUploadDialog_buttonGroup">
          <PositiveButton type="submit">アップロード</PositiveButton>
        </div>
      </form>
    </React.Fragment>
  );
};
