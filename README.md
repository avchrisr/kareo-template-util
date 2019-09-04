# kareo-template-util
kareo-template-util with UI and Service layers.

consists of following dockerized components:

**nginx**

**kareo-template-util-ui**  --  FE layer (React)

**kareo-template-util-service**  --  BE service layer (Java Spring Boot)

**postgres**

-----------

* kareo-template-util-service communicates with Oracle and Postgres. Oracle is only available within Kareo VPN

-----------

### Usage

`docker-compose up --build --scale kareo-template-util-service=3`

`docker-compose down`

`docker image prune`


### Register User
`POST http://localhost:3001/api/v1/auth/register`
```
{
	"username": "user1",
	"password": "pass1",
	"firstname": "Bear",
	"lastname": "Claw",
	"email": "bclaw@email.com"
}
```

### Login
`http://localhost:3001/api/v1/auth/login`
```
{
	"username": "user1",
	"password": "pass1"
}
```

### Get Users
`http://localhost:3001/api/v1/users/`

`http://localhost:3001/api/v1/users/1`


### Search for Templates
`http://localhost:3001/api/v1/templates?title=acne`

`http://localhost:3001/api/v1/templates?title=acne&find-partial-title-matches=true`

`http://localhost:3001/api/v1/templates?username=provider1@kareo.com&title=medspa`

-----------

## TO DO

* finish template-copy feature
* pagination / offset / limit on search results
    * change return type from List of Templates to Reponse wrapper with list of templates inside, along with other metadata such as pageNumber, resultCount, etc?
* login/logout workflow in React app
    * blacklist JWT tokens (via logout / password change, etc)
    * use Redis with TTL
        * look into other Cache options
* add request_history page, and rollback capability?
* integration tests
* custom Postgres image with init script?

-----------