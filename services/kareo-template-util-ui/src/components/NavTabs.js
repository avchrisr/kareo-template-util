import React, { createContext, useContext, useState } from 'react';
import { makeStyles } from '@material-ui/styles';

import { navigate, useRoutes } from 'hookrouter';

import { Button, ButtonGroup } from '@material-ui/core';

import PhoneIcon from '@material-ui/icons/Phone';
import FavoriteIcon from '@material-ui/icons/Favorite';
import PersonPinIcon from '@material-ui/icons/PersonPin';

import TemplateSearch from "./TemplateSearch";
import TemplateCopy from "./TemplateCopy";
import TemplateUpdate from "./TemplateUpdate";

import { RootContext } from "../RootContext";

const useStyles = makeStyles({
    container: {
        // display: 'grid'
    },
    profileBar: {
        display: 'grid',
        gridTemplateColumns: '5fr 1fr',
        margin: '0.5rem',
        // height: '1rem',

        // border: '1px solid blue'
    },
    logoutButton: {
        // width: '20%',
        // textAlign: 'right'
    }
});

export const TemplateContext = createContext();

const routes = {
    // '/': () => <HomePage />,
    '/template-search': () => <TemplateSearch />,
    '/template-copy': () => <TemplateCopy />,
    '/template-update': () => <TemplateUpdate />
};

const Provider = ({children}) => {

    // template search page fields
    const [type, setType] = useState('either');
    const [title, setTitle] = useState('');
    const [author, setAuthor] = useState('');
    const [version, setVersion] = useState('');
    const [username, setUsername] = useState('');
    const [isUsernameFieldDisabled, setUsernameFieldDisabled] = useState(false);
    const [isPartialTitleMatch, setPartialTitleMatch] = useState(false);
    const [isSearchButtonDisabled, setSearchButtonDisabled] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

    // template update page fields
    const [currentTemplateId, setCurrentTemplateId] = useState('');
    const [currentTemplateTitle, setCurrentTemplateTitle] = useState('');
    const [newTemplateTitle, setNewTemplateTitle] = useState('');
    const [newTemplateAuthor, setNewTemplateAuthor] = useState('');
    const [newTemplateVersion, setNewTemplateVersion] = useState('');
    // const [isSubmitButtonDisabled, setSubmitButtonDisabled] = useState(false);
    // const [errorMessages, setErrorMessages] = useState([]);
    // const [responseMessage, setResponseMessage] = useState('');

    // template copy page fields
    // ...

    const value = {
        type, setType,
        title, setTitle,
        author, setAuthor,
        version, setVersion,
        username, setUsername,
        isUsernameFieldDisabled, setUsernameFieldDisabled,
        isPartialTitleMatch, setPartialTitleMatch,
        isSearchButtonDisabled, setSearchButtonDisabled,
        errorMessage, setErrorMessage,
        searchResults, setSearchResults,
        page, setPage, rowsPerPage, setRowsPerPage,
        currentTemplateId, setCurrentTemplateId,
        currentTemplateTitle, setCurrentTemplateTitle,
        newTemplateTitle, setNewTemplateTitle,
        newTemplateAuthor, setNewTemplateAuthor,
        newTemplateVersion, setNewTemplateVersion
    };

    return (
        <TemplateContext.Provider value={value}>
            {children}
        </TemplateContext.Provider>
    );
};

export default function NavTabs() {
    const routeResult = useRoutes(routes);
    const classes = useStyles();

    const { authenticated, setAuthenticated, authBody, setAuthBody } = useContext(RootContext);

    console.log(`authenticated = ${authenticated}`);
    console.log(`authBody = ${authBody}`);

    const handleLogOut = (event) => {
        setAuthenticated('false');
        setAuthBody('');
        navigate('/', true);
    };

    // TODO: display username
    // const authBodyJson = JSON.parse(authBody);

    return (
        <Provider>
            <div>
                <ButtonGroup
                    color="primary"
                    fullWidth
                    size="large"
                    variant="outlined"
                >
                    <Button onClick={() => navigate('/template-search', true)}>Search Templates</Button>
                    <Button onClick={() => navigate('/template-copy', true)}>Copy Templates</Button>
                    <Button onClick={() => navigate('/template-update', true)}>Update Templates</Button>
                </ButtonGroup>

                {authenticated === 'true' &&
                    <div className={classes.profileBar}>
                        <div style={{lineHeight: '2rem'}}><Button size="large" variant="text">Chris</Button></div>
                        <Button onClick={handleLogOut} variant="outlined">Log Out</Button>

                        {/* <div>{authBody}</div> */}
                    </div>}

                {routeResult}

                {/*{routeResult || <NotFoundPage />}*/}
            </div>
        </Provider>
    );
}
