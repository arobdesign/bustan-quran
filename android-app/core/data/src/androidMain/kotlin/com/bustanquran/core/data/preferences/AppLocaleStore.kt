package com.bustanquran.core.data.preferences

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Applies the stored `appLang` preference to the resources of the process.
 *
 * DataStore is asynchronous, but `attachBaseContext()` needs the language
 * synchronously, so every write to `appLang` is mirrored into a small
 * `SharedPreferences` file. The mirrored value is the only thing read at
 * activity start; DataStore remains the source of truth for everything else.
 */
object AppLocaleStore {

    const val DEFAULT_LANG: String = "en"

    private const val PREFS_NAME = "app_locale"
    private const val KEY_LANG = "appLang"

    fun storedLang(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANG, DEFAULT_LANG)
            ?: DEFAULT_LANG

    fun persist(context: Context, lang: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANG, lang)
            .apply()
    }

    /** Language the resources are currently configured with (e.g. "en", "ar"). */
    fun appliedLang(context: Context): String =
        context.resources.configuration.locales[0].language

    /** Context whose resources are already localised, for `attachBaseContext`. */
    fun wrap(base: Context): Context {
        val locale = Locale.forLanguageTag(storedLang(base))
        val configuration = Configuration(base.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return base.createConfigurationContext(configuration)
    }
}
