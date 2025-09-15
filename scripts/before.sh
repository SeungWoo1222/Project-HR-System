#!/usr/bin/env bash
set -euo pipefail
mkdir -p /opt/hr
chown -R ubuntu:ubuntu /opt/hr
cat >/etc/systemd/system/hr.service <<'UNIT'
[Unit]
Description=HR System
After=network.target
[Service]
User=ubuntu
ExecStart=/usr/bin/java -jar /opt/hr/hr-system.jar --server.port=8080
Restart=always
[Install]
WantedBy=multi-user.target
UNIT
systemctl daemon-reload
