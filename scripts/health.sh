#!/usr/bin/env bash
set -e
for i in {1..30}; do
  code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || true)
  [ "$code" = "200" ] && exit 0
  sleep 3
done
echo "health check failed"; exit 1
