import React from 'react';
import { Label, ValidationError } from 'chat/components/basics';
import './InputItem.css';

type Props = {
  title: string;
  children: React.ReactNode;
  error: string;
}

export const InputItem: React.FC<Props> = ({ title, children, error }) => {
  return (
    <div className="InputItem_item">
      <Label>{title}</Label>
      {children}
      <ValidationError message={error}/>
    </div>
  );
};

