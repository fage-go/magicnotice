#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
work="$(mktemp -d)"
trap 'rm -rf -- "$work"' EXIT
javac -d "$work" \
  "$root/app/src/main/java/com/zhangshuo/magicnotice/notice/OriginalDismissals.java" \
  "$root/app/src/main/java/com/zhangshuo/magicnotice/notice/factory/RuleReloadState.java" \
  "$root/tools/checks/OriginalDismissalsCheck.java" \
  "$root/tools/checks/RuleReloadStateCheck.java"
java -cp "$work" com.zhangshuo.magicnotice.notice.OriginalDismissalsCheck
java -cp "$work" com.zhangshuo.magicnotice.notice.factory.RuleReloadStateCheck
