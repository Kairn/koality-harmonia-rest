# Koality Harmonia (REST)
[![Apache license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Kairn_koality-harmonia-rest&metric=alert_status)](https://sonarcloud.io/dashboard?id=Kairn_koality-harmonia-rest)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Kairn_koality-harmonia-rest&metric=coverage)](https://sonarcloud.io/dashboard?id=Kairn_koality-harmonia-rest)  

Koality Harmonia is an open-source Java based web application that serves as an free online platform for individuals to publish and share music. You are able to sign up for a free account, create your own album and upload tracks, earn virtual coins in the system by active participation, and purchase other albums with these coins.  

This repository maintains the application's server-side source code, which is independent of the application's user interface (currently still under active development). The server is a full-fledged RESTful service built with Spring MVC and Hibernate 5, and it utilizes a cloud based Oracle Database for data persistence. Give it a try, test a publicly accessible RESTful endpoint by hitting [this link](http://ec2-13-59-150-21.us-east-2.compute.amazonaws.com:8080/koality-harmonia-rest/moment/get/1).

## Build Project
### Prerequisites
* [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Apache Maven](https://maven.apache.org/download.cgi) version 3.5 or above
* [Oracle Database](https://www.oracle.com/technetwork/database/enterprise-edition/downloads/index.html)
* A Server Runtime (check out [Tomcat](https://tomcat.apache.org/download-90.cgi))

### Install
***WARNING: for security reasons, the connection properties of the cloud database cannot be shared publicly, so an alternative data source (local or remote) has to be provided if you wish to build and deploy your own version of the application. Also, you might want to change the hash value of the administrator's password so you can login as a system administrator.***

1. Add your database connection properties by creating the environmental variable `KHR_AGGRE` with the value `oracle.jdbc.driver.OracleDriver;@<URL>;@<Username>;@<Password>`. Replace the variables in angle brackets (don't include the brackets) with real values.
2. H2 is used by the application for DAO integration testing, and in order to use it, add the environmental variable `H2_AGGRE` with the value `org.h2.Driver;@jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;@sa;@`.
3. Clone this repository and change into the project's root directory (where `pom.xml` is located).
4. (Optional) Add your own administrator password. To do so, compute the SHA-256 hash of the following `<Password> + ADMIN` (your password concatenated by the literal 'ADMIN'); then, locate the class `AuthService` in the package `io.esoma.khr.service` and replace the current value of the static variable `ADMIN_HASH` with the computed hash result. Also, in order to pass the unit test, you need to create another environmental variable `MY_PASSWORDS` with the value `<Password>;@`.
5. Download [Oracle JDBC Driver](https://www.oracle.com/technetwork/database/features/jdbc/jdbc-drivers-12c-download-1958347.html) and add it to your local maven repository (`~/.m2/repository/com/oracle/ojdbc/ojdbc/12.1.0.1/`).
6. Connect to your Oracle DB and run `kh-schema.sql` and `kh-oracle.sql` located in `src/main/resources/` to setup the database with some initial data (uncomment the statements first). Without the data, some integration tests will fail, but feel free to change them if you wish to use your own data.
7. Run `mvn test` to run all unit tests.
8. After all tests pass, Run `mvn clean package` or `mvn clean install` to build the project.
9. Take the built artifact (a `.war` file) and deploy it with a server (make sure to add the environmental variables if it is running in a different environment).

## Features
*You can find the details in the repository that maintains the client-side source code. Currently, it is not available as the user interface is still under development.*

## Technical Points
### Testing
The test code in this project is written with JUnit as a base framework, and it utilizes the `spring-test` module which provides support for integration tests that require a spring context environment or a dispatcher servlet. There are more than 400 tests in the test packages which cover almost every execution path in the source code methods. However, the integrated Spring MVC tests are still experimental and not meant to test the full functionalities of all RESTful endpoints.

### Security
The server-side uses JSON Web Tokens (JWT) to handle user authentication and authorization. After the client submits a valid email/password credentials pair, a cryptographically signed JWT is generated and sent back to the client for future requests that require authentication. Each token is only valid for 30 minutes, and the user will have to login again after the current session expires. The signing key will refresh every 24 hours to prevent brute force attacks.

### Documentation
Coming soon.