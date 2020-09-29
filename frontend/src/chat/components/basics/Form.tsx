import React from 'react';
import './Form.css';

export const Form: React.FC<React.FormHTMLAttributes<HTMLFormElement>> = (props) => {
  return (
    <form className="Form" {...props}>{props.children}</form>
  );
};
