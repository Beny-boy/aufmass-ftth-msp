---
description: Code-Review der aktuellen Änderungen
---
Prüfe `git diff` (staged + unstaged) auf:
1. Fehler und Randfälle (leere Eingaben, fehlende localStorage-Daten, Offline-Betrieb)
2. Verstöße gegen `.claude/rules/`
3. Datenverlust-Risiken bei gespeicherten Aufmaß-Daten
4. Ob `CACHE_NAME` in `sw.js` angepasst werden muss

Gib konkrete Fundstellen (Datei:Zeile) und nur belegbare Befunde an.
