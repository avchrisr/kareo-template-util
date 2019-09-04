import React, { createContext, useContext, useEffect, useState } from 'react';

export const RootContext = createContext();

export default ({children}) => {

    const prevAuth = window.localStorage.getItem('authenticated') || 'false';
    const prevAuthBody = window.localStorage.getItem('authBody') || '';
    const [authenticated, setAuthenticated] = useState(prevAuth);
    const [authBody, setAuthBody] = useState(prevAuthBody);

    useEffect(() => {
        window.localStorage.setItem('authenticated', authenticated);
        window.localStorage.setItem('authBody', authBody);
    }, [authenticated, authBody]);

    const authContext = {
        authenticated, setAuthenticated,
        authBody, setAuthBody
    };

    return (
        <RootContext.Provider value={authContext}>
            {children}
        </RootContext.Provider>
    );
};
