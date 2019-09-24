import React, { useContext, useState } from 'react';
import { makeStyles } from '@material-ui/styles';
import { AppBar, Button, Checkbox, CircularProgress, FormControl, FormControlLabel, FormLabel, IconButton, InputLabel, LinearProgress,
    MenuItem, Paper, Radio, RadioGroup, Select, SnackbarContent, Table, TableBody, TableCell, TableHead,
    TableRow, TableFooter, TablePagination, TextField, Toolbar, Typography } from '@material-ui/core';

import MenuIcon from '@material-ui/icons/Menu';

import AccessAlarmIcon from '@material-ui/icons/AccessAlarm';
import axios from 'axios';
import _ from 'lodash';
import { RootContext } from "../RootContext";
import {TemplateContext} from "./NavTabs";

const REACT_APP_NGINX_HOSTNAME = process.env.REACT_APP_NGINX_HOSTNAME || 'localhost';
const REACT_APP_NGINX_PORT = process.env.REACT_APP_NGINX_PORT || '3001';
const REACT_APP_API_VERSION = process.env.REACT_APP_API_VERSION || 'v1';

const isOnlyNumbersRegEx = /^\d+$/;


// TODO:
//  - signin and signout page interaction
//  - refine update template page UI
//  - get it to actually call BE service



const useStyles = makeStyles({
    container: {
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        // gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',

        gridRowGap: '15px',

        // display: 'flex',
        // justifyContent: 'center',
        // alignItems: 'center',
        //   flexWrap: 'wrap',

        margin: '2rem',

        // border: '2px solid blue'
    },
    buttons: {
        display: 'grid',
        gridTemplateColumns: '100px 100px',
        gridGap: '1rem',
        marginTop: '2rem'
        // gridTemplateColumns: 'minmax(1fr, auto) minmax(1fr, auto)',
    },
    errorMessage: {
        color: 'red',
        display: 'grid',
        gridTemplateColumns: '1fr',
        gridRowGap: '10px',
        marginTop: '20px'

        // border: '1px solid red',
    },
    errorSnackBar: {
        backgroundColor: '#e74c3c',
    },
    successSnackBar: {
        marginTop: '1.5rem'
    },
    divider: {
        marginTop: '1.5rem',
        borderBottom: '2px solid whitesmoke'
    },
    responseContainer: {
        margin: '2rem',
    }
});


function createData(id, type, title, author, version, username, createdOn, updatedOn) {
    return { id, type, title, author, version, username, createdOn, updatedOn };
}

