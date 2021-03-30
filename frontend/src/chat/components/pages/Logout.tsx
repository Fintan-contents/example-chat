import React, {useContext, useEffect} from 'react';
import { Logger, usePageTitle } from 'framework';
import {useHistory} from 'react-router-dom';
import {Loading} from 'chat/components/parts';
import LoginContext from '../../context/LoginContext';

const Logout: React.FC = () => {
  const history = useHistory();
  const loginContext = useContext(LoginContext);

  usePageTitle('ログアウト');

  Logger.debug('rendering Logout...');

  useEffect(() => {
    loginContext.logout().then(() => {
      history.push('/');
    });
  }, [loginContext, history]);

  return <Loading />;
};

export default Logout;
