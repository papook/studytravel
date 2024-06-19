# StudyTravel

This is the README file for the StudyTravel RESTful web application. StudyTravel implements the HATEOAS principle for providing the client with the links to the next actions.

It can be used for creating partner universities and modules that students can take, and managing them by linking modules to universities or editing/deleting the resources. The server does not use any authentication and cannot be considered secure.

## Context

This web application was developed as part of the portfolio project of the course "Foundations of Distributed Systems". This project **lacks any security features** and **SHOULD NOT** be used in real-life scenarios. 

## How to run the server

For Running the server you should either have a JDK 17 installed on your computer or build and run the app in a Docker container.

### UNIX Operating Systems

On UNIX operating systems (Linux/Mac), the server can be started with only Docker installed (no need for a separate installation of Maven or JDK) by executing the _`deploy.sh`_ shell script. Simply navigate to the project root in the terminal and execute the following command:
```sh
./deploy.sh
```

### Windows

On Windows, you should manually build and run the Docker container using the Dockerfile.

## Tests

The project includes a test case suite that covers various aspects of the codebase. The tests are written using AssertJ and Mockito and can be executed to ensure the correctness and reliability of the project's functionalities. To run the tests, use any IDE or Maven.

There are 6 test classes available:
1. _`StudyTravelApplicationTests`_ : Contains only one test. Checks if the web application can successfully start or not.
2. _`StudyTravelApplicationIntegrationTests`_ : A set of ordered test, running one after another. Tests the app entirely by creating resources, managing them, and deleting at the end. **Requires a running server!**
3. _`DispatcherControllerTests`_ : A WebMvcTest used for unit testing the Dispatcher controller.
4. _`UniversityControllerTests`_ : A WebMvcTest used for unit testing the University controller. Uses a Mock version of all beans required.
5. _`StudyModuleControllerTests`_ : A WebMvcTest used for unit testing the Study Module controller. Uses a Mock version of all beans required.
6. _`ClientTests`_ : A test classes that uses the AssertJ framework. It tests the methods of a client by sending a request to the server and checking the request URI and the response status code. 

### Running the tests in Maven

If you have maven installed, you can simply navigate to the project root and run `mvn clean test`. If you do not have Maven installed, you can easily use the Maven wrapper that is available in the project root folder. Simply navigate to the root folder and run:
```sh
./mvnw clean test
```

## Client and Server

The project consists of both a client and a server component. The client is responsible for sending the requests, while the server handles the requests. The client and server communicate with each other using basic HTTP requests.

## Docker Image

A Dockerfile is available for this project, allowing for easy deployment and execution on all computers running Docker. To run the project using Docker on Mac or Linux, simply execute the `deploy.sh` script provided in the project's root directory. This script will handle the necessary steps to build and run the Docker container, ensuring a seamless deployment experience.

## Postman Collection

The project includes a Postman collection in JSON format. The collection contains a set of pre-defined API requests that can be used for testing and interacting with the project's server. You can import the collection into Postman to easily execute the requests and validate the server's responses.