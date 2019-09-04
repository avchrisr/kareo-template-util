import React, { useContext } from 'react';
import './App.css';

import NavTabs from './components/NavTabs';
import { RootContext } from './RootContext';
import SignIn from "./components/SignIn";

function App() {
    const { authenticated, authBody } = useContext(RootContext);

    console.log(`authenticated = ${authenticated}`);
    console.log(`typeof authenticated = ${typeof authenticated}`);

    return (
        <div className="App">
            {authenticated !== 'false' && <div><NavTabs /></div>}
            {authenticated === 'false' && <SignIn/>}
        </div>
    );
}

export default App;
