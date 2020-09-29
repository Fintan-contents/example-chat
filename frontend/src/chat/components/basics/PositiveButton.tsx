import React from 'react';
import './PositiveButton.css';

export const PositiveButton: React.FC<React.ButtonHTMLAttributes<HTMLButtonElement>> = (props) => {
  return (
    <button className="PositiveButton" {...props}>{props.children}</button>
  );
};
