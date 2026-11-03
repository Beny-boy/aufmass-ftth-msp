---
name: deploy
description: Führt eine kontrollierte Deployment-Vorbereitung durch und deployt nur nach ausdrücklicher Freigabe.
disable-model-invocation: true
---

# Deployment-Workflow

Ziel/Umgebung: $ARGUMENTS

1. Lies `deploy-config.md` in diesem Skill-Verzeichnis.
2. Wenn Umgebung, Deployment-Weg oder Rollback-Verfahren nicht real konfiguriert sind: **abbrechen**, nur fehlende Angaben nennen.
3. Prüfe Git-Status, passende Tests, Build, Lint sowie nötige Migrations-/Backup-Schritte.
4. Dokumentiere Zielumgebung, geplante Befehle, Risiken, Verifikationsschritte und Rollback.
5. Fordere vor dem ersten extern wirksamen Deployment-Schritt eine **explizite Freigabe** an.
6. Führe ausschließlich freigegebene Aktionen aus. Keine produktive Datenmigration, Löschung oder Änderung an Geheimnissen ohne separate Freigabe.
7. Prüfe das Ergebnis und melde den Status nachvollziehbar.

Ohne Freigabe nur Dry-Run/Prüfung. Keine Dummy-Befehle als echte Deployments ausführen.
