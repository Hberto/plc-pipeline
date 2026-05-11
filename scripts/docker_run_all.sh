#!/bin/bash
# automate run
# For Development purposes and optional
echo "Start All Containers...."
cd "$(dirname "$0")/../docker"
docker compose up -d