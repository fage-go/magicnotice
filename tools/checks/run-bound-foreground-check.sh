#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
work="$(mktemp -d)"
trap 'rm -rf -- "$work"' EXIT
javac -d "$work" \
  "$root/app/src/main/java/com/zhangshuo/magicnotice/notice/BoundForeground.java" \
  "$root/tools/checks/BoundForegroundCheck.java"
java -cp "$work" com.zhangshuo.magicnotice.notice.BoundForegroundCheck
