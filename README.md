# aufmass-ftth-msp
aufmass-app


## OBD Diagnose (Android) – Ordner `android/`
Native Android-App (Kotlin) für ELM327-Bluetooth-Adapter: Fehlerspeicher lesen/löschen, Live-Daten, VIN.
APK bauen: GitHub → Actions → "Android APK" → Artefakt `obd-diagnose-debug-apk`.

### Datenquellen
Fehlercode-Beschreibungen (9.533 genormte OBD-II-Codes, DE): [OBDex](https://github.com/foerbsnavi/OBDex), Daten unter CC0-1.0 (LICENSE-DATA im Repo geprüft).
