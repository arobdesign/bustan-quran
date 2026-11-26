package com.bustanquran.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

private val Context.familyDataStore: DataStore<FamilyPreferences> by dataStore(
    fileName = "family_preferences.pb",
    serializer = FamilyPreferencesSerializer
)

interface UserPreferencesRepository {
    val familyPreferences: Flow<FamilyPreferences>
    suspend fun updateAppLang(lang: String)
    suspend fun updateDailyLimitMin(minutes: Int)
    suspend fun updateBackupRecordings(enabled: Boolean)
    suspend fun updateAnalyticsOptIn(enabled: Boolean)
    suspend fun updateMusic(music: String)
    suspend fun updateWifiOnlyDownloads(enabled: Boolean)

    /** Used by "Hapus Semua Data": every setting goes back to its strict default. */
    suspend fun resetToDefaults()
}

class UserPreferencesRepositoryImpl constructor(
    private val context: Context
) : UserPreferencesRepository {

    override val familyPreferences: Flow<FamilyPreferences> = context.familyDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(FamilyPreferences())
            } else {
                throw exception
            }
        }

    override suspend fun updateAppLang(lang: String) {
        AppLocaleStore.persist(context, lang)
        context.familyDataStore.updateData { it.copy(appLang = lang) }
    }

    override suspend fun updateDailyLimitMin(minutes: Int) {
        context.familyDataStore.updateData { it.copy(dailyLimitMin = minutes) }
    }

    override suspend fun updateBackupRecordings(enabled: Boolean) {
        context.familyDataStore.updateData { it.copy(backupRecordings = enabled) }
    }

    override suspend fun updateAnalyticsOptIn(enabled: Boolean) {
        context.familyDataStore.updateData { it.copy(analyticsOptIn = enabled) }
    }

    override suspend fun updateMusic(music: String) {
        context.familyDataStore.updateData { it.copy(music = music) }
    }

    override suspend fun updateWifiOnlyDownloads(enabled: Boolean) {
        context.familyDataStore.updateData { it.copy(wifiOnlyDownloads = enabled) }
    }

    override suspend fun resetToDefaults() {
        AppLocaleStore.persist(context, AppLocaleStore.DEFAULT_LANG)
        context.familyDataStore.updateData { FamilyPreferences() }
    }
}
