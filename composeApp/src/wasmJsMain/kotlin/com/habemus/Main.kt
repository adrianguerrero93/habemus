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
    console.log("🔥 initializeCompose() CALLED from globalThis")
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

// Expose to globalThis
internal fun exposeInit() {
    js("""
        globalThis.initializeCompose = function() { com.habemus.initializeCompose(); };
        console.log('🔥 Assigned initializeCompose to globalThis');
    """)
}

// Call it when module loads - accessed at package level
val _init = run { exposeInit(); null }












