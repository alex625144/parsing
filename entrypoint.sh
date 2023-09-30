#!/bin/sh

exec java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=*:5005,suspend=n -jar "parsing-0.0.1-SNAPSHOT.jar"