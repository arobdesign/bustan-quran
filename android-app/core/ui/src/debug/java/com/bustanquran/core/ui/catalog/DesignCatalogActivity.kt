package com.bustanquran.core.ui.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanTheme

/**
 * Debug-only host for [DesignCatalogScreen].
 *
 * It exists because P-18 STEP 1 stops for a review of the design system before
 * any screen is rewritten: instead of wiring a route into the product
 * navigation, the catalog opens as a plain activity in the debug source set.
 *
 * Launch it with:
 * `adb shell am start -n com.bustanquran.app/com.bustanquran.core.ui.catalog.DesignCatalogActivity`
 */
class DesignCatalogActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BustanTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BustanSand)
                        .safeDrawingPadding(),
                ) {
                    DesignCatalogScreen()
                }
            }
        }
    }
}
