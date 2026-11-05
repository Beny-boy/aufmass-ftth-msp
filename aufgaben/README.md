# Projekt-Aufgaben

Aufgaben aus einer Excel-Tabelle importieren und am Desktop erinnert werden.

## Browser-Version
`index.html` im Browser öffnen. Die Erinnerung erscheint nur, solange die Seite geöffnet ist.

## Desktop-App (Windows)
Läuft im Infobereich (Tray), auch wenn das Fenster geschlossen ist, und startet beim ersten Start automatisch mit Windows (abschaltbar im Tray-Menü).
Installer: GitHub → Actions → „Desktop-App (Windows)“ → Artefakt `projekt-aufgaben-windows-installer`.
Der Installer ist nicht signiert; Windows SmartScreen zeigt deshalb eventuell eine Warnung („Weitere Informationen“ → „Trotzdem ausführen“).

## Excel-Spalten
Projekt, Aufgabe (Pflicht), Fällig, Status („erledigt“ = abgehakt), Priorität, Verantwortlich.
