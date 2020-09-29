import React, { useEffect, useContext } from 'react';
import { NotificationService } from 'framework';
import LoginContext from 'chat/context/LoginContext';
import { BackendService } from 'chat/backend';

type Props = {
  children: React.ReactNode;
}

const ApplyNotification: React.FC<Props> = ({ children }) => {
  const { isLogin } = useContext(LoginContext);
  useEffect(() => {
    if (isLogin) {
      const urlSupplier = async () => {
        const { websocketUri } = await BackendService.getNotificationUrl();
        return websocketUri;
      };
      NotificationService.connect(urlSupplier);
      return () => {
        NotificationService.disconnect();
      };
    }
  }, [isLogin]);
  return (
    <React.Fragment>
      {React.Children.only(children)}
    </React.Fragment>
  );
};

export default ApplyNotification;
