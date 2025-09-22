#!/usr/bin/env bash
set -euo pipefail

# 공통 유틸만 설치 (SSM 호출 없음)
if ! command -v curl >/dev/null 2>&1; then
  sudo apt-get update -y
  sudo apt-get install -y curl
fi
