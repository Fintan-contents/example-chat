import React from 'react';
import ReactDOM from 'react-dom';
import './Modal.css';

type Props = {
  open: boolean;
  children: React.ReactNode;
}

export const Modal: (props: Props) => React.ReactPortal | null = (props: Props) => {
  return ReactDOM.createPortal(
    (
      props.open ?
        (<div className="Modal_background">
          <div className="Modal_dialog">{props.children}</div>
        </div> )
        : null
    ),
    document.body,
  );
};