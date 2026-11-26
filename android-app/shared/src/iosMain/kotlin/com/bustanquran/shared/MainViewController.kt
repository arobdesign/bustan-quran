package com.bustanquran.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Entry point consumed by `iosApp/ContentView.swift`.
 *
 * Phase 2 only proves the framework links and renders: the screens still live in
 * the `androidMain` source sets of the feature modules and will be moved to
 * `commonMain` in Phase 3, at which point this function renders
 * `BustanQuranAppRoot()` instead of the placeholder.
 */
fun MainViewController(): UIViewController = ComposeUIViewController {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Bustan Qur'an — iOS UI lands in Phase 3")
        }
    }
}
