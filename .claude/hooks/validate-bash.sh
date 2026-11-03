#!/usr/bin/env bash
# Einfacher PreToolUse-Schutz für häufige riskante Bash-Kommandos.
# KEINE vollständige Shell-Sicherheitsanalyse und KEIN Sandbox-Ersatz.
set -euo pipefail

if ! command -v python3 >/dev/null 2>&1; then
  echo "Hook blockiert: Python 3 fehlt; installiere Python 3 oder konfiguriere den Hook bewusst um." >&2
  exit 2
fi

python3 -c "$(cat <<'PY'
import json
import re
import sys

try:
    payload = json.load(sys.stdin)
except Exception:
    # Die CLI soll bei unverständlichem Hook-Input nicht unbemerkt weiterlaufen.
    print("Hook blockiert: ungültige Tool-Eingabe.", file=sys.stderr)
    sys.exit(2)

command = payload.get("tool_input", {}).get("command", "")
if not isinstance(command, str):
    print("Hook blockiert: Bash-Befehl ist kein String.", file=sys.stderr)
    sys.exit(2)

blocked = [
    (r"\brm\s+(?:-[a-z]*[rf][a-z]*|--recursive|--force)\b", "rekursives/erzwungenes Löschen"),
    (r"\bgit\s+reset\s+--hard\b", "hartes Git-Reset"),
    (r"\bgit\s+clean\s+-[a-z]*[fd]", "Git-Clean von Dateien/Ordnern"),
    (r"\bgit\s+push\b[^\n;]*(?:--force|-f\b)", "erzwungener Git-Push"),
    (r"\b(?:curl|wget)\b[^\n;|]*\|\s*(?:ba)?sh\b", "entferntes Skript direkt in Shell"),
    (r"\b(?:mkfs(?:\.[a-z0-9]+)?|dd)\s+", "Datenträger-/Rohdatenkommando"),
    (r"\bchmod\s+-R\s+777\b", "unsichere rekursive Dateirechte"),
]
for pattern, reason in blocked:
    if re.search(pattern, command, flags=re.IGNORECASE):
        print(f"Hook blockiert: {reason}. Prüfe den Befehl und führe ihn bei Bedarf manuell aus.", file=sys.stderr)
        sys.exit(2)
sys.exit(0)
PY
)"
