#!/bin/bash

./gradlew docker && docker-compose up -d && watch -n 30 -t --color 'docker-compose ps'