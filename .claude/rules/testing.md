# Tests und Verifikation

- Finde zuerst die vorhandene Teststruktur und verwende den tatsächlich konfigurierten Test-Runner.
- Für Bugfixes möglichst einen reproduzierenden Regressionstest anlegen.
- Prüfe Normalfall, Randfälle und Fehlerpfade, soweit relevant.
- Bei Änderungen an öffentlichen APIs Kompatibilitätstests berücksichtigen.
- Starte zuerst gezielte Tests, anschließend geeignete Gesamtprüfungen (Lint/Typecheck/Build/Test), sofern vorhanden.
- Behaupte nie, Tests seien erfolgreich, wenn sie nicht ausgeführt wurden.
- Vor Abschluss Testergebnisse und verbleibende Einschränkungen nennen.
