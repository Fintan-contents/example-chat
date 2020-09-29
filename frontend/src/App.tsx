import React from 'react';
import Mixin from './chat/components/Mixin';
import Routing from './chat/components/Routing';
import { Logger } from './framework/logging';
import { ErrorPage } from './chat/components/pages';
import './App.css';

const App = () => {
  Logger.debug('rendering App...');
  return (
    <React.Fragment>
      <ErrorPage>
        <Mixin>
          <Routing/>
        </Mixin>
      </ErrorPage>
    </React.Fragment>
  );
};

export default App;
