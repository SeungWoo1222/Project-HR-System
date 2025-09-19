#!/usr/bin/env bash
set -euo pipefail

sudo mkdir -p /opt/hr /etc/hr

# systemd unit
sudo tee /etc/systemd/system/hr.service >/dev/null <<'UNIT'
[Unit]
Description=HR Spring Boot App
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/opt/hr
EnvironmentFile=/etc/hr/env
ExecStart=/usr/bin/java -jar /opt/hr/hr-system.jar
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
UNIT

sudo systemctl daemon-reload
sudo systemctl enable hr
