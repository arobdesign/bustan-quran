package com.bustanquran.app

import android.app.Application
import com.bustanquran.shared.di.initKoin

/**
 * Android entry point. All wiring lives in `:shared`, so the app only boots the
 * Koin graph; there is no annotation processing in this module anymore.
 */
class BustanQuranApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}
