#!/bin/bash

# Compile and run the application locally
./mvnw clean package -DskipTests && java -jar target/studytravel.jar