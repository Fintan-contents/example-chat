import React, { useCallback } from 'react';
import WithSystemMessages from './WithSystemMessages';
import WithLoginContext from './WithLoginContext';
import ApplyNotification from './ApplyNotification';
import { BackendService } from 'chat/backend';

type Props = {
  children: React.ReactNode;
}

const Mixin: React.FC<Props> = ({ children }) => {
  const getSystemMessages = useCallback(() => {
    return BackendService.getSystemMessages();
  }, []);
  return (
    <WithLoginContext>
      <WithSystemMessages getSystemMessages={getSystemMessages}>
        <ApplyNotification>
          {children}
        </ApplyNotification>
      </WithSystemMessages>
    </WithLoginContext>
  );
};

export default Mixin;
