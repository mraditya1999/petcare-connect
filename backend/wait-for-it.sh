#!/bin/sh
# wait-for-it.sh

set -e

host="$1"
port="$2"
shift 2
cmd="$@"

echo "Waiting for $host:$port to be available..."

# Wait up to 30 seconds for the database port to open
timeout=30
while ! nc -z "$host" "$port"; do
  sleep 1
  timeout=$((timeout - 1))
  if [ "$timeout" -eq 0 ]; then
    echo "Timeout reached. $host:$port is not available."
    exit 1
  fi
done

echo "$host:$port is up - executing command"
exec $cmd