import React from 'react';
import './Checkbox.css';

export const Checkbox: React.FC<React.InputHTMLAttributes<HTMLInputElement>> = (props) => {
  return (
    <input type="checkbox" className="Checkbox" {...props}/>
  );
};
