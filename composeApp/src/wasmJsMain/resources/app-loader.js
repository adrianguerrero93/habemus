// Explicit loader for WASM module
console.log('[app-loader.js] Loading...');

// Import the compiled Kotlin/WASM module
import('./calculadora.js').then((module) => {
    console.log('[app-loader.js] WASM module loaded');
    console.log('[app-loader.js] Module exports:', Object.keys(module).slice(0, 10));
    
    // Try to find and call initialization function
    if (typeof module.initializeCompose === 'function') {
        console.log('✅ Found initializeCompose, calling...');
        module.initializeCompose();
    } else {
        console.log('⚠️ initializeCompose not found in exports');
    }
}).catch((err) => {
    console.error('[app-loader.js] Failed to load WASM:', err);
});
