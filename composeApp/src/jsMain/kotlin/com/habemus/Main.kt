package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

external object console {
    fun log(message: String)
}

@OptIn(ExperimentalComposeUiApi::class)
@JsExport
fun main() {
    console.log("✅ main() called from JavaScript")
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












