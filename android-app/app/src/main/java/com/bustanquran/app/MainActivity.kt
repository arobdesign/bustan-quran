package com.bustanquran.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bustanquran.shared.BustanQuranAppRoot
import com.bustanquran.shared.locale.localizedContext

/**
 * The whole UI is rendered by [BustanQuranAppRoot] inside `:shared`, which is the
 * same tree the iOS host presents.
 *
 * The language chosen in Settings/Onboarding is applied here because it has to
 * happen before any resource is resolved; [BustanQuranAppRoot] recreates the
 * activity whenever the preference changes.
 */
class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localizedContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BustanQuranAppRoot()
        }
    }
}
