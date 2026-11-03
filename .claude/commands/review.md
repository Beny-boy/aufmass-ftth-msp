---
description: Prüft Änderungen auf Bugs, Sicherheitsrisiken und Wartbarkeit, ohne Dateien zu bearbeiten.
disable-model-invocation: true
---

Führe einen kritischen Code-Review durch. Umfang: $ARGUMENTS

- Wenn kein Umfang genannt wurde, nutze die lokalen Git-Änderungen als Ausgangspunkt.
- Untersuche nur relevante Dateien und Zusammenhänge.
- Priorisiere konkrete, belegte Befunde nach Schweregrad.
- Für jeden Befund: Datei/Zeile, Auswirkung, Reproduzierbarkeit und präziser Lösungsvorschlag.
- Nenne Tests, die fehlen. Wenn nichts Relevantes gefunden wird, sage das ausdrücklich.
- Keine Dateien verändern, keine Commits oder Deployments auslösen.
