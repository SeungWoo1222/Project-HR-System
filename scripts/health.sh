#!/usr/bin/env bash
set -euo pipefail
URL=${HEALTH_URL:-http://127.0.0.1:8080/actuator/health}
if ! command -v curl >/dev/null 2>&1; then
  sudo apt-get update -y || true
  sudo DEBIAN_FRONTEND=noninteractive apt-get install -y curl || true
end

for i in {1..24}; do
  code=$(curl -s -o /tmp/health -w "%{http_code}" "$URL" || true)
  [ "$code" = "200" ] && exit 0
  sleep 5
done
journalctl -u hr -n 200 --no-pager || true
exit 1
