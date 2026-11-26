package com.bustanquran.core.data.repository

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
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Family & Profiles
    fun getFamily(): Flow<FamilyEntity?>
    suspend fun upsertFamily(family: FamilyEntity)
    fun getProfiles(familyId: String): Flow<List<ChildProfileEntity>>
    fun getProfile(profileId: String): Flow<ChildProfileEntity?>
    suspend fun upsertProfile(profile: ChildProfileEntity)
    suspend fun deleteProfile(profileId: String)
    suspend fun getProfileCount(familyId: String): Int

    // Ayah Progress & Murojaah
    fun getProgressForProfile(profileId: String): Flow<List<AyahProgressEntity>>
    fun getProgressForSurah(profileId: String, surahId: Int): Flow<List<AyahProgressEntity>>
    suspend fun getAyahProgress(profileId: String, surahId: Int, ayahNo: Int): AyahProgressEntity?
    fun getDueMurojaahItems(profileId: String, currentTime: Long, limit: Int = 5): Flow<List<AyahProgressEntity>>
    suspend fun upsertProgress(progress: AyahProgressEntity)

    // Session Logs
    fun getRecentLogs(profileId: String, limit: Int = 20): Flow<List<SessionLogEntity>>
    suspend fun insertSessionLog(log: SessionLogEntity)

    /** Number of finished learning sessions — feeds the badge rules (RWD-03). */
    suspend fun countSessions(profileId: String): Int

    /** Stars collected across every finished session — feeds the badge rules (RWD-03). */
    suspend fun totalStars(profileId: String): Int

    // Recordings
    fun getRecordings(profileId: String): Flow<List<RecordingEntity>>
    suspend fun insertRecording(recording: RecordingEntity)
    suspend fun updateKeepRecording(recordingId: String, keep: Boolean)
    suspend fun softDeleteRecording(recordingId: String)

    // Rewards
    fun getRewards(profileId: String): Flow<List<RewardEntity>>
    suspend fun hasReward(profileId: String, type: String, refId: String): Boolean
    suspend fun insertReward(reward: RewardEntity)

    // Screen Time
    fun getScreenTime(profileId: String, date: String): Flow<ScreenTimeEntity?>
    suspend fun upsertScreenTime(screenTime: ScreenTimeEntity)
    suspend fun addScreenTimeSeconds(profileId: String, date: String, seconds: Int)
    suspend fun addExtraScreenTimeMinutes(profileId: String, date: String, minutes: Int)

    // Downloads
    fun getAllDownloads(): Flow<List<DownloadEntity>>
    suspend fun upsertDownload(download: DownloadEntity)

    // Purchases
    fun getAllPurchases(): Flow<List<PurchaseEntity>>
    suspend fun getPurchase(productId: String): PurchaseEntity?
    suspend fun upsertPurchase(purchase: PurchaseEntity)
}

