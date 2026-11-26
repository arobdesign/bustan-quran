package com.bustanquran.core.data.repository

import com.bustanquran.core.data.database.UserDatabase
import com.bustanquran.core.data.database.dao.AyahProgressDao
import com.bustanquran.core.data.database.dao.ChildProfileDao
import com.bustanquran.core.data.database.dao.DownloadDao
import com.bustanquran.core.data.database.dao.FamilyDao
import com.bustanquran.core.data.database.dao.PurchaseDao
import com.bustanquran.core.data.database.dao.RecordingDao
import com.bustanquran.core.data.database.dao.RewardDao
import com.bustanquran.core.data.database.dao.ScreenTimeDao
import com.bustanquran.core.data.database.dao.SessionLogDao
import com.bustanquran.core.data.database.model.AyahProgressEntity
import com.bustanquran.core.data.database.model.ChildProfileEntity
import com.bustanquran.core.data.database.model.DownloadEntity
import com.bustanquran.core.data.database.model.FamilyEntity
import com.bustanquran.core.data.database.model.PurchaseEntity
import com.bustanquran.core.data.database.model.RecordingEntity
import com.bustanquran.core.data.database.model.RewardEntity
import com.bustanquran.core.data.database.model.ScreenTimeEntity
import com.bustanquran.core.data.database.model.SessionLogEntity
import com.bustanquran.core.data.preferences.FamilyPreferences
import com.bustanquran.core.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

/**
 * The two parent-facing data controls of the Settings screen
 * (artboard 14 · Pengaturan orang tua).
 *
 * "Ekspor Data (JSON)" hands the family everything stored on this phone and
 * "Hapus Semua Data" puts the device back to a first-run state. Both are local:
 * the app is offline-first, so there is nothing to delete on a server.
 */
interface UserDataAdmin {

    /** One JSON document: family, profiles, progress, logs, rewards, screen time, purchases. */
    suspend fun exportJson(now: Long = System.currentTimeMillis()): String

    /** Wipes `user.db` and resets the family preferences to their strict defaults. */
    suspend fun deleteAllData()
}

class UserDataAdminImpl constructor(
    private val database: UserDatabase,
    private val familyDao: FamilyDao,
    private val childProfileDao: ChildProfileDao,
    private val ayahProgressDao: AyahProgressDao,
    private val sessionLogDao: SessionLogDao,
    private val recordingDao: RecordingDao,
    private val rewardDao: RewardDao,
    private val screenTimeDao: ScreenTimeDao,
    private val downloadDao: DownloadDao,
    private val purchaseDao: PurchaseDao,
    private val preferences: UserPreferencesRepository,
) : UserDataAdmin {

    override suspend fun exportJson(now: Long): String = withContext(Dispatchers.IO) {
        val family = familyDao.getFamily().first()
        val familyPreferences = preferences.familyPreferences.first()
        val profiles = family?.let { childProfileDao.getProfilesForFamily(it.id).first() }.orEmpty()
        val children = profiles.map { profile -> readChild(profile) }
        val downloads = downloadDao.getAllDownloads().first()
        val purchases = purchaseDao.getAllPurchases().first()

        buildJsonObject {
            put("format", EXPORT_FORMAT)
            put("version", EXPORT_VERSION)
            put("exportedAt", now)
            put("preferences", familyPreferences.toJson())
            put("family", family?.toJson() ?: JsonNull)
            putJsonArray("profiles") { children.forEach { add(it.toJson()) } }
            putJsonArray("downloads") { downloads.forEach { add(it.toJson()) } }
            putJsonArray("purchases") { purchases.forEach { add(it.toJson()) } }
        }.toString()
    }

    override suspend fun deleteAllData() = withContext(Dispatchers.IO) {
        database.clearAllTables()
        preferences.resetToDefaults()
    }

    private suspend fun readChild(profile: ChildProfileEntity) = ChildExport(
        profile = profile,
        progress = ayahProgressDao.getAllProgress(profile.id).first(),
        sessionLogs = sessionLogDao.getRecentLogs(profile.id, EXPORTED_LOG_LIMIT).first(),
        recordings = recordingDao.getRecordingsForProfile(profile.id).first(),
        rewards = rewardDao.getRewardsForProfile(profile.id).first(),
        screenTime = screenTimeDao.getAllScreenTime(profile.id).first(),
    )

    private data class ChildExport(
        val profile: ChildProfileEntity,
        val progress: List<AyahProgressEntity>,
        val sessionLogs: List<SessionLogEntity>,
        val recordings: List<RecordingEntity>,
        val rewards: List<RewardEntity>,
        val screenTime: List<ScreenTimeEntity>,
    ) {
        fun toJson(): JsonObject = buildJsonObject {
            put("profile", profile.toJson())
            putJsonArray("progress") { progress.forEach { add(it.toJson()) } }
            putJsonArray("sessionLogs") { sessionLogs.forEach { add(it.toJson()) } }
            putJsonArray("recordings") { recordings.forEach { add(it.toJson()) } }
            putJsonArray("rewards") { rewards.forEach { add(it.toJson()) } }
            putJsonArray("screenTime") { screenTime.forEach { add(it.toJson()) } }
        }
    }

    companion object {
        const val EXPORT_FORMAT = "bustan-quran-export"
        const val EXPORT_VERSION = 1

        /** A full history would dwarf the document; the newest activities are enough. */
        const val EXPORTED_LOG_LIMIT = 500
    }
}

