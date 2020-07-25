#!/usr/bin/env bash

docker-compose stop ratatouille-datagen

./gradlew :ratatouille-datagen:docker

docker-compose start ratatouille-datagen