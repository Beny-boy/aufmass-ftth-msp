#!/usr/bin/env bash
# PreToolUse-Hook für Bash: blockiert riskante Befehle (Exit 2 = blockieren).
cmd=$(jq -r '.tool_input.command // ""')
if echo "$cmd" | grep -Eq 'rm[[:space:]]+-[a-zA-Z]*r[a-zA-Z]*f|rm[[:space:]]+-[a-zA-Z]*f[a-zA-Z]*r|git[[:space:]]+push[^|;&]*(--force|-f([[:space:]]|$))|git[[:space:]]+reset[[:space:]]+--hard|git[[:space:]]+clean[[:space:]]+-[a-zA-Z]*f'; then
  echo "Blockiert durch validate-bash.sh: riskanter Befehl (rm -rf / force-push / reset --hard / clean -f)." >&2
  exit 2
fi
exit 0
