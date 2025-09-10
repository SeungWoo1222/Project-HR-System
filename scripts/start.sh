#!/usr/bin/env bash
set -e
systemctl enable hr || true
systemctl restart hr
