package com.bustanquran.feature.quran.di

import android.content.Context
import com.bustanquran.core.audio.ExoRecitationPlayer
import com.bustanquran.core.audio.RecitationPlayer
import com.bustanquran.feature.quran.home.HomeViewModel
import com.bustanquran.feature.quran.MushafPageViewModel
import com.bustanquran.feature.quran.QuranLibraryViewModel
import com.bustanquran.feature.quran.SurahPlayerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin bindings contributed by the Qur'an feature.
 *
 * The context is passed in (like `dataModules`) so this module never needs the
 * Android-only Koin helpers, which keeps the declaration reusable for iOS.
 */
fun quranModule(context: Context): Module = module {
    // Streams the per-ayah MP3 URLs stored in content.db (`meta.audio_template`).
    // A factory (not a singleton) so every player screen owns its engine and
    // `SurahPlayerViewModel.onCleared` really does stop the audio.
    factory<RecitationPlayer> { ExoRecitationPlayer(context) }

    viewModelOf(::HomeViewModel)
    viewModelOf(::SurahPlayerViewModel)
    viewModelOf(::QuranLibraryViewModel)
    viewModelOf(::MushafPageViewModel)
}
