package com.habemus

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val root = document.getElementById("root") ?: return
    ComposeViewport(viewportContainer = root) {
        AppWithViewModel()
    }
}

// This object initializes the app when the module loads
@OptIn(ExperimentalComposeUiApi::class)
object AppInitializer {
    init {
        val root = document.getElementById("root")
        if (root != null) {
            ComposeViewport(viewportContainer = root) {
                AppWithViewModel()
            }
        }
    }
}







