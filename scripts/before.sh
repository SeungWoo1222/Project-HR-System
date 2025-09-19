#!/usr/bin/env bash
set -euo pipefail

REGION="ap-northeast-2"

# awscli 없으면 설치 (공식 인스톨러)
if ! command -v aws >/dev/null 2>&1; then
  sudo apt-get update -y
  sudo apt-get install -y unzip curl
  ARCH=$(uname -m)
  if [ "$ARCH" = "x86_64" ]; then
    URL="https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip"
  else
    URL="https://awscli.amazonaws.com/awscli-exe-linux-aarch64.zip"
  fi
  curl -fsSL "$URL" -o /tmp/awscliv2.zip
  unzip -q -o /tmp/awscliv2.zip -d /tmp
  sudo /tmp/aws/install -i /usr/local/aws -b /usr/local/bin
fi

DB_URL=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/haruharu/db_prod/spring.datasource.url" --query "Parameter.Value" --output text)
DB_USER=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/haruharu/db_prod/spring.datasource.username" --query "Parameter.Value" --output text)
DB_PASS=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/haruharu/db_prod/spring.datasource.password" --query "Parameter.Value" --output text)

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
