#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
work="$(mktemp -d "${TMPDIR:-/tmp}/magicnotice-rule-evaluation.XXXXXX")"
trap 'rm -rf -- "$work"' EXIT
javac -d "$work" \
  "$root/app/src/main/java/com/zhangshuo/magicnotice/notice/factory/RuleEvaluation.java" \
  "$root/app/src/main/java/com/zhangshuo/magicnotice/notice/factory/RuleOrdering.java" \
  "$root/tools/checks/RuleEvaluationCheck.java"
java -cp "$work" com.zhangshuo.magicnotice.notice.factory.RuleEvaluationCheck
