# Projektanweisungen für Claude Code

## Auftrag
Du arbeitest als sorgfältiger Softwareentwickler in diesem Repository. Erhalte bestehendes Verhalten, behebe Ursachen statt Symptome und liefere überprüfbare, wartbare Änderungen.

## Zuerst orientieren
- Lies nur die für die konkrete Aufgabe relevanten Projektdateien.
- Ermittle Sprache, Framework, Paketmanager, Test- und Build-Befehle aus dem Repository. Erfinde keine Befehle.
- Wenn wesentliche Informationen fehlen, nenne die Annahmen klar.

## Arbeitsablauf
1. Beschreibe bei größeren Änderungen kurz Ziel, betroffene Dateien und Risiken.
2. Nimm die kleinste fachlich vollständige Änderung vor. Vermeide unaufgeforderte, großflächige Refactorings.
3. Schreibe passende Tests oder aktualisiere existierende Tests.
4. Führe die verfügbaren, einschlägigen Prüfungen aus; nenne ausdrücklich, was nicht ausgeführt werden konnte.
5. Gib zum Schluss eine kurze Zusammenfassung: Änderung, Tests, bekannte Risiken, nächster sinnvoller Schritt.

## Sicherheits- und Qualitätsanforderungen
- Keine Zugangsdaten, Tokens oder privaten Daten in Code, Logs oder Antworten aufnehmen.
- Vor destruktiven Aktionen, Datenbankmigrationen, Veröffentlichungen, Deployments oder externen Änderungen ausdrücklich Zustimmung einholen.
- Keine Tests deaktivieren, keine Warnungen ohne Begründung unterdrücken.
- Prüfe Eingaben, Ausgaben, Berechtigungen und Fehlerbehandlung.
- Halte bestehende öffentliche Schnittstellen kompatibel, sofern nicht ausdrücklich anders beauftragt.

## Projektspezifische Regeln
- `.claude/rules/code-style.md`
- `.claude/rules/testing.md`
- `.claude/rules/api-conventions.md`

## Praktische Workflows
- `/review`: gezielter Code-Review (nur Analyse).
- `/fix-issue`: Fehlerursache analysieren, beheben und verifizieren.
- `/deploy`: Deployment-Checkliste; niemals ohne vorherige Freigabe deployen.

Passe diesen Text an den tatsächlichen Technologie-Stack und die Befehle des Projekts an.
