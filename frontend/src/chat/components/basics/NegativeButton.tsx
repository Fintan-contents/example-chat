import React from 'react';
import './NegativeButton.css';

export const NegativeButton: React.FC<React.ButtonHTMLAttributes<HTMLButtonElement>> = (props) => {
  return (
    <button className="NegativeButton" {...props}>{props.children}</button>
  );
};
