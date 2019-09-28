#!/bin/bash

confluent local destroy > /dev/null 2>&1

docker-compose down > /dev/null 2>&1

./gradlew docker && docker-compose up -d && watch -n 30 -t --color 'docker-compose ps'