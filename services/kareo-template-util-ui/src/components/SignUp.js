import React, { useContext, useState } from 'react';
import Avatar from '@material-ui/core/Avatar'
import Button from '@material-ui/core/Button'
import CssBaseline from '@material-ui/core/CssBaseline'
import TextField from '@material-ui/core/TextField'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import Checkbox from '@material-ui/core/Checkbox'
import Link from '@material-ui/core/Link'
import Grid from '@material-ui/core/Grid'
import Box from '@material-ui/core/Box'
import LockOutlinedIcon from '@material-ui/icons/LockOutlined'
import HowToRegOutlined from '@material-ui/icons/HowToRegOutlined'
import Typography from '@material-ui/core/Typography'
import { makeStyles } from '@material-ui/core/styles'
import Container from '@material-ui/core/Container'
import SnackbarContent from '@material-ui/core/SnackbarContent';

import _ from 'lodash';
import axios from 'axios';

const REACT_APP_STATIC_SITE_DEMO_MODE = process.env.REACT_APP_STATIC_SITE_DEMO_MODE || 'false';
const REACT_APP_NGINX_HOSTNAME = process.env.REACT_APP_NGINX_HOSTNAME || 'localhost';
const REACT_APP_NGINX_PORT = process.env.REACT_APP_NGINX_PORT || '9090';        // 3001
const REACT_APP_API_VERSION = process.env.REACT_APP_API_VERSION || 'v1';

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
    )
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
        // backgroundColor: theme.palette.secondary.main,
        backgroundColor: '#46b6db'
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(3),
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
    },
    errorDisplay: {
        marginTop: '1.5rem',
        backgroundColor: '#e74c3c'
    },
    successDisplay: {
        marginTop: '1.5rem'
    }
}));

export default function SignUp() {
    const classes = useStyles();

    const initialState = {
        firstname: '',
        lastname: '',
        email: '',
        password: '',
        isSubmitting: false,
        errorMessages: [],
        responseMessage: ''
    };

    const [data, setData] = useState(initialState);
    const handleInputValueChange = (event) => {
        setData({
            ...data,
            [event.target.name]: event.target.value
        });
    };

    const handleSignUp = async (event) => {
        event.preventDefault();

        console.log(`sign up button clicked. firstname = ${data.firstname} | lastname = ${data.lastname} | email = ${data.email} | password = ${data.password}`);

        const errorMessages = [];

        if (_.isEmpty(data.firstname) || _.isEmpty(data.lastname) ||
            _.isEmpty(data.email) || _.isEmpty(data.password)) {
            const errorMessage = 'All fields are required in order to sign up';
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
            isSubmitting: true
        });

        const url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/${REACT_APP_API_VERSION}/auth/register`;

        const requestBody = {
            firstname: data.firstname,
            lastname: data.lastname,
            email: data.email,
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

            const errorMessage = _.get(err, 'response.data.message') || _.get(err, 'message');


            // TODO: implement static site demo mode
            if (REACT_APP_STATIC_SITE_DEMO_MODE === 'true') {

                // TODO: create the user in cache, or localStorage


            } else {
                setData({
                    ...data,
                    isSubmitting: false,
                    errorMessages: [errorMessage]
                });
            }
        });

        if (res || REACT_APP_STATIC_SITE_DEMO_MODE === 'true') {

            console.log(`-------------  res.data  ---------------`);
            if (res) {
                console.log(JSON.stringify(res.data, null, 4));
            }

            setData({
                ...data,
                isSubmitting: true,
                responseMessage: `Successfully registered user. Redirecting to the login page...`
            });

            setTimeout(() => {
                window.location.replace('/login');
            }, 1500);
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <CssBaseline />
            <div className={classes.paper}>
                <Avatar className={classes.avatar}>
                    {/* <LockOutlinedIcon /> */}
                    <HowToRegOutlined />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Sign up
                </Typography>
                <form className={classes.form}>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                autoComplete="fname"
                                name="firstname"
                                variant="outlined"
                                required
                                fullWidth
                                id="firstname"
                                label="First Name"
                                autoFocus
                                value={data.firstname}
                                onChange={handleInputValueChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                variant="outlined"
                                required
                                fullWidth
                                id="lastname"
                                label="Last Name"
                                name="lastname"
                                autoComplete="lname"
                                value={data.lastname}
                                onChange={handleInputValueChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                variant="outlined"
                                required
                                fullWidth
                                id="email"
                                label="Email Address"
                                name="email"
                                autoComplete="email"
                                value={data.email}
                                onChange={handleInputValueChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                variant="outlined"
                                required
                                fullWidth
                                name="password"
                                label="Password"
                                type="password"
                                id="password"
                                autoComplete="current-password"
                                value={data.password}
                                onChange={handleInputValueChange}
                            />
                        </Grid>
                        {/* <Grid item xs={12}>
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        value="allowExtraEmails"
                                        color="primary"
                                    />
                                }
                                label="I want to receive inspiration, marketing promotions and updates via email."
                            />
                        </Grid> */}
                    </Grid>
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="primary"
                        className={classes.submit}
                        disabled={data.isSubmitting}
                        onClick={handleSignUp}
                    >
                        Sign Up
                    </Button>
                    <Grid container justify="flex-end">
                        <Grid item>
                            <Link href="/login" variant="body2">
                                Already have an account? Sign in
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

            <div className={classes.successDisplay}>
                {data.responseMessage.length > 0 && <SnackbarContent
                    className={classes.snackbar}
                    message={data.responseMessage}
                />}
            </div>
        </Container>
    )
}
