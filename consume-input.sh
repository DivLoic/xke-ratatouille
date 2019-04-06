#!/bin/bash

kafka-console-consumer --bootstrap-server localhost:9092 --topic input-food-order | hexdump -C

