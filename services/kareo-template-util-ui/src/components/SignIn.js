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
import TouchAppOutlined from '@material-ui/icons/TouchAppOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import SnackbarContent from '@material-ui/core/SnackbarContent';

import _ from 'lodash';
import axios from 'axios';
import { RootContext } from "../RootContext";

import { navigate } from 'hookrouter';
import { AuthContext, ReducerActionTypes } from "../App";

const REACT_APP_STATIC_SITE_DEMO_MODE = process.env.REACT_APP_STATIC_SITE_DEMO_MODE || 'false';
const REACT_APP_NGINX_HOSTNAME = process.env.REACT_APP_NGINX_HOSTNAME || 'localhost';
const REACT_APP_NGINX_PORT = process.env.REACT_APP_NGINX_PORT || '9090';        // 3001
const REACT_APP_API_VERSION = process.env.REACT_APP_API_VERSION || 'v1';

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
        // backgroundColor: theme.palette.secondary.main,
        backgroundColor: '#46b6db'
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(1),
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
    },
    errorDisplay: {
        marginTop: '1.5rem',
        backgroundColor: '#e74c3c',
    },
}));

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

const SignIn = (props) => {
    const classes = useStyles();

    const { dispatch } = useContext(AuthContext);

    const initialState = {
        email: '',
        password: '',
        isSubmitting: false,
        errorMessages: []
    };

    const [data, setData] = useState(initialState);

    const handleInputValueChange = (event) => {
        setData({
            ...data,
            [event.target.name]: event.target.value
        });
    };

    const handleSignIn = async (event) => {
        event.preventDefault();

        console.log(`sign in button clicked. email = ${data.email} | password = ${data.password}`);

        const errorMessages = [];

        if (_.isEmpty(data.email) || _.isEmpty(data.password)) {
            const errorMessage = 'Email and password are required in order to sign in';
            console.log(errorMessage);

            errorMessages.push(errorMessage);
            setData({
                ...data,
                errorMessages
            });
            return;
        }

        setData({
            ...data,
            isSubmitting: true,
            errorMessages: []
        });

        const url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/${REACT_APP_API_VERSION}/auth/login`;

        const requestBody = {
            username: data.email,
            password: data.password
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

            let errorMessage = _.get(err, 'response.data.message') || _.get(err, 'message');
            if (errorMessage.includes('Bad credentials')) {
                errorMessage = 'Invalid user credential';
            }

            // TODO: implement static site demo mode
            if (REACT_APP_STATIC_SITE_DEMO_MODE === 'true') {

                console.log('-------  sign in error 1  ---------');
                // props.setAuthenticated('true');
                // navigate('/');
            } else {

                console.log('--------   Sign In Error Message   ----------');
                console.log(errorMessage);


                setData({
                    ...data,
                    isSubmitting: false,
                    errorMessages: [errorMessage]
                });
            }
        });

        if (res) {
            console.log(`-------------  res.data  ---------------`);
            console.log(JSON.stringify(res.data, null, 4));

            /*
            {
                "jwt": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJnYmVhckBlbWFpbC5jb20iLCJpYXQiOjE1Njg0OTQ0MDcsImV4cCI6MTU2OTA5OTIwN30.GdTamSSsmrLNt8Qggv-bk1iVk_Jglqwua3WnWMu2kZ7iCGuqrZP0qRCb2YDS1-50jHvxaLg3MOVoCyWRd_VGVQ"
            }
            */

            const base64Url = res.data.jwt.split('.')[0]; // 0: header  1: payload
            const base64 = base64Url.replace('-', '+').replace('_', '/');
            const jwtHeader = JSON.parse(Buffer.from(base64, 'base64').toString('UTF-8'));

            console.log(`-------------  jwtHeader  ---------------`);
            console.log(jwtHeader);

            res.data.userId = jwtHeader.userId;
            res.data.userFirstname = jwtHeader.userFirstname;

            dispatch({
                type: ReducerActionTypes.LOGIN,
                payload: res.data
            });

            navigate('/');
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <CssBaseline />
            <div className={classes.paper}>
                <Avatar className={classes.avatar}>
                    {/* <LockOutlinedIcon /> */}
                    <TouchAppOutlined />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Sign in
                </Typography>
                <form className={classes.form}>
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
                        value={data.email}
                        onChange={handleInputValueChange}
                        // onChange={(event) => setEmail(event.target.value)}
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
                        value={data.password}
                        onChange={handleInputValueChange}
                        // onChange={(event) => setPassword(event.target.value)}
                    />
                    {/* <FormControlLabel
                        control={<Checkbox value="remember" color="primary" />}
                        label="Remember me"
                    /> */}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="primary"
                        className={classes.submit}
                        disabled={data.isSubmitting}
                        onClick={handleSignIn}
                    >
                        {data.isSubmitting ? 'Loading...' : 'Sign In'}
                    </Button>
                    <Grid container>
                        <Grid item xs>
                            <Link href="#" variant="body2">
                                Forgot password?
                            </Link>
                        </Grid>
                        <Grid item>
                            <Link href="/sign-up" variant="body2">
                                {"Don't have an account? Sign Up"}
                            </Link>
                        </Grid>
                    </Grid>
                </form>
            </div>
            <Box mt={4}>
                <Copyright />
            </Box>

            <div className={classes.errorDisplay}>
                {data.errorMessages.map((errorMessage, index) => (<SnackbarContent
                    className={classes.errorDisplay}
                    message={errorMessage}
                    key={index}
                />))}
            </div>

        </Container>
    );
}

export default SignIn;
