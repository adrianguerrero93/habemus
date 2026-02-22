// Explicit loader for WASM module
console.log('[app-loader.js] Loading...');

// Import the compiled Kotlin/WASM module
import('./calculadora.js').then((module) => {
    console.log('[app-loader.js] WASM module loaded');
    
    // Wait a bit for Kotlin code to initialize
    setTimeout(() => {
        if (typeof globalThis.initializeCompose === 'function') {
            console.log('✅ Found globalThis.initializeCompose, calling...');
            globalThis.initializeCompose();
        } else {
            console.log('⚠️ initializeCompose still not in globalThis');
            console.log('globalThis keys:', Object.keys(globalThis).filter(k => !k.startsWith('webkit')).slice(0, 20));
        }
    }, 500);
}).catch((err) => {
    console.error('[app-loader.js] Failed to load WASM:', err);
});

