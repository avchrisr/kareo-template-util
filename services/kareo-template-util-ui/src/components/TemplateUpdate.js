import React, { useContext, useState } from 'react';
import { makeStyles } from '@material-ui/styles';
import { AppBar, Button, Checkbox, CircularProgress, FormControl, FormControlLabel, FormLabel, IconButton, InputLabel, LinearProgress,
    MenuItem, Paper, Radio, RadioGroup, Select, SnackbarContent, Table, TableBody, TableCell, TableHead,
    TableRow, TableFooter, TablePagination, TextField, Toolbar, Typography } from '@material-ui/core';

import MenuIcon from '@material-ui/icons/Menu';

import AccessAlarmIcon from '@material-ui/icons/AccessAlarm';
import axios from 'axios';
import _ from 'lodash';
import { TemplateSearchContext } from "./NavTabs";

const REACT_APP_NGINX_HOSTNAME = process.env.REACT_APP_NGINX_HOSTNAME || 'localhost';
const REACT_APP_NGINX_PORT = process.env.REACT_APP_NGINX_PORT || '3001';
const REACT_APP_API_VERSION = process.env.REACT_APP_API_VERSION || 'v1';


// TODO:
//  - signin and signout page interaction
//  - refine update template page UI
//  - get it to actually call BE service



const useStyles = makeStyles({
    container: {
        display: 'grid',
        gridTemplateColumns: '1fr 1fr 1fr',
        // gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',

        gridRowGap: '15px',

        // display: 'flex',
        // justifyContent: 'center',
        // alignItems: 'center',
        //   flexWrap: 'wrap',

        margin: '2rem',

        // border: '2px solid blue'
    },
    displayError: {
        color: 'red',
        // margin: '30px 50px'
    },
    divider: {
        marginTop: '1.5rem',
        borderBottom: '2px solid whitesmoke'
    },
    searchResults: {
        margin: '1.5rem'
    }
});


function createData(id, type, title, author, version, username, createdOn, updatedOn) {
    return { id, type, title, author, version, username, createdOn, updatedOn };
}

