import React, { useRef, useState } from 'react';
import {CloseButton, PositiveButton} from 'chat/components/basics';
import './FileUploadDialog.css';
import { useSystemMessages, Logger } from 'framework';

type Props = {
  channelId: number;
  uploadFile: (channelId: number, file: File) => void;
  inputElementId: string;
}

export const FileUploadDialog: React.FC<Props> = (props: Props) => {
  const [imgSrc, setImgSrc] = useState<string>();
  // file要素は非制御コンポーネントのため、Refを使って値を参照する
  const fileInput: React.RefObject<HTMLInputElement> = useRef<HTMLInputElement>(null);
  const systemMessages = useSystemMessages();
  const notAllowFileExtensionMessage = systemMessages('errors.upload.notAllowFileExtension');
  const fileUnreadMessage = systemMessages('errors.upload.fileUnread');

  Logger.debug('rendering FileUploadForm...', fileInput);

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    if (fileInput.current !== null && fileInput.current.files !== null && fileInput.current.files.length > 0) {
      // ファイル選択したらダイアログ開くようにしておく
      props.uploadFile(props.channelId, fileInput.current.files[0]);

      // ファイル投稿機能を実現するため、ファイルのinput要素を使用するが、これについては制御されたコンポーネントとして実装できない。
      // そのため、非制御コンポーネント(https://ja.reactjs.org/docs/uncontrolled-components.html#the-file-input-tag)として実装し、
      // 例外的にDOMへアクセスするものとする。
      const dialogElement = document.getElementById(dialogElementId) as HTMLDialogElement;
      dialogElement.close();
    }
  };

  const dialogElementId = 'uploadDialog';

  const handleChange = () => {
    if (fileInput.current !== null && fileInput.current.files !== null && fileInput.current.files.length > 0) {
      const file = fileInput.current.files[0];
      const extension = file.name.split('.').pop();
      const allowExtension = ['jpg', 'jpeg', 'png', 'gif'];
      if(!extension || !allowExtension.includes(extension.toLowerCase())) {
        // エラーを表示する場所がないため、alert()する
        window.alert(notAllowFileExtensionMessage);
        return;
      }

      const dialogElement = document.getElementById(dialogElementId) as HTMLDialogElement;
      const image = new Image();
      image.src = URL.createObjectURL(file);
      image.onerror = (errorEvent) => {
        window.alert(fileUnreadMessage);
        dialogElement.close();
      };

      setImgSrc(image.src);
      dialogElement.showModal();
    }
  };

  const handleClose: React.MouseEventHandler<HTMLButtonElement> = (event) => {
    const dialogElement = document.getElementById(dialogElementId) as HTMLDialogElement;
    dialogElement.close();
  };

  return (
    <dialog id={dialogElementId} className="FileUploadDialog_dialog">
      <div className="FileUploadDialog_header">
        <h2 className="FileUploadDialog_title">アップロードファイル</h2>
        <CloseButton onClick={handleClose} />
      </div>
      <form className="FileUploadDialog_form" onSubmit={handleSubmit}>
        <input type="file"
          id={props.inputElementId}
          className="FileUploadDialog_hidden"
          ref={fileInput}
          accept={'image/*'}
          onChange={handleChange} />
        <img className="FileUploadDialog_image" src={imgSrc} alt="alt" />
        <div className="FileUploadDialog_buttonGroup">
          <PositiveButton type="submit">アップロード</PositiveButton>
        </div>
      </form>
    </dialog>
  );
};
