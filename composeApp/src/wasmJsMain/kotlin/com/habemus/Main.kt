package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

external object console {
    fun log(message: String)
}

// Singleton that initializes on creation
object AppInitializer {
    init {
        console.log("🔥 Kotlin module loaded, initializing...")
        
        window.setTimeout({
            initApp()
            null
        }, 100)
    }
}

// Trigger initialization by accessing the object
fun forceInit() {
    AppInitializer.toString() // Force access
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