export default function TemplateUpdate() {
    const classes = useStyles();

    const [currentTemplateId, setCurrentTemplateId] = useState('');
    const [currentTemplateTitle, setCurrentTemplateTitle] = useState('');
    const [newTemplateTitle, setNewTemplateTitle] = useState('');
    const [newTemplateAuthor, setNewTemplateAuthor] = useState('');
    const [newTemplateVersion, setNewTemplateVersion] = useState('');

    const [isSubmitButtonDisabled, setSubmitButtonDisabled] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [responseMessage, setResponseMessage] = useState('');


    const handleInputValueChange = (event) => {

        switch (event.target.name) {
            case 'currentTemplateId':
                setCurrentTemplateId(event.target.value);
                break;
            case 'currentTemplateTitle':
                setCurrentTemplateTitle(event.target.value);
                break;
            case 'newTemplateTitle':
                setNewTemplateTitle(event.target.value);
                break;
            case 'newTemplateAuthor':
                setNewTemplateAuthor(event.target.value);
                break;
            case 'newTemplateVersion':
                setNewTemplateVersion(event.target.value);
                break;
            default:
                console.log(`Error - Unrecognized event.target.name = ${event.target.name}`);
                break;
        }

        // setErrorMessage('');
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        // INPUT VALIDATION
        if (_.isEmpty(currentTemplateId) || _.isEmpty(currentTemplateTitle)) {
            setErrorMessage('Current Template ID and Current Template Title are required.');
            return;
        }

        if (_.isEmpty(newTemplateTitle) && _.isEmpty(newTemplateAuthor) && _.isEmpty(newTemplateVersion)) {
            setErrorMessage('At least one field of new template is required.');
            return;
        }

        // disable the submit button until response comes back
        setSubmitButtonDisabled(true);


        setTimeout(() => {
            setResponseMessage('Success!');
            setSubmitButtonDisabled(false);
        }, 2000);



        /*
                let url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/${REACT_APP_API_VERSION}/templates`;
                let queryCount = 0;

                if (title) {
                    if (queryCount === 0) {
                        url += '?';
                    } else {
                        url += '&';
                    }
                    queryCount += 1;
                    url += `title=${title}`
                }

                console.log(`## URL = ${url}`);


                // TODO: implement user login and proper JWT usage
                const jwt = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjaHJpc3IiLCJpYXQiOjE1Njc1NDU3MjAsImV4cCI6MTU2ODE1MDUyMH0.ps-dOeKe4BA7hbZ7EWWfFHG-H-FQxMtRFhhaap2LIzaL_cQkbY2lXZuGdkWLgPkqw558tmZFXv_i478Jxavxgg';


                const options = {
                    url,
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + jwt
                    },
                    // data: dataSource.buildPayload(),
                    timeout: 5000,
                    // auth: {
                    //     username: environment.username,
                    //     password: environment.password
                    // }
                };

                console.log(`URL = ${url}`);

                const res = await axios(options).catch((err) => {
                    console.log(`-------------  AXIOS ERROR  ---------------`);
                    console.log(err);
                    console.log(JSON.stringify(err, null, 4));
                    console.log(`-------------  ERROR RESPONSE  ---------------`);
                    console.log(err.response);

                    const errorMessage = _.get(err, 'response.data.message') || _.get(err, 'message');
                    setErrorMessage(errorMessage);

                    // TODO: below pseudo search result is for demonstrative purpose (when an error occurs). remove this pseudo data later

                    setSearchResults([
                        createData(1, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(223, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
                        createData(356, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
                        createData(434, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(564, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01'),
                        createData(6, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(723, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
                        createData(856, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
                        createData(934, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(1034, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(1164, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01'),
                        createData(12, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(1323, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
                        createData(1456, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
                        createData(1534, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(1664, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01'),
                        createData(17, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(1823, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
                        createData(1956, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
                        createData(2064, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01'),
                        createData(21, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(2223, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
                        createData(2356, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
                        createData(2434, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
                        createData(2564, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01')
                    ]);
                });

                if (res) {
                    console.log(`-------------  res.data  ---------------`);
                    console.log(JSON.stringify(res.data, null, 4));

                    setSearchResults(res.data);
                    setPage(0);
                }
        */

        // setSubmitButtonDisabled(false);

    };

    const handleReset = () => {
        setCurrentTemplateId('');
        setCurrentTemplateTitle('');
        setNewTemplateTitle('');
        setNewTemplateAuthor('');
        setNewTemplateVersion('');
        setSubmitButtonDisabled(false);
        setErrorMessage('');
        setResponseMessage('');
    };

    return (
        <div>
            <div>
                <AppBar position="static">
                    <Toolbar>
                        {/*<IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="menu">*/}
                        {/*    <MenuIcon />*/}
                        {/*</IconButton>*/}
                        <Typography variant="h5" className={classes.title}>
                            Update Templates
                        </Typography>
                        {/*<Button color="inherit">Login</Button>*/}
                    </Toolbar>
                </AppBar>
            </div>

            <div className={classes.container}>
                <div>
                    <TextField
                        label="Current Template ID"
                        // helperText="Not applicable for System Templates"
                        value={currentTemplateId}
                        name="currentTemplateId"
                        onChange={handleInputValueChange}
                        margin="normal"
                    />
                    <TextField style={{display: 'block'}}
                               label="Current Template Title"
                               value={currentTemplateTitle}
                               name="currentTemplateTitle"
                               onChange={handleInputValueChange}
                               margin="normal"
                    />
                    <TextField style={{display: 'block'}}
                               label="New Template Title"
                               value={newTemplateTitle}
                               name="newTemplateTitle"
                               onChange={handleInputValueChange}
                               margin="normal"
                    />
                    <TextField style={{display: 'block'}}
                               label="New Template Author"
                               value={newTemplateAuthor}
                               name="newTemplateAuthor"
                               onChange={handleInputValueChange}
                               margin="normal"
                    />
                    <TextField style={{display: 'block'}}
                               label="New Template Version"
                               value={newTemplateVersion}
                               name="newTemplateVersion"
                               onChange={handleInputValueChange}
                               margin="normal"
                    />
                </div>

                <div/>

                <div className={classes.buttons}>
                    <Button
                        color="primary"
                        variant="contained"
                        fullWidth={false}
                        disabled={isSubmitButtonDisabled}
                        onClick={handleSubmit}
                    >Submit</Button>

                    <Button
                        color="secondary"
                        variant="contained"
                        fullWidth={false}
                        // disabled={isSubmitButtonDisabled}
                        onClick={handleReset}
                    >Reset</Button>
                </div>

                <div/>
                <div/>

                {errorMessage.length > 0 &&
                <div className={classes.displayError}>
                    <SnackbarContent
                        className={classes.snackbar}
                        message={errorMessage}
                    />
                </div>
                }
            </div>

            <div className={classes.divider}></div>

            {/*{isSearchButtonDisabled && <CircularProgress className={classes.progress} />}*/}
            {isSubmitButtonDisabled && <LinearProgress variant="query" />}

            {!isSubmitButtonDisabled && responseMessage.length > 0 &&
            <div className={classes.searchResults}>
                {responseMessage}
            </div>
            }

            {/*{!isSubmitButtonDisabled && searchResults.length === 0 &&*/}
            {/*<div className={classes.searchResults}>*/}
            {/*    <h3>No Results</h3>*/}
            {/*</div>*/}
            {/*}*/}
        </div>
    );
}
