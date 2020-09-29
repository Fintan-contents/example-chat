import React from 'react';
import './Select.css';

export const Select: React.FC<React.SelectHTMLAttributes<HTMLSelectElement>> = (props) => {
  return (
    <select className="Select" {...props}>{props.children}</select>
  );
};
