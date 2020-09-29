import React from 'react';
import {Link} from 'react-router-dom';
import './Top.css';
import {PositiveButton, SystemMessage, Title} from 'chat/components/basics';

const Top: React.FC = () => {
  return (
    <div className="Top_content">
      <div className="Top_form">
        <Title><SystemMessage name='label.welcome'/></Title>
        <Link to="/signup" className="start-button">
          <PositiveButton>始める</PositiveButton>
        </Link>
      </div>
    </div>
  );
};

export default Top;
