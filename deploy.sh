#!/bin/bash

CONTAINER_NAME=studytravel

docker buildx build . -t $CONTAINER_NAME && docker run -it -p 8080:8080 $CONTAINER_NAME