package com.bustanquran.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family")
data class FamilyEntity(
    @PrimaryKey
    val id: String,
    val plan: String = "free", // free, juz_amma_lifetime, quran_30_lifetime, family_monthly, family_yearly
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "consent_json")
    val consentJson: String = "{}",
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)

@Entity(tableName = "child_profile")
data class ChildProfileEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "family_id")
    val familyId: String,
    val nickname: String,
    val stage: String, // 3-4, 5-6, 7-10
    val avatar: String, // e.g. "bird", "camel", "sheep", "bee"
    @ColumnInfo(name = "settings_json")
    val settingsJson: String = "{}",
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)

@Entity(
    tableName = "ayah_progress",
    primaryKeys = ["profile_id", "surah_id", "ayah_no"]
)
data class AyahProgressEntity(
    @ColumnInfo(name = "profile_id")
    val profileId: String,
    @ColumnInfo(name = "surah_id")
    val surahId: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    val status: String = "none", // none, learning, memorised, due
    @ColumnInfo(name = "fade_level")
    val fadeLevel: Int = 0, // 0: full, 1: some words hidden, 2: first letters only, 3: blank
    val ease: Float = 2.5f, // SM-2 ease factor
    @ColumnInfo(name = "interval_days")
    val intervalDays: Int = 0, // SM-2 interval
    @ColumnInfo(name = "due_at")
    val dueAt: Long = 0L,
    @ColumnInfo(name = "last_reviewed_at")
    val lastReviewedAt: Long = 0L,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)

@Entity(tableName = "session_log")
data class SessionLogEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "profile_id")
    val profileId: String,
    val type: String, // game, listen, hafalan, lesson
    @ColumnInfo(name = "ref_id")
    val refId: String, // e.g. surah_106, game_balon_huruf
    @ColumnInfo(name = "started_at")
    val startedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "duration_sec")
    val durationSec: Int = 0,
    val correct: Int = 0,
    val total: Int = 0,
    val stars: Int = 0,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)

@Entity(tableName = "recording")
data class RecordingEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "profile_id")
    val profileId: String,
    @ColumnInfo(name = "surah_id")
    val surahId: Int,
    @ColumnInfo(name = "ayah_from")
    val ayahFrom: Int,
    @ColumnInfo(name = "ayah_to")
    val ayahTo: Int,
    @ColumnInfo(name = "file_path")
    val filePath: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    val keep: Boolean = false, // if false, auto-delete after 90 days
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)

@Entity(
    tableName = "reward",
    primaryKeys = ["profile_id", "type", "ref_id"]
)
data class RewardEntity(
    @ColumnInfo(name = "profile_id")
    val profileId: String,
    val type: String, // card, badge
    @ColumnInfo(name = "ref_id")
    val refId: String, // e.g. card_camel, badge_first_surah
    @ColumnInfo(name = "earned_at")
    val earnedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)

@Entity(
    tableName = "screen_time",
    primaryKeys = ["profile_id", "date"]
)
data class ScreenTimeEntity(
    @ColumnInfo(name = "profile_id")
    val profileId: String,
    val date: String, // YYYY-MM-DD
    @ColumnInfo(name = "seconds_used")
    val secondsUsed: Int = 0,
    @ColumnInfo(name = "extra_granted")
    val extraGranted: Int = 0 // minutes added via ParentGate
)

@Entity(
    tableName = "download",
    primaryKeys = ["reciter_id", "juz"]
)
data class DownloadEntity(
    @ColumnInfo(name = "reciter_id")
    val reciterId: String,
    val juz: Int,
    val status: String = "pending", // pending, downloading, completed, failed
    val bytes: Long = 0L,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "purchase")
data class PurchaseEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: String, // juz_amma_lifetime, quran_30_lifetime, family_monthly, family_yearly
    @ColumnInfo(name = "purchase_token")
    val purchaseToken: String,
    val state: String, // purchased, pending, acknowledged
    @ColumnInfo(name = "verified_at")
    val verifiedAt: Long = 0L,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)
