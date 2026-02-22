package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.js.JsExport

external object console {
    fun log(message: String)
}

@JsExport
fun startApp() {
    console.log("🔥 startApp() called!")
    
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











