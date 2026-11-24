#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
work="$(mktemp -d "${TMPDIR:-/tmp}/magicnotice-mode-check.XXXXXX")"
trap 'rm -rf -- "$work"' EXIT
javac -d "$work" \
  "$root/app/src/main/java/com/zhangshuo/magicnotice/notice/ListenerStatusDotMode.java" \
  "$root/tools/checks/ListenerStatusDotModeCheck.java"
java -cp "$work" com.zhangshuo.magicnotice.notice.ListenerStatusDotModeCheck