private fun FamilyPreferences.toJson(): JsonObject = buildJsonObject {
    put("appLang", appLang)
    put("dailyLimitMin", dailyLimitMin)
    put("backupRecordings", backupRecordings)
    put("analyticsOptIn", analyticsOptIn)
    put("music", music)
    put("wifiOnlyDownloads", wifiOnlyDownloads)
}

private fun FamilyEntity.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("plan", plan)
    put("createdAt", createdAt)
    put("updatedAt", updatedAt)
    put("consentJson", consentJson)
    put("deleted", deleted)
}

private fun ChildProfileEntity.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("familyId", familyId)
    put("nickname", nickname)
    put("stage", stage)
    put("avatar", avatar)
    put("settingsJson", settingsJson)
    put("updatedAt", updatedAt)
    put("deleted", deleted)
}

private fun AyahProgressEntity.toJson(): JsonObject = buildJsonObject {
    put("surahId", surahId)
    put("ayahNo", ayahNo)
    put("status", status)
    put("fadeLevel", fadeLevel)
    put("ease", ease)
    put("intervalDays", intervalDays)
    put("dueAt", dueAt)
    put("lastReviewedAt", lastReviewedAt)
    put("updatedAt", updatedAt)
}

private fun SessionLogEntity.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("type", type)
    put("refId", refId)
    put("startedAt", startedAt)
    put("durationSec", durationSec)
    put("correct", correct)
    put("total", total)
    put("stars", stars)
}

private fun RecordingEntity.toJson(): JsonObject = buildJsonObject {
    put("id", id)
    put("surahId", surahId)
    put("ayahFrom", ayahFrom)
    put("ayahTo", ayahTo)
    put("createdAt", createdAt)
    put("keep", keep)
    // The audio file itself stays on the phone; only its path is exported.
    put("filePath", filePath)
}

private fun RewardEntity.toJson(): JsonObject = buildJsonObject {
    put("type", type)
    put("refId", refId)
    put("earnedAt", earnedAt)
}

private fun ScreenTimeEntity.toJson(): JsonObject = buildJsonObject {
    put("date", date)
    put("secondsUsed", secondsUsed)
    put("extraGranted", extraGranted)
}

private fun DownloadEntity.toJson(): JsonObject = buildJsonObject {
    put("reciterId", reciterId)
    put("juz", juz)
    put("status", status)
    put("bytes", bytes)
    put("updatedAt", updatedAt)
}

private fun PurchaseEntity.toJson(): JsonObject = buildJsonObject {
    put("productId", productId)
    put("state", state)
    put("verifiedAt", verifiedAt)
    put("updatedAt", updatedAt)
}
