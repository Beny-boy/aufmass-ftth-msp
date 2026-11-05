# aufmass-ftth-msp
aufmass-app


## OBD Diagnose (Android) – Ordner `android/`
Native Android-App (Kotlin) für ELM327-Bluetooth-Adapter: Fehlerspeicher lesen/löschen, Live-Daten, VIN.
APK bauen: GitHub → Actions → "Android APK" → Artefakt `obd-diagnose-debug-apk`.

### Datenquellen
Fehlercode-Beschreibungen (9.533 genormte OBD-II-Codes, DE): [OBDex](https://github.com/foerbsnavi/OBDex), Daten unter CC0-1.0 (LICENSE-DATA im Repo geprüft).

## Projekt-Aufgaben (Desktop-Erinnerung) – Ordner `aufgaben/`
Web-App: Excel-Tabelle (Projekt, Aufgabe, Fällig, Status, Priorität, Verantwortlich) importieren, Aufgaben nach Überfällig/Heute/7 Tage/Später sortiert, Desktop-Benachrichtigung zur gewählten Uhrzeit. `aufgaben/index.html` im Browser öffnen. Excel-Bibliothek: SheetJS 0.18.5 (Apache-2.0, `aufgaben/xlsx.LICENSE.txt`).
