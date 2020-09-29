import React from 'react';
import './Title.css';

export const Title: React.FC<React.HTMLAttributes<HTMLHeadingElement>> = (props) => {
  return (
    <h2 className="Title" {...props}>{props.children}</h2>
  );
};
