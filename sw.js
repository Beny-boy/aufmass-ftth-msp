// Service Worker für "Aufmaß FTTH/MSP"
// Sorgt dafür, dass die App offline funktioniert und installierbar ist.

const CACHE_NAME = 'aufmass-ftth-msp-v1';
const APP_SHELL = [
  './',
  './index.html'
];

// Beim Installieren: App-Shell (die HTML-Datei selbst) in den Cache legen
self.addEventListener('install', (event) => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(APP_SHELL))
  );
});

// Beim Aktivieren: alte Cache-Versionen aufräumen
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(
        keys
          .filter((key) => key !== CACHE_NAME)
          .map((key) => caches.delete(key))
      )
    )
  );
  self.clients.claim();
});

// Bei jeder Anfrage: zuerst aus dem Cache bedienen (funktioniert offline),
// im Hintergrund die neue Version nachladen und für's nächste Mal aktualisieren
self.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') return;

  event.respondWith(
    caches.match(event.request).then((cached) => {
      const fetchPromise = fetch(event.request)
        .then((response) => {
          if (response && response.status === 200) {
            const clone = response.clone();
            caches.open(CACHE_NAME).then((cache) => cache.put(event.request, clone));
          }
          return response;
        })
        .catch(() => cached); // offline -> auf Cache zurückfallen

      return cached || fetchPromise;
    })
  );
});
