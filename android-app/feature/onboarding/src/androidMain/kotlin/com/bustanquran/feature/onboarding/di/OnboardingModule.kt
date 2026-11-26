package com.bustanquran.feature.onboarding.di

import com.bustanquran.feature.onboarding.OnboardingViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Koin bindings contributed by the onboarding feature. */
val onboardingModule: Module = module {
    viewModelOf(::OnboardingViewModel)
}
