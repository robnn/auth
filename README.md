
# Custom authentication module for Spring framework with social login integration
**Master status:** [<img src="https://travis-ci.org/robnn/auth.svg?branch=master">](https://travis-ci.org/robnn/auth)

The purpose of this module is to handle authentication comfortably, just by annotating an endpoint or service.

![annotation](annotation.png)

*As you can see, only one annotation is needed on the custom API or service method, and the module uses its own annotation!*

### Stored data

The module uses 3 tables, `au_user`, `au_role`, join table between them and `au_user_token`. The user table stores data for users, like name, username, email address, etc. 
The token table stores the currently logged in users auth token, and validity time.
The Role table stores roles, by default 2: `USER` and `ADMIN`, but can extended, by creating new roles using the `RoleDao`

### Usage

Add the repository:

    <repository>
        <id>auth-repository</id>
        <url>https://raw.github.com/robnn/auth/repository</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>

Add the dependency:

    <dependency>
        <groupId>hu.robnn</groupId>
        <artifactId>auth</artifactId>
        <version>0.1.0</version>
    </dependency>

The module publishes an API (`{your_base_url}/users`) where users can register, and log in. During login, the user receives
 an authentication token, hereinafter he should use this token as authentication in every request. Using the `@Authenticated`
 annotation the programmer secures an API or service method. Calling an annotated
method, the authentication aspect hijacks the call and validates the token. The validation procedure is simple, the user sends his token in the 
`X-Auth-Token` HTTP header, and if the token is valid, and the
user has the needed role (`ADMIN` or `USER`, for now), then the request is valid, else an exception is thrown which handled by
an ExceptionHandler, and pushed back to the caller, who should handle it.

The token expires in 30 minutes, but every annotated method call changes the expiration time to call time + 30 minutes.

### Endpoints

#### `POST users`:

The user calls this, to register into the system. Posted data is a complete UserDTO:
```
    realName: String
    username: String
    emailAddress: String
    password: String
```

The role is filled with `USER` during registration. If you want `ADMIN` users, create them in the database manually.

Response is the registered user, without password.

#### `POST users/login`:

The user calls this, to login to the system. Posted data is a UserDTO containing username and password:
```
    username: String
    password: String
```

Response is a newly created auth token:
```
    token: String
```

#### `GET users/byToken/{token}`:

The user can call this, to get the stored User data, for a valid token. This request uses the authentication procedure,
so a valid token must be presented in the HTTP header too.

Response is a full user, without password

#### `POST users/login/facebook`:

The user calls this with an oauth2 access token, to login with facebook:
```
    token: String
```

The method gets the user's data from facebook, and registers them if needed, then gives back an auth token in the format of basic login.

#### `POST users/login/google`:

Same as facebook, just with a google access token.

#### `POST users/addRolesToUser?username={}`:

Can be used to add roles to a user, by posting an array of roleCodes:
```
    String[]
```

Only callable by users with ADMIN roles.

### Password storage

The module uses the Spring salted hash technique to store and validate passwords. No dictionary attacks is possible.

### Interceptors

If you want to execute custom code during registration, login or authentication, the module provides interceptor interfaces.
With them you can execute any code during these processes, just implement the needed interface, any number of times, the
engine will get them from the context, and call the methods:

* AuthenticationInterceptor
* LoginInterceptor
* RegisterInterceptor

### Messages

During registration, login and authentication, errors can happen. These errors will be given back to the caller of the API with
HTTP Response code 400 in this message format:
```
    severity: Severity
    message: String
```
Where severity can be:
```
    INFO
    WARNING
    ERROR
    DEBUG
```
But mostly, ERROR.

And message can be:
```
    INVALID_TOKEN - during authentication
    USED_USERNAME - during registration
    USED_EMAIL_ADDRESS - during registration
    INVALID_CREDENTIALS - during login
    INSUFFICIENT_PERMISSION - during authentication
    NO_USER_FOR_USERNAME - can be thrown during role adding
    ERROR_DURING_FACEBOOK_SYNC - can be thrown during facebook sync
    ERROR_DURING_GOOGLE_SYNC - can be thrown during google sync
    
```
One can easily use an interceptor for example registration data validation, 
and send an adequate error code with this message format, calling the UserException constructor with string.

### Development notes

Add this to your maven settings `~/.m2/settings.xml`:

    <settings>
     <servers>
       <server>
        <id>github</id>
        <username>user</username>
        <password>pass</password>
       </server>
     </servers>
    </settings>

To update the maven repository on github, increase the version in the pom and run:

    mvn jar:jar deploy:deploy 
    mvn site:site

**Copyright 2019 Robin Kurovszky**

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
