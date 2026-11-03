# API-Konventionen (nur wenn dieses Projekt APIs verwendet)

- Bestehende URL-, Benennungs- und Versionierungskonventionen einhalten.
- Eingaben validieren und Fehler mit passenden Statuscodes bzw. Fehlertypen behandeln.
- Keine internen Exceptions, Stacktraces oder Geheimnisse an Clients ausgeben.
- Autorisierung serverseitig prüfen; nicht mit Authentifizierung gleichsetzen.
- Retry, Zeitlimits, Idempotenz und Rate Limits bei externen Aufrufen berücksichtigen.
- Schnittstellenänderungen inklusive Beispielen, Kompatibilität und Tests dokumentieren.
