import React from 'react';

export interface LoginSwitch {
  login: () => Promise<void>;
  logout: () => Promise<void>;
  isLogin: boolean;
  user: {
    accountId: string;
    userName: string;
    mailAddress: string;
  };
}

export default React.createContext<LoginSwitch>({} as LoginSwitch);

