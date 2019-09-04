import React, { useState } from 'react';
import { makeStyles } from '@material-ui/styles';
import {
    AppBar, Button, FormControl, FormControlLabel, FormLabel, InputLabel, LinearProgress,
    MenuItem, Radio, RadioGroup, Select, SnackbarContent, TextField, Toolbar, Typography
} from '@material-ui/core';

import _ from 'lodash';
import axios from 'axios';

const isOnlyNumbersRegEx = /^\d+$/;

const REACT_APP_NGINX_HOSTNAME = process.env.REACT_APP_NGINX_HOSTNAME || 'localhost';
const REACT_APP_NGINX_PORT = process.env.REACT_APP_NGINX_PORT || '3001';
const REACT_APP_API_VERSION = process.env.REACT_APP_API_VERSION || 'v1';

const useStyles = makeStyles({
    container: {
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        // gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',

        gridRowGap: '15px',

        // display: 'flex',
        // justifyContent: 'center',
        // alignItems: 'center',
        // flexWrap: 'wrap',

        margin: '2rem',

        // width: '800px',
        // border: '2px solid blue',

        // nested selectors
        '& div': {
            // background: '#eee',
            // padding: '1em'
            // border: '1px solid red'
        }
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
    }
});

function TemplateCopy() {
    const classes = useStyles();

    const [fromEnv, setFromEnv] = useState('dev');
    const [toEnv, setToEnv] = useState('dev');
    const [fromType, setFromType] = useState('user');
    const [toType, setToType] = useState('user');
    const [fromUsername, setFromUsername] = useState('');
    const [toUsername, setToUsername] = useState('');
    const [templateIds, setTemplateIds] = useState('');
    const [createOrReplaceSystemTemplate, setCreateOrReplaceSystemTemplate] = useState('create');
    const [systemTemplateIdToReplace, setSystemTemplateIdToReplace] = useState('');
    const [isLoading, setLoading] = useState(false);
    const [errorMessages, setErrorMessages] = useState([]);
    const [submitResponseMessage, setSubmitResponseMessage] = useState('');

    const handleInputValueChange = (event) => {
        switch (event.target.name) {
            case 'fromEnv':
                setFromEnv(event.target.value);
                break;
            case 'toEnv':
                setToEnv(event.target.value);
                break;
            case 'fromType':
                setFromType(event.target.value);
                if (event.target.value === 'system') {
                    setFromUsername('');
                }
                break;
            case 'toType':
                setToType(event.target.value);
                if (event.target.value === 'system') {
                    setToUsername('');
                }
                break;
            case 'fromUsername':
                setFromUsername(event.target.value);
                break;
            case 'toUsername':
                setToUsername(event.target.value);
                break;
            case 'templateIds':
                setTemplateIds(event.target.value);
                break;
            case 'createOrReplaceSystemTemplate':
                setCreateOrReplaceSystemTemplate(event.target.value);
                if (event.target.value === 'create') {
                    setSystemTemplateIdToReplace('');
                }
                break;
            case 'systemTemplateIdToReplace':
                setSystemTemplateIdToReplace(event.target.value);
                break;
            default:
                console.log(`Error - Unrecognized event.target.name = ${event.target.name}`);
                break;
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const templateIdsToCopy = [];
        let templateIdsArray;
        let systemTemplateId;

        // INPUT VALIDATION
        const errorMessages = [];

        if (fromType === 'user' && _.isEmpty(fromUsername.trim())) {
            errorMessages.push(`"FromUsername" is required.`);
        }
        if (toType === 'user' && _.isEmpty(toUsername.trim())) {
            errorMessages.push(`"ToUsername" is required.`);
        }
        if (fromType === 'user' && toType === 'user' &&
            !_.isEmpty(fromUsername.trim()) &&
            (fromUsername.trim() === toUsername.trim())) {
            errorMessages.push(`"ToUsername" must be different from "FromUsername".`);
        }
        if (fromType === 'system' && toType === 'system') {
            errorMessages.push(`System templates cannot be copied to System`);
        }

        if (createOrReplaceSystemTemplate === 'replace') {
            systemTemplateId = systemTemplateIdToReplace.trim();

            if (_.isEmpty(systemTemplateId)) {
                errorMessages.push(`When replacing an existing System Template, the System Template ID to replace is required.`);
            } else {
                if (!isOnlyNumbersRegEx.test(systemTemplateId) || !_.isInteger(_.toFinite(systemTemplateId))) {
                    errorMessages.push(`"${systemTemplateId}" is not a valid template ID as it is not an integer number.`);
                }
            }
        }

        if (_.isEmpty(templateIds.trim())) {
            errorMessages.push(`At least one Template ID is required.`);
        } else {
            templateIdsArray = templateIds.split(',');

            templateIdsArray.forEach((id) => {
                id = id.trim();
                if (!isOnlyNumbersRegEx.test(id) || !_.isInteger(_.toFinite(id))) {
                    errorMessages.push(`"${id}" is not a valid template ID as it is not an integer number.`);
                } else if (id === systemTemplateId) {
                    errorMessages.push(`Template ID being copied from, and Template ID being replaced cannot be the same.`);
                } else {
                    templateIdsToCopy.push(parseInt(id));
                }
            });

            if (templateIdsArray.length > 1 && toType === 'system' && createOrReplaceSystemTemplate === 'replace') {
                errorMessages.push(`When copying multiple User templates to System, it must be "Create New System Template"`);
            }
        }

        if (errorMessages.length > 0) {
            setErrorMessages(errorMessages);
            return;
        }

        // disable the button until search results comes back
        setLoading(true);
        setErrorMessages([]);


        // setTimeout(() => {
        //     handleReset();
        // }, 4000);



        const url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/${REACT_APP_API_VERSION}/templates/copy-templates`;

        const requestBody = {
            fromEnvironment: fromEnv,
            toEnvironment: toEnv,
            fromType,
            toType,
            fromUsername,
            toUsername,
            templateIds: templateIdsToCopy,
            createNewSystemTemplate: createOrReplaceSystemTemplate === 'create',
            systemTemplateIdToReplace: systemTemplateIdToReplace
        };


        // TODO: implement user login and proper JWT usage
        const jwt = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjaHJpc3IiLCJpYXQiOjE1Njc1NDU3MjAsImV4cCI6MTU2ODE1MDUyMH0.ps-dOeKe4BA7hbZ7EWWfFHG-H-FQxMtRFhhaap2LIzaL_cQkbY2lXZuGdkWLgPkqw558tmZFXv_i478Jxavxgg';


        const options = {
            url,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + jwt
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

            setSubmitResponseMessage(res.data.message);
        }

        setLoading(false);
    };

    const handleReset = () => {
        setFromEnv('dev');
        setToEnv('dev');
        setFromType('user');
        setToType('user');
        setFromUsername('');
        setToUsername('');
        setTemplateIds('');
        setCreateOrReplaceSystemTemplate('create');
        setSystemTemplateIdToReplace('');
        setLoading(false);
        setErrorMessages([]);
    };

    return (
        <div>
            <div>
                <AppBar position="relative">
                    <Toolbar>
                        {/*<IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="menu">*/}
                        {/*    <MenuIcon />*/}
                        {/*</IconButton>*/}
                        <Typography variant="h5" className={classes.title}>
                            Copy Templates
                        </Typography>
                        {/*<Button color="inherit">Login</Button>*/}
                    </Toolbar>
                </AppBar>
            </div>
            <div className={classes.container}>
                <div>
                    <FormControl>
                        <InputLabel htmlFor="from-environment">From</InputLabel>
                        <Select
                            // native
                            value={fromEnv}
                            onChange={handleInputValueChange}
                            // inputProps={{
                            //     name: 'age',
                            //     id: 'age-simple',
                            // }}
                            name="fromEnv"
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
                <div>
                    <FormControl>
                        <InputLabel htmlFor="to-environment">To</InputLabel>
                        <Select
                            value={toEnv}
                            onChange={handleInputValueChange}
                            // inputProps={{
                            //     name: 'age',
                            //     id: 'age-simple',
                            // }}
                            name="toEnv"
                        >
                            <MenuItem value="dev">DEV</MenuItem>
                            <MenuItem value="qa">QA/TEST</MenuItem>
                            <MenuItem value="prod">PRODUCTION</MenuItem>
                        </Select>
                    </FormControl>
                </div>

                <div>
                    <FormControl>
                         <InputLabel htmlFor="from-type">Type</InputLabel>
                        <Select
                            value={fromType}
                            onChange={handleInputValueChange}
                            // inputProps={{
                            //     name: 'age',
                            //     id: 'age-simple',
                            // }}
                            name="fromType"
                        >
                            <MenuItem value="user">USER</MenuItem>
                            <MenuItem value="system">SYSTEM</MenuItem>
                        </Select>
                    </FormControl>
                </div>
                <div>
                    <FormControl>
                         <InputLabel htmlFor="to-type">Type</InputLabel>
                        <Select
                            value={toType}
                            onChange={handleInputValueChange}
                            // inputProps={{
                            //     name: 'age',
                            //     id: 'age-simple',
                            // }}
                            name="toType"
                        >
                            <MenuItem value="user">USER</MenuItem>
                            <MenuItem value="system">SYSTEM</MenuItem>
                        </Select>
                    </FormControl>
                </div>
                <div>
                    <TextField
                        label="Username"
                        helperText="applicable only if copying from User"
                        value={fromUsername}
                        name="fromUsername"
                        onChange={handleInputValueChange}
                        margin="normal"
                        disabled={fromType !== 'user'}
                    />
                </div>
                <div>
                    <TextField
                        label="Username"
                        helperText="applicable only if copying to User"
                        value={toUsername}
                        name="toUsername"
                        onChange={handleInputValueChange}
                        margin="normal"
                        disabled={toType !== 'user'}
                    />
                </div>
                <div>
                    <TextField
                        label="Template IDs"
                        helperText="comma separated if multiple"
                        value={templateIds}
                        name="templateIds"
                        onChange={handleInputValueChange}
                        margin="normal"
                    />
                </div>
                <div>
                    {toType === 'system' &&
                    <span>
                        <FormControl style={{marginTop: '1rem'}}>
                            <FormLabel>Options for copying to System</FormLabel>
                            <RadioGroup name="createOrReplaceSystemTemplate" value={createOrReplaceSystemTemplate} onChange={handleInputValueChange}>
                                <FormControlLabel
                                    value="create"
                                    control={<Radio color="primary" />}
                                    label="Create New System Template"
                                    labelPlacement="end" />
                                <FormControlLabel
                                    value="replace"
                                    control={<Radio color="primary" />}
                                    label="Replace Existing System Template (To retain deactivated status set by customers)"
                                    labelPlacement="end" />
                            </RadioGroup>
                        </FormControl>
                        <TextField style={{marginLeft: '2rem'}}
                            label="Template ID to replace"
                            helperText="only one ID is allowed"
                            value={systemTemplateIdToReplace}
                            name="systemTemplateIdToReplace"
                            onChange={handleInputValueChange}
                            margin="normal"
                            disabled={createOrReplaceSystemTemplate === 'create'}
                        />
                    </span>
                    }
                </div>
                <div className={classes.buttons}>
                    <Button
                        color="primary"
                        variant="contained"
                        fullWidth={false}
                        disabled={isLoading}
                        onClick={handleSubmit}
                    >Submit</Button>

                    <Button
                        color="secondary"
                        variant="contained"
                        fullWidth={false}
                        // disabled={isSearchButtonDisabled}
                        onClick={handleReset}
                    >Reset</Button>
                </div>
                <div></div>

                {isLoading && <LinearProgress variant="query" />}

                {/*<div className={classes.errorMessage}>{errorMessages.map((errorMessage) => (<div>{errorMessage}</div>))}</div>*/}
                {errorMessages.length > 0 && <div className={classes.errorMessage}>{errorMessages.map((errorMessage) => (<SnackbarContent
                    className={classes.snackbar}
                    message={errorMessage}
                />))}</div>}

                {submitResponseMessage.length > 0 && <SnackbarContent
                    className={classes.snackbar}
                    message={submitResponseMessage}
                />}


                {/*<Snackbar*/}
                {/*    anchorOrigin={{*/}
                {/*        vertical: 'bottom',*/}
                {/*        horizontal: 'left',*/}
                {/*    }}*/}
                {/*    open={open}*/}
                {/*    autoHideDuration={6000}*/}
                {/*    onClose={handleClose}*/}
                {/*>*/}
                {/*    <MySnackbarContentWrapper*/}
                {/*        onClose={handleClose}*/}
                {/*        variant="success"*/}
                {/*        message="This is a success message!"*/}
                {/*    />*/}
                {/*</Snackbar>*/}

            </div>
        </div>
    );
}

export default TemplateCopy;
