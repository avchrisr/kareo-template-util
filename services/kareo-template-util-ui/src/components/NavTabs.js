import React, { createContext, useContext, useState } from 'react';
import { makeStyles } from '@material-ui/styles';

import { navigate, useRoutes } from 'hookrouter';

import { Button, ButtonGroup } from '@material-ui/core';

import PhoneIcon from '@material-ui/icons/Phone';
import FavoriteIcon from '@material-ui/icons/Favorite';
import PersonPinIcon from '@material-ui/icons/PersonPin';

import TemplateSearch from "./TemplateSearch";
import TemplateCopy from "./TemplateCopy";
import {RootContext} from "../RootContext";

const useStyles = makeStyles({
    container: {
        // display: 'grid'
    },
});

export const TemplateSearchContext = createContext();

const routes = {
    // '/': () => <HomePage />,
    '/template-search': () => <TemplateSearch />,
    '/template-copy': () => <TemplateCopy />
};

const Provider = ({children}) => {

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
        page, setPage, rowsPerPage, setRowsPerPage };

    return (
        <TemplateSearchContext.Provider value={value}>
            {children}
        </TemplateSearchContext.Provider>
    );
};

export default function NavTabs() {
    const routeResult = useRoutes(routes);
    const classes = useStyles();

    const { authenticated, setAuthenticated, authBody, setAuthBody } = useContext(RootContext);

    const handleLogOut = (event) => {
        setAuthenticated('false');
    };

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
                </ButtonGroup>

                {authenticated === 'true' && <Button onClick={handleLogOut}>Log Out</Button>}

                {routeResult}

                {/*{routeResult || <NotFoundPage />}*/}
            </div>
        </Provider>
    );
}
