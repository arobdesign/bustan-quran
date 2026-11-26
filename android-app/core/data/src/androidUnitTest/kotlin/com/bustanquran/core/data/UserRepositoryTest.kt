package com.bustanquran.core.data

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
import com.bustanquran.core.data.repository.UserRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private val familyDao: FamilyDao = mockk()
    private val childProfileDao: ChildProfileDao = mockk()
    private val ayahProgressDao: AyahProgressDao = mockk()
    private val sessionLogDao: SessionLogDao = mockk()
    private val recordingDao: RecordingDao = mockk()
    private val rewardDao: RewardDao = mockk()
    private val screenTimeDao: ScreenTimeDao = mockk()
    private val downloadDao: DownloadDao = mockk()
    private val purchaseDao: PurchaseDao = mockk()

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        repository = UserRepositoryImpl(
            familyDao = familyDao,
            childProfileDao = childProfileDao,
            ayahProgressDao = ayahProgressDao,
            sessionLogDao = sessionLogDao,
            recordingDao = recordingDao,
            rewardDao = rewardDao,
            screenTimeDao = screenTimeDao,
            downloadDao = downloadDao,
            purchaseDao = purchaseDao
        )
    }

    @Test
    fun testFamilyAndChildProfileOperations() = runTest {
        val family = FamilyEntity(
            id = "family_123",
            plan = "juz_amma_lifetime",
            consentJson = "{\"guardianApproved\":true,\"timestamp\":1700000000}"
        )
        coEvery { familyDao.upsertFamily(family) } returns Unit
        every { familyDao.getFamily() } returns flowOf(family)

        repository.upsertFamily(family)
        val retrieved = repository.getFamily().first()
        assertNotNull(retrieved)
        assertEquals("juz_amma_lifetime", retrieved?.plan)

        val profile1 = ChildProfileEntity(
            id = "child_1",
            familyId = "family_123",
            nickname = "Adam",
            stage = "5-6",
            avatar = "camel"
        )
        coEvery { childProfileDao.upsertProfile(profile1) } returns Unit
        every { childProfileDao.getProfilesForFamily("family_123") } returns flowOf(listOf(profile1))
        coEvery { childProfileDao.getProfileCount("family_123") } returns 1
        coEvery { childProfileDao.softDeleteProfile("child_1", any()) } returns Unit

        repository.upsertProfile(profile1)
        val profiles = repository.getProfiles("family_123").first()
        assertEquals(1, profiles.size)
        assertEquals("Adam", profiles[0].nickname)
        assertEquals(1, repository.getProfileCount("family_123"))

        repository.deleteProfile("child_1")
        coVerify(exactly = 1) { childProfileDao.softDeleteProfile("child_1", any()) }
    }

    @Test
    fun testAyahProgressAndMurojaahQueue() = runTest {
        val now = System.currentTimeMillis()
        val dueItem = AyahProgressEntity(
            profileId = "child_1",
            surahId = 106,
            ayahNo = 1,
            status = "memorised",
            fadeLevel = 3,
            ease = 2.5f,
            intervalDays = 3,
            dueAt = now - 1000L
        )

        every { ayahProgressDao.getDueMurojaahItems("child_1", now, 5) } returns flowOf(listOf(dueItem))
        coEvery { ayahProgressDao.upsertProgress(dueItem) } returns Unit

        repository.upsertProgress(dueItem)
        val dueItems = repository.getDueMurojaahItems("child_1", now, 5).first()
        assertEquals(1, dueItems.size)
        assertEquals(1, dueItems[0].ayahNo)
        assertEquals("memorised", dueItems[0].status)
    }

    @Test
    fun testSessionLogAndRewards() = runTest {
        val log = SessionLogEntity(
            id = "session_1",
            profileId = "child_1",
            type = "game",
            refId = "balon_huruf",
            durationSec = 120,
            correct = 5,
            total = 5,
            stars = 3
        )
        coEvery { sessionLogDao.insertLog(log) } returns Unit
        every { sessionLogDao.getRecentLogs("child_1", 20) } returns flowOf(listOf(log))

        repository.insertSessionLog(log)
        val logs = repository.getRecentLogs("child_1").first()
        assertEquals(1, logs.size)
        assertEquals(3, logs[0].stars)

        val reward = RewardEntity(
            profileId = "child_1",
            type = "card",
            refId = "animal_camel"
        )
        coEvery { rewardDao.insertReward(reward) } returns Unit
        coEvery { rewardDao.hasReward("child_1", "card", "animal_camel") } returns true
        coEvery { rewardDao.hasReward("child_1", "badge", "first_surah") } returns false

        repository.insertReward(reward)
        assertTrue(repository.hasReward("child_1", "card", "animal_camel"))
        assertFalse(repository.hasReward("child_1", "badge", "first_surah"))
    }

    @Test
    fun testScreenTimeAndPurchases() = runTest {
        val date = "2026-11-24"
        val screenTime = ScreenTimeEntity(
            profileId = "child_1",
            date = date,
            secondsUsed = 600,
            extraGranted = 0
        )
        every { screenTimeDao.getScreenTime("child_1", date) } returns flowOf(screenTime)
        coEvery { screenTimeDao.upsertScreenTime(screenTime) } returns Unit
        coEvery { screenTimeDao.ensureScreenTimeRow("child_1", date) } returns Unit
        coEvery { screenTimeDao.addSeconds("child_1", date, 300) } returns Unit
        coEvery { screenTimeDao.addExtraMinutes("child_1", date, 10) } returns Unit

        repository.upsertScreenTime(screenTime)
        repository.addScreenTimeSeconds("child_1", date, 300)
        repository.addExtraScreenTimeMinutes("child_1", date, 10)

        // Both increments must seed the row first, otherwise the UPDATE matches nothing.
        coVerify(exactly = 2) { screenTimeDao.ensureScreenTimeRow("child_1", date) }
        coVerify(exactly = 1) { screenTimeDao.addSeconds("child_1", date, 300) }
        coVerify(exactly = 1) { screenTimeDao.addExtraMinutes("child_1", date, 10) }

        val purchase = PurchaseEntity(
            productId = "juz_amma_lifetime",
            purchaseToken = "token_abc_123",
            state = "purchased"
        )
        coEvery { purchaseDao.upsertPurchase(purchase) } returns Unit
        coEvery { purchaseDao.getPurchase("juz_amma_lifetime") } returns purchase

        repository.upsertPurchase(purchase)
        val retrievedPurchase = repository.getPurchase("juz_amma_lifetime")
        assertNotNull(retrievedPurchase)
        assertEquals("token_abc_123", retrievedPurchase?.purchaseToken)
    }

    @Test
    fun testDownloads() = runTest {
        val download = DownloadEntity(
            reciterId = "child_boy",
            juz = 30,
            status = "completed",
            bytes = 45_000_000L
        )
        every { downloadDao.getAllDownloads() } returns flowOf(listOf(download))
        coEvery { downloadDao.upsertDownload(download) } returns Unit

        repository.upsertDownload(download)
        val allDownloads = repository.getAllDownloads().first()
        assertEquals(1, allDownloads.size)
        assertEquals("completed", allDownloads[0].status)
    }
}
