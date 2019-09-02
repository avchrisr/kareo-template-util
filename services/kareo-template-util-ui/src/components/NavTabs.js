import React, { useState } from 'react';
import { makeStyles } from '@material-ui/styles';

import { navigate, useRoutes } from 'hookrouter';

import { Button, ButtonGroup } from '@material-ui/core';

import PhoneIcon from '@material-ui/icons/Phone';
import FavoriteIcon from '@material-ui/icons/Favorite';
import PersonPinIcon from '@material-ui/icons/PersonPin';

import TemplateSearch from "./TemplateSearch";
import TemplateCopy from "./TemplateCopy";

const useStyles = makeStyles({
    container: {
        // display: 'grid'
    },
});

const routes = {
    // '/': () => <HomePage />,
    '/template-search': () => <TemplateSearch />,
    '/template-copy': () => <TemplateCopy />
};

function NavTabs() {
    const routeResult = useRoutes(routes);
    const classes = useStyles();

    return (
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

            {routeResult}

            {/*{routeResult || <NotFoundPage />}*/}
        </div>
    );
}

export default NavTabs;
