#!/bin/bash

docker-compose up -d

mvn clean package -DskipTests && \
java -jar -Dspring.profiles.active=prod target/manager-0.0.1-SNAPSHOT.jar