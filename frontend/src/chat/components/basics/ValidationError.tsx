import React from 'react';
import './ValidationError.css';

type Props = {
  message: string;
}

export const ValidationError: React.FC<Props> = ({ message }) => {
  if (message) {
    return (<div className="ValidationError">{message}</div>);
  }
  return null;
};
