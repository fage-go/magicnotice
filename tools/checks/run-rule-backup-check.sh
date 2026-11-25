#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
gson="${1:-}"
if [[ -z "$gson" ]]; then
  gson="$(find "${GRADLE_USER_HOME:-$HOME/.gradle}/caches/modules-2/files-2.1/com.google.code.gson/gson/2.8.5" -name 'gson-2.8.5.jar' -print -quit)"
fi
[[ -f "$gson" ]]
tmp="$(mktemp -d)"
trap 'rm -rf -- "$tmp"' EXIT
javac -cp "$gson" -d "$tmp" \
  "$root/app/src/main/java/com/zhangshuo/magicnotice/backup/RuleBackup.java" \
  "$root/app/src/main/java/com/zhangshuo/magicnotice/notice/factory/RuleOrdering.java" \
  "$root/tools/checks/RuleBackupCheck.java"
java -cp "$tmp:$gson" com.zhangshuo.magicnotice.backup.RuleBackupCheck
