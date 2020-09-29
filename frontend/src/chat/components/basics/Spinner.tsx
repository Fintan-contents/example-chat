import React from 'react';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faSpinner} from '@fortawesome/free-solid-svg-icons';

export const Spinner: React.FC = () => {
  return (
    <FontAwesomeIcon icon={faSpinner} size={'6x'} spin={true} />
  );
};

