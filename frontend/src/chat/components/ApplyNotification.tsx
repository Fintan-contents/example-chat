import React, { useEffect, useContext, useState } from 'react';
import { NotificationService } from 'framework';
import LoginContext from 'chat/context/LoginContext';
import { BackendService } from 'chat/backend';

type Props = {
  children: React.ReactNode;
}

const ApplyNotification: React.FC<Props> = ({ children }) => {
  const [websocketUri, setWebsocketUri] = useState();
  const { isLogin } = useContext(LoginContext);

  useEffect(() => {
    BackendService.getNotificationUrl().then((response) => {
      setWebsocketUri(response.websocketUri);
    });
  }, []);

  useEffect(() => {
    if (isLogin) {
      if (websocketUri) {
        NotificationService.connect(websocketUri);
      }
      return () => {
        if (websocketUri) {
          NotificationService.disconnect();
        }
      };
    }
  }, [isLogin, websocketUri]);
  return (
    <React.Fragment>
      {React.Children.only(children)}
    </React.Fragment>
  );
};

export default ApplyNotification;
