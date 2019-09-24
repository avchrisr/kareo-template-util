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

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessages, setErrorMessages] = useState([]);
    const [successMessage, setSuccessMessage] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleInputValueChange = (event) => {
        switch (event.target.name) {
            case 'firstName':
                setFirstName(event.target.value);
                break;
            case 'lastName':
                setLastName(event.target.value);
                break;
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

    const handleSignUp = async (event) => {
        event.preventDefault();

        console.log(`sign up button clicked. firstName = ${firstName} | lastName = ${lastName} | email = ${email} | password = ${password}`);

        const errorMessages = [];
        setErrorMessages(errorMessages);

        if (_.isEmpty(firstName) || _.isEmpty(lastName) ||
            _.isEmpty(email) || _.isEmpty(password)) {
            const errorMessage = 'All fields are required in order to sign up';
            console.log(errorMessage);

            errorMessages.push(errorMessage);
            setErrorMessages(errorMessages);
            return;
        }

        setIsSubmitting(true);

        const url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/${REACT_APP_API_VERSION}/auth/register`;

        const requestBody = {
            firstname: firstName,
            lastname: lastName,
            email,
            username: email,
            password
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
                setErrorMessages([errorMessage]);
            }

            setIsSubmitting(false);

        });

        if (res || REACT_APP_STATIC_SITE_DEMO_MODE === 'true') {

            console.log(`-------------  res.data  ---------------`);
            if (res) {
                console.log(JSON.stringify(res.data, null, 4));
            }

            // setSuccessMessage(res.data);
            setSuccessMessage(`Successfully registered user. Redirecting to the login page...`);

            setTimeout(() => {
                setIsSubmitting(false);
                window.location.replace('/login');
            }, 2500);
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
                                name="firstName"
                                variant="outlined"
                                required
                                fullWidth
                                id="firstName"
                                label="First Name"
                                autoFocus
                                value={firstName}
                                onChange={handleInputValueChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                variant="outlined"
                                required
                                fullWidth
                                id="lastName"
                                label="Last Name"
                                name="lastName"
                                autoComplete="lname"
                                value={lastName}
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
                                value={email}
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
                                value={password}
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
                        onClick={handleSignUp}
                        disabled={isSubmitting}
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
                {errorMessages.map((errorMessage, index) => (<SnackbarContent
                    className={classes.errorDisplay}
                    message={errorMessage}
                    key={index}
                />))}
            </div>

            <div className={classes.successDisplay}>
                {successMessage.length > 0 && <SnackbarContent
                    className={classes.snackbar}
                    message={successMessage}
                />}
            </div>
        </Container>
    )
}
