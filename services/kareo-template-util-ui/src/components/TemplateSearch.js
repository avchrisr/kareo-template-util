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

export default function TemplateSearch() {
    const classes = useStyles();

    const {
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
        page, setPage, rowsPerPage, setRowsPerPage } = useContext(TemplateSearchContext);

    const emptyRows = rowsPerPage - Math.min(rowsPerPage, searchResults.length - page * rowsPerPage);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    const handleInputValueChange = (event) => {

        switch (event.target.name) {
            case 'type':
                setType(event.target.value);
                if (event.target.value === 'system') {
                    // username field is only applicable for User Templates
                    setUsername('');
                    setUsernameFieldDisabled(true);
                } else {
                    setUsernameFieldDisabled(false);
                }
                break;
            case 'title':
                setTitle(event.target.value);
                break;
            case 'author':
                setAuthor(event.target.value);
                break;
            case 'version':
                setVersion(event.target.value);
                break;
            case 'username':
                setUsername(event.target.value);
                break;
            case 'isPartialTitleMatch':
                setPartialTitleMatch(!isPartialTitleMatch);
                break;
            default:
                console.log(`Error - Unrecognized event.target.name = ${event.target.name}`);
                break;
        }

        setErrorMessage('');
    };

    const handleSearchSubmit = async (event) => {
        event.preventDefault();

        // INPUT VALIDATION
        if (_.isEmpty(title) &&
            _.isEmpty(type) &&
            _.isEmpty(author) &&
            _.isEmpty(version) &&
            _.isEmpty(username)) {
            setErrorMessage('At least one field is required in order to search');
            return;
        }

        // disable the button until search results comes back
        setSearchButtonDisabled(true);

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
        if (isPartialTitleMatch) {
            if (queryCount === 0) {
                url += '?';
            } else {
                url += '&';
            }
            queryCount += 1;
            url += `find-partial-title-matches=${isPartialTitleMatch}`
        }
        if (type === 'system' || type === 'custom') {
            if (queryCount === 0) {
                url += '?';
            } else {
                url += '&';
            }
            queryCount += 1;
            url += `type=${type}`
        }
        if (author) {
            if (queryCount === 0) {
                url += '?';
            } else {
                url += '&';
            }
            queryCount += 1;
            url += `author=${author}`
        }
        if (version) {
            if (queryCount === 0) {
                url += '?';
            } else {
                url += '&';
            }
            queryCount += 1;
            url += `version=${version}`
        }
        if (username) {
            if (queryCount === 0) {
                url += '?';
            } else {
                url += '&';
            }
            queryCount += 1;
            url += `username=${username}`
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

        setSearchButtonDisabled(false);
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
                            Search Templates
                        </Typography>
                        {/*<Button color="inherit">Login</Button>*/}
                    </Toolbar>
                </AppBar>
            </div>

            <div className={classes.container}>
                <div>
                    <FormControl style={{marginTop: '1.5rem'}}>
                        <FormLabel>Template Type</FormLabel>
                        <RadioGroup name="type" value={type} onChange={handleInputValueChange}>
                            <FormControlLabel
                                value="system"
                                control={<Radio color="primary" />}
                                label="System Templates Only"
                                labelPlacement="end" />
                            <FormControlLabel
                                value="user"
                                control={<Radio color="primary" />}
                                label="User Templates Only"
                                labelPlacement="end" />
                            <FormControlLabel
                                value="either"
                                control={<Radio color="primary" />}
                                label="Either Templates"
                                labelPlacement="end" />
                        </RadioGroup>
                    </FormControl>
                    <TextField
                           label="Username"
                           helperText="Not applicable for System Templates"
                           value={username}
                           name="username"
                           onChange={handleInputValueChange}
                           margin="normal"
                           disabled={isUsernameFieldDisabled}
                    />
                </div>
                <div>
                    <TextField style={{marginTop: 0}}
                           label="Title"
                           value={title}
                           name="title"
                           onChange={handleInputValueChange}
                           margin="normal"
                    />
                    <FormControlLabel style={{display: 'block'}}
                          control={
                              <Checkbox
                                  color="primary"
                                  checked={isPartialTitleMatch}
                                  name="isPartialTitleMatch"
                                  onChange={handleInputValueChange} />
                          }
                          label="Find Partial Title Matches"
                    />
                    <TextField style={{display: 'block'}}
                           label="Author"
                           value={author}
                           name="author"
                           onChange={handleInputValueChange}
                           margin="normal"
                    />
                    <TextField style={{display: 'block'}}
                           label="Version"
                           value={version}
                           name="version"
                           onChange={handleInputValueChange}
                           margin="normal"
                    />
                </div>
                <div/>

                <div>
                    <Button style={{marginTop: '30px'}}
                            color="primary"
                            variant="contained"
                            fullWidth={false}
                            disabled={isSearchButtonDisabled}
                            onClick={handleSearchSubmit}
                    >Search</Button>
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
            {isSearchButtonDisabled && <LinearProgress variant="query" />}

            {!isSearchButtonDisabled && searchResults.length > 0 &&
            <div className={classes.searchResults}>
                <h2>Search Results</h2>
                <div>
                    <Paper>
                        <div>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>ID</TableCell>
                                        <TableCell align="right">Type</TableCell>
                                        <TableCell align="right">Title</TableCell>
                                        <TableCell align="right">Author</TableCell>
                                        <TableCell align="right">Version</TableCell>
                                        <TableCell align="right">Username</TableCell>
                                        <TableCell align="right">CreatedOn</TableCell>
                                        <TableCell align="right">UpdatedOn</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {searchResults.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(row => (
                                        <TableRow key={row.id}>
                                            <TableCell component="th" scope="row">{row.id}</TableCell>
                                            <TableCell align="right">{row.type}</TableCell>
                                            <TableCell align="right">{row.title}</TableCell>
                                            <TableCell align="right">{row.author}</TableCell>
                                            <TableCell align="right">{row.version}</TableCell>
                                            <TableCell align="right">{row.username}</TableCell>
                                            <TableCell align="right">{row.createdOn}</TableCell>
                                            <TableCell align="right">{row.updatedOn}</TableCell>
                                        </TableRow>
                                    ))}

                                    {emptyRows > 0 && (
                                        <TableRow style={{ height: 48 * emptyRows }}>
                                            <TableCell colSpan={6} />
                                        </TableRow>
                                    )}
                                </TableBody>
                                <TableFooter>
                                    <TableRow>
                                        <TablePagination
                                            rowsPerPageOptions={[5, 10, 25]}
                                            colSpan={3}
                                            count={searchResults.length}
                                            rowsPerPage={rowsPerPage}
                                            page={page}
                                            backIconButtonProps={{
                                                'aria-label': 'previous page',
                                            }}
                                            nextIconButtonProps={{
                                                'aria-label': 'next page',
                                            }}
                                            onChangePage={handleChangePage}
                                            onChangeRowsPerPage={handleChangeRowsPerPage}
                                        />
                                    </TableRow>
                                </TableFooter>
                            </Table>
                        </div>
                    </Paper>
                </div>
            </div>
            }

            {!isSearchButtonDisabled && searchResults.length === 0 &&
            <div className={classes.searchResults}>
                <h3>No Results</h3>
            </div>
            }
        </div>
    );
}
