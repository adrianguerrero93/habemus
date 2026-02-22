package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

external object console {
    fun log(message: String)
}

// This code runs when the module loads
fun main() {
    console.log("🔥 WASM main() entry point")
    
    window.setTimeout({
        initApp()
        null
    }, 100)
}

@OptIn(ExperimentalComposeUiApi::class)
private fun initApp() {
    console.log("⏱️ initApp() called")
    val root = document.getElementById("root") ?: run {
        console.log("❌ root not found")
        return
    }
    
    try {
        console.log("🎨 Creating ComposeViewport...")
        root.innerHTML = ""
        ComposeViewport(viewportContainer = root) {
            AppWithViewModel()
        }
        console.log("✅ App rendered!")
    } catch (e: Throwable) {
        console.log("❌ Error: ${e.message}")
    }
}











