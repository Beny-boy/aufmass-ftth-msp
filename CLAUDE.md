# Aufmaß FTTH/MSP

Offline-fähige PWA zur Aufmaß-Erfassung (FTTH/MSP). UI-Sprache: Deutsch.

## Struktur
- `index.html` – komplette App in einer Datei (HTML, CSS, JS inline, ~1,9 MB). Manifest als data-URI eingebettet.
- `sw.js` – Service Worker (Cache `aufmass-ftth-msp-v1`, Cache-first mit Hintergrund-Update).
- Kein Build-System, keine Tests, keine Abhängigkeitsverwaltung im Repo.

## Arbeitsregeln
- Änderungen minimal halten; `index.html` ist groß – gezielt mit Grep/Read-Offset arbeiten, nie komplett lesen.
- Texte in der UI auf Deutsch lassen.
- Bei Änderungen am App-Shell-Cache in `sw.js` `CACHE_NAME` hochzählen, sonst sehen Nutzer ggf. alte Versionen.
- Nutzerdaten liegen in `localStorage` – Datenformat nicht ohne Migration ändern.
- Regeln siehe `.claude/rules/`.
