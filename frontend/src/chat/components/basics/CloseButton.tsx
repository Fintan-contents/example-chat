import React from 'react';
import './CloseButton.css';

export const CloseButton: React.FC<React.ButtonHTMLAttributes<HTMLButtonElement>> = (props) => {
  return (
    <div className="CloseButton_wrapper">
      <button className="CloseButton" {...props} />
    </div>
  );
};

