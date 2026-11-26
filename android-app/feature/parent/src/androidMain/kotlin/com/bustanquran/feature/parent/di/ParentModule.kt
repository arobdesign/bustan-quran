package com.bustanquran.feature.parent.di

import com.bustanquran.feature.parent.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Koin bindings contributed by the parent area. */
val parentModule: Module = module {
    viewModelOf(::SettingsViewModel)
}
