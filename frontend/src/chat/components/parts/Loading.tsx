import React from 'react';
import { Spinner } from 'chat/components/basics';
import './Loading.css';

export const Loading: React.FC = () => {
  return (
    <div className="Loading_content">
      <Spinner/>
    </div>
  );
};
