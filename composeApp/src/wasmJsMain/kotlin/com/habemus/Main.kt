package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

external object console {
    fun log(message: String)
}

@OptIn(ExperimentalComposeUiApi::class)
fun initializeCalculadora() {
    console.log("🔥 initializeCalculadora() called!")
    
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











