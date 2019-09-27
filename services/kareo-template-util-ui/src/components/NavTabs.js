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

import { AuthContext, ReducerActionTypes } from "../App";

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
    const [searchTemplateEnv, setSearchTemplateEnv] = useState('dev');
    const [type, setType] = useState('either');
    const [title, setTitle] = useState('');
    const [author, setAuthor] = useState('');
    const [version, setVersion] = useState('');
    const [username, setUsername] = useState('');
    const [templateId, setTemplateId] = useState('');
    const [isUsernameFieldDisabled, setUsernameFieldDisabled] = useState(false);
    const [isPartialTitleMatch, setPartialTitleMatch] = useState(false);
    const [isSearchButtonDisabled, setSearchButtonDisabled] = useState(false);
    const [errorMessages, setErrorMessages] = useState([]);
    const [searchResults, setSearchResults] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

    // template update page fields
    const [updateTemplateEnv, setUpdateTemplateEnv] = useState('dev');
    const [currentTemplateId, setCurrentTemplateId] = useState('');
    const [currentTemplateTitle, setCurrentTemplateTitle] = useState('');
    const [newTemplateTitle, setNewTemplateTitle] = useState('');
    const [newTemplateAuthor, setNewTemplateAuthor] = useState('');
    const [newTemplateVersion, setNewTemplateVersion] = useState('');
    // const [isSubmitButtonDisabled, setSubmitButtonDisabled] = useState(false);
    // const [errorMessages, setErrorMessages] = useState([]);
    // const [responseMessage, setResponseMessage] = useState('');

    // template copy page fields
    const [fromEnv, setFromEnv] = useState('dev');
    const [toEnv, setToEnv] = useState('dev');
    const [fromType, setFromType] = useState('user');
    const [toType, setToType] = useState('user');
    const [fromUsername, setFromUsername] = useState('');
    const [toUsername, setToUsername] = useState('');
    const [templateIds, setTemplateIds] = useState('');
    const [createOrReplaceSystemTemplate, setCreateOrReplaceSystemTemplate] = useState('create');
    const [systemTemplateIdToReplace, setSystemTemplateIdToReplace] = useState('');

    const value = {
        searchTemplateEnv, setSearchTemplateEnv,
        type, setType,
        title, setTitle,
        author, setAuthor,
        version, setVersion,
        username, setUsername,
        templateId, setTemplateId,
        isUsernameFieldDisabled, setUsernameFieldDisabled,
        isPartialTitleMatch, setPartialTitleMatch,
        isSearchButtonDisabled, setSearchButtonDisabled,
        errorMessages, setErrorMessages,
        searchResults, setSearchResults,
        page, setPage, rowsPerPage, setRowsPerPage,
        updateTemplateEnv, setUpdateTemplateEnv,
        currentTemplateId, setCurrentTemplateId,
        currentTemplateTitle, setCurrentTemplateTitle,
        newTemplateTitle, setNewTemplateTitle,
        newTemplateAuthor, setNewTemplateAuthor,
        newTemplateVersion, setNewTemplateVersion,
        fromEnv, setFromEnv,
        toEnv, setToEnv,
        fromType, setFromType,
        toType, setToType,
        fromUsername, setFromUsername,
        toUsername, setToUsername,
        templateIds, setTemplateIds,
        createOrReplaceSystemTemplate, setCreateOrReplaceSystemTemplate,
        systemTemplateIdToReplace, setSystemTemplateIdToReplace
    };

    return (
        <TemplateContext.Provider value={value}>
            {children}
        </TemplateContext.Provider>
    );
};

const NavTabs = (props) => {
    const routeResult = useRoutes(routes);
    const classes = useStyles();

    const { dispatch } = useContext(AuthContext);

    const isAuthenticated = window.localStorage.getItem('isAuthenticated');
    const userId = window.localStorage.getItem('userId');
    const userFirstname = window.localStorage.getItem('userFirstname');
    const jwt = window.localStorage.getItem('jwt');

    const handleLogOut = (event) => {
        dispatch({
            type: ReducerActionTypes.LOGOUT
        });

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

                {isAuthenticated === 'true' &&
                    <div className={classes.profileBar}>
                        <div style={{lineHeight: '2rem'}}><Button size="large" variant="text">Welcome {userFirstname}!</Button></div>
                        <Button onClick={handleLogOut} variant="outlined">Log Out</Button>

                        {/* <div>{authBody}</div> */}
                    </div>}

                {routeResult}

                {/*{routeResult || <NotFoundPage />}*/}
            </div>
        </Provider>
    );
};

export default NavTabs;
