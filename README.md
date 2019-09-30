# kareo-template-util

kareo-template-util is a utility tool, which allows logged in users to search, copy, and migrate user proprietary data from one customer account to another with cross application environments support.

## screenshots

### search templates page

[![kareo-template-util-search-page-png.png](https://i.postimg.cc/tR2mx3p4/kareo-template-util-search-page-png.png)](https://postimg.cc/RqJRrnWk)

------

### copy templates page

[![kareo-template-util-copy-page.png](https://i.postimg.cc/8kKt5TmF/kareo-template-util-copy-page.png)](https://postimg.cc/14Fp7hTP)

------

### update templates page

[![kareo-template-util-update-page.png](https://i.postimg.cc/DzvXLX27/kareo-template-util-update-page.png)](https://postimg.cc/jD12rCw1)

-----------

## System Architecture

kareo-template-util consists of following dockerized components:

**kareo-template-util-ui**
- Frontend UI (React)

**kareo-template-util-service**
- Backend service layer (Java Spring Boot)

**postgres**
- Datastore

**nginx**
- single point of entry into Backend with the ability to load balance to multiple instances of Backend services

-----------

* kareo-template-util-service communicates with both Oracle and Postgres. Oracle is only available within Kareo VPN

-----------

## usage

### build

`ORACLE_PROD_DB_JDBC_URL="" ORACLE_PROD_DB_USERNAME="" ORACLE_PROD_DB_PASSWORD="" docker-compose up --build --scale kareo-template-util-service=3`

### register user
`POST http://localhost:3001/api/v1/auth/register`
```
{
	"username": "pink.panther123",
	"password": "bark!",
	"firstname": "Pink",
	"lastname": "Panther",
	"email": "ppanther@email.com"
}
```

### login
`http://localhost:3001/api/v1/auth/login`
```
{
	"username": "pink.panther123",
	"password": "bark!"
}
```

### get users
`http://localhost:3001/api/v1/users/`

`http://localhost:3001/api/v1/users/1`


### search for templates
`http://localhost:3001/api/v1/templates?title=acne`

`http://localhost:3001/api/v1/templates?title=acne&find-partial-title-matches=true`

`http://localhost:3001/api/v1/templates?username=provider1@kareo.com&title=medspa`

-----------

## TO DO

* pagination / offset / limit on search results
    * change return type from List of Templates to Reponse wrapper with list of templates inside, along with other metadata such as pageNumber, resultCount, etc?
* blacklist JWT tokens (via logout / password change, etc)
* use Redis with TTL
    * look into other Cache options
* add request_history page, and rollback capability?

-----------