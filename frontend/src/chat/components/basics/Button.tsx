import React from 'react';
import './Button.css';

export const Button: React.FC<React.ButtonHTMLAttributes<HTMLButtonElement>> = (props) => {
  return (
    <button className="Button" {...props}>{props.children}</button>
  );
};
