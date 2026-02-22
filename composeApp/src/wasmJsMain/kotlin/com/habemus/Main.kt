package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

external object console {
    fun log(message: String)
}

@OptIn(ExperimentalComposeUiApi::class)
fun initializeCompose() {
    console.log("⏱️ initializeCompose() called")
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

// Module-level property with side effects during initialization
// This MUST run when the module loads
val autoInit = run {
    console.log("🔥 Main.kt autoInit - scheduling setupUI in 200ms")
    window.setTimeout({
        console.log("🔥 Timeout fired, calling initializeCompose")
        initializeCompose()
        null
    }, 200)
    null // return null to the property
}











