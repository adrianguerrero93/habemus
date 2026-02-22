package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Main is called by WASM runtime
    // Schedule initialization after a brief delay to let DOM settle
    window.setTimeout(
        { initializeApp(); null },
        100
    )
}

@OptIn(ExperimentalComposeUiApi::class)
private fun initializeApp() {
    val root = document.getElementById("root")
    if (root != null) {
        ComposeViewport(viewportContainer = root) {
            AppWithViewModel()
        }
    }
}










