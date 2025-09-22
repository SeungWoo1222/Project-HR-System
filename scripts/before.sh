#!/usr/bin/env bash
set -euo pipefail
REGION="ap-northeast-2"

# 디버그가 필요할 때만 주석 해제
# set -x

# awscli v2 없으면 설치
if ! command -v aws >/dev/null 2>&1; then
  sudo apt-get update -y
  sudo apt-get install -y unzip curl
  ARCH="$(uname -m)"
  URL="https://awscli.amazonaws.com/awscli-exe-linux-${ARCH/x86_64/x86_64}${ARCH/aarch64/aarch64}.zip"
  curl -fsSL "$URL" -o /tmp/awscliv2.zip
  unzip -q -o /tmp/awscliv2.zip -d /tmp
  sudo /tmp/aws/install -i /usr/local/aws -b /usr/local/bin
fi

DB_URL=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/haruharu/db_prod/spring.datasource.url" --query "Parameter.Value" --output text)
DB_USER=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/haruharu/db_prod/spring.datasource.username" --query "Parameter.Value" --output text)
DB_PASS=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/haruharu/db_prod/spring.datasource.password" --query "Parameter.Value" --output text)

#AWS_ACCESS_KEY=$(aws ssm get-parameter --with-decryption --region "$REGION" --name "/haruharu/application/cloud.aws.s3.access-key"  --query "Parameter.Value" -r text)
#AWS_BUCKET=$(aws ssm get-parameter    --with-decryption --region "$REGION" --name "/haruharu/application/cloud.aws.s3.bucket"      --query "Parameter.Value" -r text)

# 필수값 검증
for v in DB_URL DB_USER DB_PASS; do
  [ -n "${!v:-}" ] || { echo "ERROR: $v is empty. Check SSM or IAM/KMS permission."; exit 1; }
done

sudo mkdir -p /etc/hr
sudo tee /etc/hr/env >/dev/null <<ENV
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=$DB_URL
SPRING_DATASOURCE_USERNAME=$DB_USER
SPRING_DATASOURCE_PASSWORD=$DB_PASS

# spring-cloud-aws 속성의 환경변수 매핑
#CLOUD_AWS_CREDENTIALS_ACCESS_KEY=$AWS_ACCESS_KEY
#CLOUD_AWS_REGION_STATIC=$REGION
#CLOUD_AWS_S3_BUCKET=$AWS_BUCKET
ENV

# 파일 권한 최소화
sudo chown root:root /etc/hr/env
sudo chmod 600 /etc/hr/env
