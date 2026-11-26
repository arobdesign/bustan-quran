package com.bustanquran.shared.locale

import android.content.Context
import com.bustanquran.core.data.preferences.AppLocaleStore

/**
 * Context for `attachBaseContext()` whose resources already use the language
 * stored in Settings/Onboarding.
 *
 * `:app` only knows `:shared`, so this is the entry point it calls; the actual
 * preference handling lives in `:core:data` next to the DataStore that owns it.
 */
fun localizedContext(base: Context): Context = AppLocaleStore.wrap(base)
