#!/usr/bin/env bash
set -euo pipefail
CONFIG_FILE="$(cd "$(dirname "$0")/.." && pwd)/config.toml"
AUTHOR_NAME="keyy-codex"
AUTHOR_EMAIL="keyy1315@naver.com"
if [[ -f "$CONFIG_FILE" ]]; then
  cfg_author=$(awk -F '"' '/author_name/ {print $2}' "$CONFIG_FILE" | tail -n 1)
  cfg_email=$(awk -F '"' '/author_email/ {print $2}' "$CONFIG_FILE" | tail -n 1)
  if [[ -n "${cfg_author:-}" ]]; then
    AUTHOR_NAME="$cfg_author"
  fi
  if [[ -n "${cfg_email:-}" ]]; then
    AUTHOR_EMAIL="$cfg_email"
  fi
fi
GIT_AUTHOR_NAME="$AUTHOR_NAME" \
GIT_AUTHOR_EMAIL="$AUTHOR_EMAIL" \
GIT_COMMITTER_NAME="$AUTHOR_NAME" \
GIT_COMMITTER_EMAIL="$AUTHOR_EMAIL" \
git commit "$@"
