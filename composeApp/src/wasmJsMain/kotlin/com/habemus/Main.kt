package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

external object console {
    fun log(message: String)
}

object WasmApp {
    init {
        console.log("🔥 WASM module loaded, scheduling initialization...")
        
        // Schedule initialization after DOM is ready
        window.setTimeout(
            { 
                initializeApp()
                null 
            },
            100
        )
    }
    
    @OptIn(ExperimentalComposeUiApi::class)
    private fun initializeApp() {
        console.log("⏱️ initializeApp() called")
        
        val root = document.getElementById("root")
        console.log("📍 Root element found: ${root != null}")
        
        if (root != null) {
            try {
                console.log("🎨 Creating ComposeViewport...")
                // Clear loader
                root.innerHTML = ""
                ComposeViewport(viewportContainer = root) {
                    AppWithViewModel()
                }
                console.log("✅ App rendered!")
            } catch (e: Throwable) {
                console.log("❌ Error: ${e.message}")
            }
        }
    }
}

// Force initialization by accessing the object
fun main() {
    // Access WasmApp to trigger its init block
    WasmApp
}