export default function TemplateUpdate() {
    const classes = useStyles();

    const { authenticated, setAuthenticated, authBody, setAuthBody } = useContext(RootContext);

    console.log(`TemplateSearch - authenticated = ${authenticated}`);
    console.log(`TemplateSearch - authBody = ${authBody}`);

    const authBodyJson = JSON.parse(authBody);
    console.log(authBodyJson.jwt);


    const {
        updateTemplateEnv, setUpdateTemplateEnv,
        currentTemplateId, setCurrentTemplateId,
        currentTemplateTitle, setCurrentTemplateTitle,
        newTemplateTitle, setNewTemplateTitle,
        newTemplateAuthor, setNewTemplateAuthor,
        newTemplateVersion, setNewTemplateVersion } = useContext(TemplateContext);

    const [isSubmitButtonDisabled, setSubmitButtonDisabled] = useState(false);
    const [errorMessages, setErrorMessages] = useState([]);
    const [responseMessage, setResponseMessage] = useState('');

    const handleInputValueChange = (event) => {

        switch (event.target.name) {
            case 'updateTemplateEnv':
                setUpdateTemplateEnv(event.target.value);
                break;
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
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const currentId = currentTemplateId.trim();
        const currentTitle = currentTemplateTitle.trim();
        const newTitle = newTemplateTitle.trim();
        const newAuthor = newTemplateAuthor.trim();
        const newVersion = newTemplateVersion.trim();

        // INPUT VALIDATION
        const errorMessages = [];

        if (_.isEmpty(updateTemplateEnv)) {
            errorMessages.push('Please specify the environment');
        }

        if (_.isEmpty(currentId) || _.isEmpty(currentTitle)) {
            errorMessages.push('Current Template ID and Current Template Title are required.');
        }

        if (!_.isEmpty(currentId) && (!isOnlyNumbersRegEx.test(currentId) || !_.isInteger(_.toFinite(currentId)))) {
            errorMessages.push(`"${currentId}" is not a valid template ID as it is not an integer number.`);
        }

        if (_.isEmpty(newTitle) && _.isEmpty(newAuthor) && _.isEmpty(newVersion)) {
            errorMessages.push('At least one new template field is required.');
        }

        if (errorMessages.length > 0) {
            setErrorMessages(errorMessages);
            return;
        }

        // disable the submit button until response comes back
        setSubmitButtonDisabled(true);
        setErrorMessages([]);

        const url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/${REACT_APP_API_VERSION}/templates/update-template-metadata`;

        const requestBody = {
            environment: updateTemplateEnv,
            currentTemplateId: currentId,
            currentTemplateTitle: currentTitle,
            newTitle,
            newAuthor,
            newVersion
        };

        console.log(`## URL = ${url}`);

        const options = {
            url,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + authBodyJson.jwt
            },
            data: requestBody,
            timeout: 15000,
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
            setErrorMessages([errorMessage]);
        });

        if (res) {
            console.log(`-------------  res.data  ---------------`);
            console.log(JSON.stringify(res.data, null, 4));

            setResponseMessage(res.data.message);
        }

        setSubmitButtonDisabled(false);
    };

    const handleReset = () => {
        setUpdateTemplateEnv('dev');
        setCurrentTemplateId('');
        setCurrentTemplateTitle('');
        setNewTemplateTitle('');
        setNewTemplateAuthor('');
        setNewTemplateVersion('');
        setSubmitButtonDisabled(false);
        setErrorMessages([]);
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

            <form className={classes.container}>
                <div>
                    <FormControl>
                        <InputLabel htmlFor="update-template-environment">Environment</InputLabel>
                        <Select
                            // native
                            value={updateTemplateEnv}
                            onChange={handleInputValueChange}
                            // inputProps={{
                            //     name: 'age',
                            //     id: 'age-simple',
                            // }}
                            name="updateTemplateEnv"
                        >
                            {/* <option value="dev">DEV</option>
                                <option value="qa">QA/TEST</option>
                                <option value="prod">PRODUCTION</option> */}
                            <MenuItem value="dev">DEV</MenuItem>
                            <MenuItem value="qa">QA/TEST</MenuItem>
                            <MenuItem value="prod">PRODUCTION</MenuItem>
                        </Select>
                    </FormControl>
                </div>
                <div/>
                <div>
                    <TextField
                        label="Current Template ID"
                        helperText="Only one template can be updated at a time"
                        value={currentTemplateId}
                        name="currentTemplateId"
                        onChange={handleInputValueChange}
                        margin="normal"
                        required
                        autoFocus
                    />
                    <TextField style={{display: 'block'}}
                               label="Current Template Title"
                               helperText="This is to ensure that the right template is updated"
                               value={currentTemplateTitle}
                               name="currentTemplateTitle"
                               onChange={handleInputValueChange}
                               margin="normal"
                               required
                    />
                </div>

                <div>
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

                <div className={classes.buttons}>
                    <Button
                        type="submit"
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
            </form>

            <div className={classes.responseContainer}>
                {/*<div className={classes.divider}></div>*/}

                {/*{isSearchButtonDisabled && <CircularProgress className={classes.progress} />}*/}
                {isSubmitButtonDisabled && <LinearProgress variant="query" />}

                {/*{!isSubmitButtonDisabled && searchResults.length === 0 &&*/}
                {/*<div className={classes.searchResults}>*/}
                {/*    <h3>No Results</h3>*/}
                {/*</div>*/}
                {/*}*/}

                {errorMessages.length > 0 && <div className={classes.errorMessage}>{errorMessages.map((errorMessage, index) => (<SnackbarContent
                    className={classes.errorSnackBar}
                    message={errorMessage}
                    key={index}
                />))}</div>}

                {responseMessage.length > 0 && <SnackbarContent
                    className={classes.successSnackBar}
                    message={responseMessage}
                />}
            </div>


        </div>
    );
}
