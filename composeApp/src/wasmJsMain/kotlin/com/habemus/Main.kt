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

// Self-executing assignment
val hack: String = js("""
(function() {
    var fn = function() { com.habemus.initializeCompose(); };
    window.initializeCompose = fn;
    window.__habemusReady = true;
    console.log('🔥 Kotlin module: initializeCompose ready');
    return 'ready';
})()
""")












