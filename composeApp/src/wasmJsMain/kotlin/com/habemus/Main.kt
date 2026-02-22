package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

external object console {
    fun log(message: String)
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    console.log("🔥 main() called by WASM runtime")
    
    // Check if root exists immediately
    val rootImmediate = document.getElementById("root")
    console.log("📍 Root element immediately: ${if (rootImmediate != null) "FOUND" else "NOT FOUND"}")
    
    // Schedule initialization after a brief delay
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
    console.log("⏱️ initializeApp() called after setTimeout")
    
    val root = document.getElementById("root")
    console.log("📍 Root element in initializeApp: ${if (root != null) "FOUND" else "NOT FOUND"}")
    
    if (root != null) {
        try {
            console.log("🎨 Creating ComposeViewport...")
            ComposeViewport(viewportContainer = root) {
                AppWithViewModel()
            }
            console.log("✅ ComposeViewport created successfully")
        } catch (e: Throwable) {
            console.log("❌ Error creating ComposeViewport: ${e.message}")
        }
    } else {
        console.log("❌ Cannot initialize: root element not found")
    }
}











