import React, { useContext, useState } from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import SnackbarContent from '@material-ui/core/SnackbarContent';

import _ from 'lodash';
import axios from 'axios';
import {RootContext} from "../RootContext";

function Copyright() {
    return (
        <Typography variant="body2" color="textSecondary" align="center">
            {'Copyright © '}
            <Link color="inherit" href="#">
                Chris Ro
            </Link>{' '}
            {new Date().getFullYear()}
            {'.'}
        </Typography>
    );
}

const useStyles = makeStyles(theme => ({
    '@global': {
        body: {
            backgroundColor: theme.palette.common.white,
        },
    },
    paper: {
        marginTop: theme.spacing(8),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    avatar: {
        margin: theme.spacing(1),
        backgroundColor: theme.palette.secondary.main,
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(1),
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
    },
}));

export default function SignIn() {
    const classes = useStyles();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessages, setErrorMessages] = useState([]);

    const { authenticated, setAuthenticated, authBody, setAuthBody } = useContext(RootContext);

    const handleInputValueChange = (event) => {
        switch (event.target.name) {
            case 'email':
                setEmail(event.target.value);
                break;
            case 'password':
                setPassword(event.target.value);
                break;
            default:
                console.log(`Error - Unrecognized event.target.name = ${event.target.name}`);
                break;
        }
    };

    const handleSignIn = async (event) => {
        event.preventDefault();

        console.log(`sign in button clicked. email = ${email} | password = ${password}`);

        const errorMessages = [];

        if (_.isEmpty(email) || _.isEmpty(password)) {
            const errorMessage = 'email or password is blank.';
            console.log(errorMessage);

            errorMessages.push(errorMessage);

            setErrorMessages(errorMessages);
            return;
        }


        setAuthenticated('true');

/*
        // const url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/v1/templates/copy-templates`;
        const url = `http://api/v1/templates/copy-templates`;

        const requestBody = {
            // fromEnvironment: fromEnv,
            // toEnvironment: toEnv,
            // fromType,
            // toType,
            // fromUsername,
            // toUsername,
            // templateIdsArray,
            // createNewSystemTemplate: createOrReplaceSystemTemplate === 'create',
            // systemTemplateIdToReplace: systemTemplateIdToReplace
        };

        const options = {
            url,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            data: requestBody,
            timeout: 15000,
            // auth: {
            //     username: environment.username,
            //     password: environment.password
            // }
        };

        console.log(`URL = ${url}`);

        const res = await axios(options).catch(err => {
            console.log(`-------------  AXIOS ERROR  ---------------`);
            console.log(err);
            console.log(JSON.stringify(err, null, 4));
            console.log(`-------------  ERROR RESPONSE  ---------------`);
            console.log(err.response);

            // const errorMessage = _.get(err, 'response.data.message') || _.get(err, 'message');

            // setErrorMessages([errorMessage]);
        });

        if (res) {
            console.log(`-------------  res.data  ---------------`);
            console.log(JSON.stringify(res.data, null, 4));

            // setSubmitResponseMessage(res.data);
        }
 */


    };

    return (
        <Container component="main" maxWidth="xs">
            <CssBaseline />
            <div className={classes.paper}>
                <Avatar className={classes.avatar}>
                    <LockOutlinedIcon />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Sign in
                </Typography>
                <form className={classes.form} noValidate>
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="Email Address"
                        name="email"
                        autoComplete="email"
                        autoFocus
                        value={email}
                        onChange={handleInputValueChange}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                        value={password}
                        onChange={handleInputValueChange}
                    />
                    <FormControlLabel
                        control={<Checkbox value="remember" color="primary" />}
                        label="Remember me"
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="primary"
                        className={classes.submit}
                        onClick={handleSignIn}
                    >
                        Sign In
                    </Button>
                    <Grid container>
                        <Grid item xs>
                            <Link href="#" variant="body2">
                                Forgot password?
                            </Link>
                        </Grid>
                        <Grid item>
                            <Link href="#" variant="body2">
                                {"Don't have an account? Sign Up"}
                            </Link>
                        </Grid>
                    </Grid>
                </form>
            </div>
            <Box mt={8}>
                <Copyright />
            </Box>

            {errorMessages.map((errorMessage, index) => (<SnackbarContent
                className={classes.snackbar}
                message={errorMessage}
                key={index}
            />))}

        </Container>
    );
}
