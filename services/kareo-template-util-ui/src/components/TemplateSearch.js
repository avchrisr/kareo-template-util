import React, { useState } from 'react';
import { makeStyles } from '@material-ui/styles';
import { Button, Checkbox, CircularProgress, FormControl, FormControlLabel, FormLabel, InputLabel, LinearProgress,
        MenuItem, Paper, Radio, RadioGroup, Select, SnackbarContent, Table, TableBody, TableCell, TableHead,
        TableRow, TableFooter, TablePagination, TextField } from '@material-ui/core';

import AccessAlarmIcon from '@material-ui/icons/AccessAlarm';
import axios from 'axios';
import _ from 'lodash';

const useStyles = makeStyles({
    container: {
        // display: 'flex',
        // justifyContent: 'center',
        // alignItems: 'center',
        //   flexWrap: 'wrap',
        margin: '2rem',

        // border: '2px solid blue'
    },
    section: {
        display: 'flex',

        // justifyContent: 'space-around',
        // justifyContent: 'space-evenly',
        // alignContent: 'start',
        // alignItems: 'center',
        // paddingRight: '100px',
        // marginBottom: '1.5rem',
        // borderBottom: '2px solid whitesmoke'

        // border: '2px solid red'


    },
    templateType: {
        paddingTop: '35px',
        paddingRight: '50px',

        // display: 'flex',
        // alignItems: 'center',
        // display: 'inline-block',
        //   marginLeft: theme.spacing(1),
        //   marginRight: theme.spacing(1),
        //    width: '300px'

        // border: '2px solid black'
    },
    displayError: {
        color: 'red',
        margin: '30px 50px'
    },
    divider: {
        marginTop: '1.5rem',
        borderBottom: '2px solid whitesmoke'
    }
});


function createData(id, type, title, author, version, username, createdOn, updatedOn) {
    return { id, type, title, author, version, username, createdOn, updatedOn };
}

function TemplateSearch() {
    const classes = useStyles();

    const [type, setType] = useState('either');
    const [title, setTitle] = useState('');
    const [author, setAuthor] = useState('');
    const [version, setVersion] = useState('');
    const [username, setUsername] = useState('');
    const [isUsernameFieldDisabled, setUsernameFieldDisabled] = useState(false);
    const [isPartialTitleMatch, setPartialTitleMatch] = useState(false);
    const [isSearchButtonDisabled, setSearchButtonDisabled] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const [searchResults, setSearchResults] = useState([]);

    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(5);

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

        // disable the button until search results comes back
        setSearchButtonDisabled(true);

        setTimeout(() => {
            setSearchButtonDisabled(false);

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
        }, 2600);


        // INPUT VALIDATION
        if (_.isEmpty(title) &&
            _.isEmpty(author) &&
            _.isEmpty(version) &&
            _.isEmpty(username)) {
            setErrorMessage('At least one field is required in order to search');
            return;
        }

        /*

        // disable the button until search results comes back
        this.setState({
            searchButtonDisabled: true,
            searchButtonOpacity: 0.4,
            loading: true,
            errorMessage: ''
        });

        let url = `http://${REACT_APP_NGINX_HOSTNAME}:${REACT_APP_NGINX_PORT}/api/templates`;

        if (this.state.searchValue.title) {
            url += `?title=${this.state.searchValue.title}`
        }
        if (this.state.searchValue.author) {
            url += `&author=${this.state.searchValue.author}`
        }
        if (this.state.searchValue.version) {
            url += `&version=${this.state.searchValue.version}`
        }
        if (this.state.searchValue.username) {
            url += `&username=${this.state.searchValue.username}`
        }

        const options = {
            url,
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
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

            this.setState({
                searchButtonDisabled: false,
                searchButtonOpacity: 1.0,
                loading: false,
                errorMessage
            });
        });

        if (res) {
            console.log(`-------------  res.data  ---------------`);
            console.log(JSON.stringify(res.data, null, 4));

            this.setState({
                searchResults: res.data
            });

            // enable search button
            this.setState({
                searchButtonDisabled: false,
                searchButtonOpacity: 1.0,
                loading: false,
                errorMessage: ''
            });
        }
        */
    };

    return (
        <div className={classes.container}>
            {/* <AccessAlarmIcon></AccessAlarmIcon> */}
            {/* <Button color="default" variant="text" fullWidth={false}>Hello World</Button> */}

            <h2>Search Templates</h2>
            <div className={classes.section}>

                <div className={classes.templateType}>
                    <FormControl>
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
                </div>
                <div>
                    <TextField style={{display: 'block'}}
                        // className={classes.textField}
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
                        // className={classes.textField}
                               label="Author"
                               value={author}
                               name="author"
                               onChange={handleInputValueChange}
                               margin="normal"
                    />
                    <TextField style={{display: 'block'}}
                        // className={classes.textField}
                               label="Version"
                               value={version}
                               name="version"
                               onChange={handleInputValueChange}
                               margin="normal"
                    />
                    <TextField style={{display: 'block'}}
                        // className={classes.textField}
                               label="Username"
                               value={username}
                               name="username"
                               onChange={handleInputValueChange}
                               margin="normal"
                               disabled={isUsernameFieldDisabled}
                    />
                </div>
                {errorMessage.length > 0 &&
                <div className={classes.displayError}>
                    ERROR: {errorMessage}
                </div>
                }
            </div>
            <div>
                <Button style={{display: 'block'}}
                        color="primary"
                        variant="contained"
                        fullWidth={false}
                        disabled={isSearchButtonDisabled}
                        onClick={handleSearchSubmit}
                >Search</Button>
            </div>

            <div className={classes.divider}></div>

            {/*{isSearchButtonDisabled && <CircularProgress className={classes.progress} />}*/}
            {isSearchButtonDisabled && <LinearProgress variant="query" />}

            {!isSearchButtonDisabled && searchResults.length > 0 &&
            <div>
                <h2>Search Results</h2>
                <div className={classes.section}>
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
        </div>
    );
}

export default TemplateSearch;
