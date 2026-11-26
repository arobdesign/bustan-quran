package com.bustanquran.shared.di

import android.content.Context
import com.bustanquran.core.data.di.dataModules
import com.bustanquran.feature.games.di.gamesModule
import com.bustanquran.feature.islamic.di.islamicModule
import com.bustanquran.feature.onboarding.di.onboardingModule
import com.bustanquran.feature.parent.di.parentModule
import com.bustanquran.feature.quran.di.quranModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Single Koin graph for the whole application.
 *
 * The data modules take the application context explicitly so neither of them
 * depends on Android-only Koin APIs; feature modules only contribute their view
 * models. iOS will reuse `dataModules` once the Room databases move to commonMain.
 */
fun initKoin(context: Context) {
    startKoin {
        androidLogger(Level.ERROR)
        androidContext(context)
        modules(
            dataModules(context) + listOf(
                onboardingModule,
                quranModule(context),
                gamesModule,
                islamicModule,
                parentModule,
            )
        )
    }
}
