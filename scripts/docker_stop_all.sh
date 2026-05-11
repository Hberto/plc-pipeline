#!/bin/bash
# automate stop
# For Development purposes and optional
echo "Stopping all Containers...."
cd "$(dirname "$0")/../docker"
docker compose down