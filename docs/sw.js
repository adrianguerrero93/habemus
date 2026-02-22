const CACHE_NAME = 'calculadora-v2.6-jsexport';
const ASSETS_TO_CACHE = [
  '/habemus/',
  '/habemus/index.html',
  '/habemus/manifest.json',
];

// Instalar: cachear assets
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      return cache.addAll(ASSETS_TO_CACHE).catch(err => {
        console.log('Cache addAll error:', err);
        return Promise.resolve();
      });
    })
  );
  self.skipWaiting();
});

// Activar: limpiar caches viejos
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames
          .filter(cacheName => cacheName !== CACHE_NAME)
          .map(cacheName => caches.delete(cacheName))
      );
    })
  );
  self.clients.claim();
});

// Fetch: Network-first, fallback to cache
self.addEventListener('fetch', event => {
  const { request } = event;
  const url = new URL(request.url);

  // Solo cachear en el scope de /habemus/
  if (!url.pathname.startsWith('/habemus/')) {
    return;
  }

  // Estrategia: Network-first (conectividad primero, fallback offline)
  event.respondWith(
    fetch(request)
      .then(response => {
        // Cachear respuesta exitosa
        if (response.ok) {
          const responseToCache = response.clone();
          caches.open(CACHE_NAME).then(cache => {
            cache.put(request, responseToCache);
          });
        }
        return response;
      })
      .catch(() => {
        // Si no hay conexión, usar cache
        return caches.match(request).then(cachedResponse => {
          return cachedResponse || new Response('Offline - sin contenido en caché', {
            status: 503,
            statusText: 'Service Unavailable',
            headers: new Headers({
              'Content-Type': 'text/plain'
            })
          });
        });
      })
  );
});

