import React, { useContext, useState } from 'react';
import { makeStyles } from '@material-ui/styles';
import { AppBar, Button, Checkbox, CircularProgress, FormControl, FormControlLabel, FormLabel, IconButton, InputLabel, LinearProgress,
        MenuItem, Paper, Radio, RadioGroup, Select, SnackbarContent, Table, TableBody, TableCell, TableHead,
        TableRow, TableFooter, TablePagination, TextField, Toolbar, Typography } from '@material-ui/core';

import MenuIcon from '@material-ui/icons/Menu';

import AccessAlarmIcon from '@material-ui/icons/AccessAlarm';
import axios from 'axios';
import _ from 'lodash';
import { TemplateContext } from "./NavTabs";

import { RootContext } from "../RootContext";

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
    errorSnackBar: {
        backgroundColor: '#e74c3c',
    },
    divider: {
        marginTop: '1.5rem',
        borderBottom: '2px solid whitesmoke'
    },
    searchResults: {
        margin: '1.5rem'
    },
    responseContainer: {
        margin: '2rem',
    }
});


function createData(id, type, title, author, version, username, createdOn, updatedOn) {
    return { id, type, title, author, version, username, createdOn, updatedOn };
}

export default function TemplateSearch() {
    const classes = useStyles();

    // const isAuthenticated = window.localStorage.getItem('isAuthenticated');
    // const userId = window.localStorage.getItem('userId');
    // const userFirstname = window.localStorage.getItem('userFirstname');
    const jwt = window.localStorage.getItem('jwt');

    const {
        searchTemplateEnv, setSearchTemplateEnv,
        type, setType,
        title, setTitle,
        author, setAuthor,
        version, setVersion,
        username, setUsername,
        templateId, setTemplateId,
        isUsernameFieldDisabled, setUsernameFieldDisabled,
        isPartialTitleMatch, setPartialTitleMatch,
        isSearchButtonDisabled, setSearchButtonDisabled,
        errorMessages, setErrorMessages,
        searchResults, setSearchResults,
        page, setPage, rowsPerPage, setRowsPerPage } = useContext(TemplateContext);

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
            case 'searchTemplateEnv':
                setSearchTemplateEnv(event.target.value);
                break;
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
            case 'templateId':
                setTemplateId(event.target.value);
                break;
            case 'isPartialTitleMatch':
                setPartialTitleMatch(!isPartialTitleMatch);
                break;
            default:
                console.log(`Error - Unrecognized event.target.name = ${event.target.name}`);
                break;
        }

        setErrorMessages([]);
    };

    const handleSearchSubmit = async (event) => {
        event.preventDefault();

        // INPUT VALIDATION
        const errorMsgs = [];

        if (_.isEmpty(searchTemplateEnv)) {
            errorMsgs.push('Please specify the environment.');
        }

        if (_.isEmpty(title) &&
            _.isEmpty(author) &&
            _.isEmpty(version) &&
            _.isEmpty(username) &&
            _.isEmpty(templateId)) {
            errorMsgs.push('At least one field is required in order to search.');
        }

        if (errorMsgs.length > 0) {
            setErrorMessages(errorMsgs);
            return;
        }

        // disable the button until search results comes back
        setErrorMessages([]);
        setSearchButtonDisabled(true);

        let url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/${REACT_APP_API_VERSION}/templates`;
        let queryCount = 0;

        if (searchTemplateEnv) {
            if (queryCount === 0) {
                url += '?';
            } else {
                url += '&';
            }
            queryCount += 1;
            url += `environment=${searchTemplateEnv}`
        }
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
            url += `findPartialTitleMatches=${isPartialTitleMatch}`
        }
        if (type === 'system' || type === 'user') {
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
        if (templateId) {
            if (queryCount === 0) {
                url += '?';
            } else {
                url += '&';
            }
            queryCount += 1;
            url += `templateId=${templateId}`
        }

        console.log(`## URL = ${url}`);

        const options = {
            url,
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + jwt
            },
            // data: dataSource.buildPayload(),
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

            // // TODO: below pseudo search result is for demonstrative purpose (when an error occurs). remove this pseudo data later
            //
            // setSearchResults([
            //     createData(1, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(223, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
            //     createData(356, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
            //     createData(434, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(564, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01'),
            //     createData(6, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(723, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
            //     createData(856, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
            //     createData(934, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(1034, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(1164, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01'),
            //     createData(12, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(1323, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
            //     createData(1456, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
            //     createData(1534, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(1664, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01'),
            //     createData(17, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(1823, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
            //     createData(1956, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
            //     createData(2064, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01'),
            //     createData(21, 'System', 'Acne', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(2223, 'User', 'MedSpa', 'Amy', '1.5', 'amy.vandenbrink@kareotest.com', '2018-01-01', '2018-01-01'),
            //     createData(2356, 'User', 'Depression', 'Susie', '1.1', 's.johnson@medical.com', '2018-01-01', '2018-01-01'),
            //     createData(2434, 'System', 'Acupuncture', 'Kareo', '1.0', null, '2018-01-01', '2018-01-01'),
            //     createData(2564, 'User', 'Diabetes', 'Dr. House', '1.0', 'house@practice.com', '2018-01-01', '2018-02-01')
            // ]);
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

            <form className={classes.container}>
                <div>
                    <FormControl>
                        <InputLabel htmlFor="search-template-environment">Environment</InputLabel>
                        <Select
                            // native
                            value={searchTemplateEnv}
                            onChange={handleInputValueChange}
                            // inputProps={{
                            //     name: 'age',
                            //     id: 'age-simple',
                            // }}
                            name="searchTemplateEnv"
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
                <div/>
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
                           autoFocus
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
                    <TextField
                        label="Template ID"
                        helperText="If Template ID is provided, it will ignore all other search parameters"
                        value={templateId}
                        name="templateId"
                        onChange={handleInputValueChange}
                        margin="normal"
                    />
                </div>
                <div/>

                <div>
                    <Button
                        type="submit"
                        style={{marginTop: '30px'}}
                        color="primary"
                        variant="contained"
                        fullWidth={false}
                        disabled={isSearchButtonDisabled}
                        onClick={handleSearchSubmit}
                    >Search</Button>
                </div>
                <div/>
                <div/>
            </form>

            <div className={classes.responseContainer}>
                {/*{isSearchButtonDisabled && <CircularProgress className={classes.progress} />}*/}
                {isSearchButtonDisabled && <LinearProgress className={classes.searchResults} variant="query" />}

                {errorMessages.length > 0 && <div className={classes.errorMessage}>{errorMessages.map((errorMessage, index) => (<SnackbarContent
                    className={classes.errorSnackBar}
                    message={errorMessage}
                    key={index}
                />))}</div>}
            </div>

            <div className={classes.divider}></div>

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
