#!/bin/bash

hz start -c src/main/resources/hazelcast.xml &
hz start -c src/main/resources/hazelcast.xml &
hz start -c src/main/resources/hazelcast.xml &

hz-mc start -p 8080 &
