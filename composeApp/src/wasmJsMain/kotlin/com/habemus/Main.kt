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
    console.log("🔥 initializeCompose() CALLED")
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

// Module-level initialization via js()
val _init: String = js("(window.initializeCompose = () => com.habemus.initializeCompose(), console.log('Module loaded'), 'ok')")












