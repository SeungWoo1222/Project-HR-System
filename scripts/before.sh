#!/usr/bin/env bash
set -euo pipefail

REGION="ap-northeast-2"

# awscli 필요 시 설치
if ! command -v aws >/dev/null; then
  sudo apt-get update -y
  sudo apt-get install -y awscli
fi

DB_URL=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/hr/prod/db/url" --query "Parameter.Value" --output text)
DB_USER=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/hr/prod/db/username" --query "Parameter.Value" --output text)
DB_PASS=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/hr/prod/db/password" --query "Parameter.Value" --output text)

sudo mkdir -p /etc/hr
sudo tee /etc/hr/env >/dev/null <<ENV
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=$DB_URL
SPRING_DATASOURCE_USERNAME=$DB_USER
SPRING_DATASOURCE_PASSWORD=$DB_PASS
ENV

# 파일 권한 최소화
sudo chown root:root /etc/hr/env
sudo chmod 600 /etc/hr/env
