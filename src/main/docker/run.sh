#!/bin/sh
echo "********************************************************"
echo "************ Starting KBox Manager Service *************"
echo "********************************************************"

echo "########################################################"
echo "##### Waiting for the RDBMS to start on port: 27017 #####"
echo "########################################################"
while ! `nc -z mongodb 27017`; do sleep 3; done
echo "=>>>>>>>>>>  MongoDB Server has started "

exec java -Dspring.profiles.active=prod -jar /usr/local/kbox/@project.build.finalName@.jar
