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
ExecStart=/usr/bin/java -jar /opt/hr/hr-system.jar
EnvironmentFile=/etc/hr/env
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
UNIT

# 기본 env (없으면 생성)
[ -f /etc/hr/env ] || sudo tee /etc/hr/env >/dev/null <<'ENV'
SPRING_PROFILES_ACTIVE=prod
# SPRING_DATASOURCE_URL=jdbc:mysql://10.0.3.113:3306/hr?serverTimezone=Asia/Seoul&useSSL=false
# SPRING_DATASOURCE_USERNAME=hrapp
# SPRING_DATASOURCE_PASSWORD=******
ENV

sudo systemctl daemon-reload
sudo systemctl enable hr
