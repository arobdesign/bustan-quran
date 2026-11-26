package com.bustanquran.feature.games.di

import com.bustanquran.feature.games.AyatGameViewModel
import com.bustanquran.feature.games.LearningSessionRecorder
import com.bustanquran.feature.games.SessionLogStore
import com.bustanquran.feature.games.UserRepositorySessionLogStore
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Koin bindings contributed by the games feature (P-18 screens). */
val gamesModule: Module = module {
    viewModelOf(::AyatGameViewModel)
    single<SessionLogStore> { UserRepositorySessionLogStore(get()) }
    single { LearningSessionRecorder(get(), get()) }
}
