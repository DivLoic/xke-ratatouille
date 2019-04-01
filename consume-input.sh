#!/bin/bash

kafka-console-consumer --bootstrap-server localhost:9092 --topic exercise-breakfast | hexdump -C