class UserRepositoryImpl constructor(
    private val familyDao: FamilyDao,
    private val childProfileDao: ChildProfileDao,
    private val ayahProgressDao: AyahProgressDao,
    private val sessionLogDao: SessionLogDao,
    private val recordingDao: RecordingDao,
    private val rewardDao: RewardDao,
    private val screenTimeDao: ScreenTimeDao,
    private val downloadDao: DownloadDao,
    private val purchaseDao: PurchaseDao
) : UserRepository {

    override fun getFamily(): Flow<FamilyEntity?> = familyDao.getFamily()
    override suspend fun upsertFamily(family: FamilyEntity) = familyDao.upsertFamily(family)

    override fun getProfiles(familyId: String): Flow<List<ChildProfileEntity>> =
        childProfileDao.getProfilesForFamily(familyId)

    override fun getProfile(profileId: String): Flow<ChildProfileEntity?> =
        childProfileDao.getProfile(profileId)

    override suspend fun upsertProfile(profile: ChildProfileEntity) =
        childProfileDao.upsertProfile(profile)

    override suspend fun deleteProfile(profileId: String) =
        childProfileDao.softDeleteProfile(profileId)

    override suspend fun getProfileCount(familyId: String): Int =
        childProfileDao.getProfileCount(familyId)

    override fun getProgressForProfile(profileId: String): Flow<List<AyahProgressEntity>> =
        ayahProgressDao.getAllProgress(profileId)

    override fun getProgressForSurah(profileId: String, surahId: Int): Flow<List<AyahProgressEntity>> =
        ayahProgressDao.getProgressForSurah(profileId, surahId)

    override suspend fun getAyahProgress(profileId: String, surahId: Int, ayahNo: Int): AyahProgressEntity? =
        ayahProgressDao.getAyahProgress(profileId, surahId, ayahNo)

    override fun getDueMurojaahItems(profileId: String, currentTime: Long, limit: Int): Flow<List<AyahProgressEntity>> =
        ayahProgressDao.getDueMurojaahItems(profileId, currentTime, limit)

    override suspend fun upsertProgress(progress: AyahProgressEntity) =
        ayahProgressDao.upsertProgress(progress)

    override fun getRecentLogs(profileId: String, limit: Int): Flow<List<SessionLogEntity>> =
        sessionLogDao.getRecentLogs(profileId, limit)

    override suspend fun insertSessionLog(log: SessionLogEntity) =
        sessionLogDao.insertLog(log)

    override suspend fun countSessions(profileId: String): Int =
        sessionLogDao.countCompletedSessions(profileId)

    override suspend fun totalStars(profileId: String): Int =
        sessionLogDao.sumStars(profileId)

    override fun getRecordings(profileId: String): Flow<List<RecordingEntity>> =
        recordingDao.getRecordingsForProfile(profileId)

    override suspend fun insertRecording(recording: RecordingEntity) =
        recordingDao.insertRecording(recording)

    override suspend fun updateKeepRecording(recordingId: String, keep: Boolean) =
        recordingDao.updateKeepStatus(recordingId, keep)

    override suspend fun softDeleteRecording(recordingId: String) =
        recordingDao.softDeleteRecording(recordingId)

    override fun getRewards(profileId: String): Flow<List<RewardEntity>> =
        rewardDao.getRewardsForProfile(profileId)

    override suspend fun hasReward(profileId: String, type: String, refId: String): Boolean =
        rewardDao.hasReward(profileId, type, refId)

    override suspend fun insertReward(reward: RewardEntity) =
        rewardDao.insertReward(reward)

    override fun getScreenTime(profileId: String, date: String): Flow<ScreenTimeEntity?> =
        screenTimeDao.getScreenTime(profileId, date)

    override suspend fun upsertScreenTime(screenTime: ScreenTimeEntity) =
        screenTimeDao.upsertScreenTime(screenTime)

    override suspend fun addScreenTimeSeconds(profileId: String, date: String, seconds: Int) {
        if (seconds <= 0) return
        screenTimeDao.ensureScreenTimeRow(profileId, date)
        screenTimeDao.addSeconds(profileId, date, seconds)
    }

    override suspend fun addExtraScreenTimeMinutes(profileId: String, date: String, minutes: Int) {
        if (minutes <= 0) return
        screenTimeDao.ensureScreenTimeRow(profileId, date)
        screenTimeDao.addExtraMinutes(profileId, date, minutes)
    }

    override fun getAllDownloads(): Flow<List<DownloadEntity>> = downloadDao.getAllDownloads()
    override suspend fun upsertDownload(download: DownloadEntity) = downloadDao.upsertDownload(download)

    override fun getAllPurchases(): Flow<List<PurchaseEntity>> = purchaseDao.getAllPurchases()
    override suspend fun getPurchase(productId: String): PurchaseEntity? = purchaseDao.getPurchase(productId)
    override suspend fun upsertPurchase(purchase: PurchaseEntity) = purchaseDao.upsertPurchase(purchase)
}
