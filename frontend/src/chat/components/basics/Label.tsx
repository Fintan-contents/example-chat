import React from 'react';
import './Label.css';

export const Label: React.FC<React.LabelHTMLAttributes<HTMLLabelElement>> = (props) => {
  return (
    <label className="Label" {...props}>{props.children}</label>
  );
};
