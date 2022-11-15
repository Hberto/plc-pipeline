#!/usr/bin/env bash

until printf "" 2>>/dev/null >>/dev/tcp/cassandra/9042; do 
    sleep 5;
    echo "Waiting for cassandra...";
done

echo "Creating keyspace and table..."
cqlsh cassandra -u cassandra -p cassandra -e "CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};"
cqlsh cassandra -u cassandra -p cassandra -e "CREATE TABLE IF NOT EXISTS test.test (topic text, value int, timestamp timestamp, PRIMARY KEY ((topic), timestamp));"
cqlsh cassandra -u cassandra -p cassandra -e "CREATE TABLE IF NOT EXISTS test.kafka (topic text, value int, current_timestamp timestamp, PRIMARY KEY ((topic), current_timestamp));"
echo "Done Creating keyspace and table..."