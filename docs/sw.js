const CACHE_NAME = 'calculadora-v1.1';
const URLS_TO_CACHE = [
    '/habemus/',
    '/habemus/index.html',
    '/habemus/manifest.json',
    '/habemus/icon-192.png',
    '/habemus/calculadora.js',
    '/habemus/skiko.wasm',
    '/habemus/e9b28911e687b1ee6b42.wasm'
];

self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME).then(cache => {
            return cache.addAll(URLS_TO_CACHE).catch(() => {
                // Silently fail if some assets don't exist yet
                return Promise.resolve();
            });
        })
    );
    self.skipWaiting();
});

self.addEventListener('activate', event => {
    event.waitUntil(
        caches.keys().then(cacheNames => {
            return Promise.all(
                cacheNames.map(cacheName => {
                    if (cacheName !== CACHE_NAME) {
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
    self.clients.claim();
});

self.addEventListener('fetch', event => {
    // Skip cross-origin requests
    if (!event.request.url.includes('/habemus/')) {
        return;
    }
    
    event.respondWith(
        caches.match(event.request).then(response => {
            if (response) {
                return response;
            }
            return fetch(event.request).then(response => {
                if (!response || response.status !== 200 || response.type !== 'basic') {
                    return response;
                }
                const responseToCache = response.clone();
                caches.open(CACHE_NAME).then(cache => {
                    cache.put(event.request, responseToCache);
                });
                return response;
            }).catch(() => {
                // Return cached version or offline page if fetch fails
                return caches.match(event.request);
            });
        })
    );
});
