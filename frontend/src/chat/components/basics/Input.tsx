import React from 'react';
import './Input.css';

export const Input: React.FC<React.InputHTMLAttributes<HTMLInputElement>> = (props) => {
  return (
    <input className="Input" {...props}/>
  );
};
