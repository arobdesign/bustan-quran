package com.bustanquran.feature.islamic.di

import com.bustanquran.feature.islamic.HijaiyahLessonViewModel
import com.bustanquran.feature.islamic.TajwidLessonViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Koin bindings contributed by the Islamic feature (P-18 lesson screens). */
val islamicModule: Module = module {
    viewModelOf(::HijaiyahLessonViewModel)
    viewModelOf(::TajwidLessonViewModel)
}
