package com.bustanquran.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bustanquran.core.data.database.model.AyahProgressEntity
import com.bustanquran.core.data.database.model.ChildProfileEntity
import com.bustanquran.core.data.database.model.DownloadEntity
import com.bustanquran.core.data.database.model.FamilyEntity
import com.bustanquran.core.data.database.model.PurchaseEntity
import com.bustanquran.core.data.database.model.RecordingEntity
import com.bustanquran.core.data.database.model.RewardEntity
import com.bustanquran.core.data.database.model.ScreenTimeEntity
import com.bustanquran.core.data.database.model.SessionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Query("SELECT * FROM family WHERE deleted = 0 LIMIT 1")
    fun getFamily(): Flow<FamilyEntity?>

    @Query("SELECT * FROM family WHERE id = :id LIMIT 1")
    suspend fun getFamilyById(id: String): FamilyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFamily(family: FamilyEntity)

    @Query("UPDATE family SET deleted = 1, updated_at = :timestamp WHERE id = :familyId")
    suspend fun softDeleteFamily(familyId: String, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface ChildProfileDao {
    @Query("SELECT * FROM child_profile WHERE family_id = :familyId AND deleted = 0 ORDER BY updated_at ASC")
    fun getProfilesForFamily(familyId: String): Flow<List<ChildProfileEntity>>

    @Query("SELECT * FROM child_profile WHERE id = :id AND deleted = 0 LIMIT 1")
    fun getProfile(id: String): Flow<ChildProfileEntity?>

    @Query("SELECT COUNT(*) FROM child_profile WHERE family_id = :familyId AND deleted = 0")
    suspend fun getProfileCount(familyId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: ChildProfileEntity)

    @Query("UPDATE child_profile SET deleted = 1, updated_at = :timestamp WHERE id = :profileId")
    suspend fun softDeleteProfile(profileId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM child_profile WHERE updated_at > :since")
    suspend fun getProfilesModifiedSince(since: Long): List<ChildProfileEntity>
}

@Dao
interface AyahProgressDao {
    @Query("SELECT * FROM ayah_progress WHERE profile_id = :profileId AND deleted = 0")
    fun getAllProgress(profileId: String): Flow<List<AyahProgressEntity>>

    @Query("SELECT * FROM ayah_progress WHERE profile_id = :profileId AND surah_id = :surahId AND deleted = 0 ORDER BY ayah_no ASC")
    fun getProgressForSurah(profileId: String, surahId: Int): Flow<List<AyahProgressEntity>>

    @Query("SELECT * FROM ayah_progress WHERE profile_id = :profileId AND surah_id = :surahId AND ayah_no = :ayahNo LIMIT 1")
    suspend fun getAyahProgress(profileId: String, surahId: Int, ayahNo: Int): AyahProgressEntity?

    @Query("SELECT * FROM ayah_progress WHERE profile_id = :profileId AND status = 'memorised' AND due_at <= :currentTime AND deleted = 0 ORDER BY due_at ASC LIMIT :limit")
    fun getDueMurojaahItems(profileId: String, currentTime: Long, limit: Int = 5): Flow<List<AyahProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: AyahProgressEntity)

    @Query("SELECT * FROM ayah_progress WHERE updated_at > :since")
    suspend fun getProgressModifiedSince(since: Long): List<AyahProgressEntity>
}

@Dao
interface SessionLogDao {
    @Query("SELECT * FROM session_log WHERE profile_id = :profileId AND deleted = 0 ORDER BY started_at DESC LIMIT :limit")
    fun getRecentLogs(profileId: String, limit: Int = 20): Flow<List<SessionLogEntity>>

    @Query("SELECT * FROM session_log WHERE profile_id = :profileId AND started_at >= :startTime AND deleted = 0 ORDER BY started_at ASC")
    fun getLogsSince(profileId: String, startTime: Long): Flow<List<SessionLogEntity>>

    @Query("SELECT COUNT(*) FROM session_log WHERE profile_id = :profileId AND deleted = 0")
    suspend fun countCompletedSessions(profileId: String): Int

    @Query("SELECT COALESCE(SUM(stars), 0) FROM session_log WHERE profile_id = :profileId AND deleted = 0")
    suspend fun sumStars(profileId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SessionLogEntity)

    @Query("SELECT * FROM session_log WHERE updated_at > :since")
    suspend fun getLogsModifiedSince(since: Long): List<SessionLogEntity>
}

@Dao
interface RecordingDao {
    @Query("SELECT * FROM recording WHERE profile_id = :profileId AND deleted = 0 ORDER BY created_at DESC")
    fun getRecordingsForProfile(profileId: String): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recording WHERE profile_id = :profileId AND surah_id = :surahId AND deleted = 0 ORDER BY created_at DESC")
    fun getRecordingsForSurah(profileId: String, surahId: Int): Flow<List<RecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: RecordingEntity)

    @Query("UPDATE recording SET keep = :keep, updated_at = :timestamp WHERE id = :recordingId")
    suspend fun updateKeepStatus(recordingId: String, keep: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE recording SET deleted = 1, updated_at = :timestamp WHERE id = :recordingId")
    suspend fun softDeleteRecording(recordingId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM recording WHERE keep = 0 AND created_at < :expirationTime AND deleted = 0")
    suspend fun getExpiredRecordings(expirationTime: Long): List<RecordingEntity>
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM reward WHERE profile_id = :profileId AND deleted = 0 ORDER BY earned_at DESC")
    fun getRewardsForProfile(profileId: String): Flow<List<RewardEntity>>

    @Query("SELECT * FROM reward WHERE profile_id = :profileId AND type = :type AND deleted = 0")
    fun getRewardsByType(profileId: String, type: String): Flow<List<RewardEntity>>

    @Query("SELECT COUNT(*) > 0 FROM reward WHERE profile_id = :profileId AND type = :type AND ref_id = :refId AND deleted = 0")
    suspend fun hasReward(profileId: String, type: String, refId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: RewardEntity)

    @Query("SELECT * FROM reward WHERE updated_at > :since")
    suspend fun getRewardsModifiedSince(since: Long): List<RewardEntity>
}

@Dao
interface ScreenTimeDao {
    @Query("SELECT * FROM screen_time WHERE profile_id = :profileId AND date = :date LIMIT 1")
    fun getScreenTime(profileId: String, date: String): Flow<ScreenTimeEntity?>

    /** Whole history for one child; used by the parent JSON export. */
    @Query("SELECT * FROM screen_time WHERE profile_id = :profileId ORDER BY date DESC")
    fun getAllScreenTime(profileId: String): Flow<List<ScreenTimeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertScreenTime(screenTime: ScreenTimeEntity)

    /**
     * Seeds today's row so the increments below always hit something. Without it
     * the `UPDATE ... SET x = x + :delta` queries match zero rows on the first
     * session of the day and silently drop the time.
     */
    @Query("INSERT OR IGNORE INTO screen_time (profile_id, date, seconds_used, extra_granted) VALUES (:profileId, :date, 0, 0)")
    suspend fun ensureScreenTimeRow(profileId: String, date: String)

    @Query("UPDATE screen_time SET seconds_used = seconds_used + :seconds WHERE profile_id = :profileId AND date = :date")
    suspend fun addSeconds(profileId: String, date: String, seconds: Int)

    @Query("UPDATE screen_time SET extra_granted = extra_granted + :minutes WHERE profile_id = :profileId AND date = :date")
    suspend fun addExtraMinutes(profileId: String, date: String, minutes: Int)
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download ORDER BY juz ASC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM download WHERE reciter_id = :reciterId AND juz = :juz LIMIT 1")
    fun getDownload(reciterId: String, juz: Int): Flow<DownloadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDownload(download: DownloadEntity)

    @Query("DELETE FROM download WHERE reciter_id = :reciterId AND juz = :juz")
    suspend fun deleteDownload(reciterId: String, juz: Int)
}

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchase WHERE deleted = 0")
    fun getAllPurchases(): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchase WHERE product_id = :productId AND deleted = 0 LIMIT 1")
    suspend fun getPurchase(productId: String): PurchaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPurchase(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchase WHERE updated_at > :since")
    suspend fun getPurchasesModifiedSince(since: Long): List<PurchaseEntity>
}
