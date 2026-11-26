package com.bustanquran.core.data

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
import com.bustanquran.core.data.database.model.FamilyEntity
import com.bustanquran.core.data.database.model.RewardEntity
import com.bustanquran.core.data.database.model.ScreenTimeEntity
import com.bustanquran.core.data.preferences.FamilyPreferences
import com.bustanquran.core.data.preferences.UserPreferencesRepository
import com.bustanquran.core.data.repository.UserDataAdminImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Artboard 14 · "Ekspor Data (JSON)" and "Hapus Semua Data". */
class UserDataAdminTest {

    private val database: UserDatabase = mockk(relaxed = true)
    private val familyDao: FamilyDao = mockk()
    private val childProfileDao: ChildProfileDao = mockk()
    private val ayahProgressDao: AyahProgressDao = mockk()
    private val sessionLogDao: SessionLogDao = mockk()
    private val recordingDao: RecordingDao = mockk()
    private val rewardDao: RewardDao = mockk()
    private val screenTimeDao: ScreenTimeDao = mockk()
    private val downloadDao: DownloadDao = mockk()
    private val purchaseDao: PurchaseDao = mockk()
    private val preferences: UserPreferencesRepository = mockk(relaxed = true)

    private lateinit var admin: UserDataAdminImpl

    @Before
    fun setUp() {
        admin = UserDataAdminImpl(
            database = database,
            familyDao = familyDao,
            childProfileDao = childProfileDao,
            ayahProgressDao = ayahProgressDao,
            sessionLogDao = sessionLogDao,
            recordingDao = recordingDao,
            rewardDao = rewardDao,
            screenTimeDao = screenTimeDao,
            downloadDao = downloadDao,
            purchaseDao = purchaseDao,
            preferences = preferences,
        )
    }

    private fun stubChild(profileId: String = "child_1") {
        every { ayahProgressDao.getAllProgress(profileId) } returns flowOf(
            listOf(
                AyahProgressEntity(profileId = profileId, surahId = 1, ayahNo = 1, status = "memorised")
            )
        )
        every { sessionLogDao.getRecentLogs(profileId, any()) } returns flowOf(emptyList())
        every { recordingDao.getRecordingsForProfile(profileId) } returns flowOf(emptyList())
        every { rewardDao.getRewardsForProfile(profileId) } returns flowOf(
            listOf(RewardEntity(profileId = profileId, type = "badge", refId = "badge_first_surah"))
        )
        every { screenTimeDao.getAllScreenTime(profileId) } returns flowOf(
            listOf(ScreenTimeEntity(profileId = profileId, date = "2026-11-26", secondsUsed = 600))
        )
    }

    @Test
    fun `export carries the family, each child and the stored preferences`() = runTest {
        every { familyDao.getFamily() } returns flowOf(FamilyEntity(id = "fam_1"))
        every { childProfileDao.getProfilesForFamily("fam_1") } returns flowOf(
            listOf(
                ChildProfileEntity(
                    id = "child_1",
                    familyId = "fam_1",
                    nickname = "Aisyah",
                    stage = "5-6",
                    avatar = "bird"
                )
            )
        )
        every { preferences.familyPreferences } returns flowOf(
            FamilyPreferences(appLang = "id", wifiOnlyDownloads = false)
        )
        stubChild()
        every { downloadDao.getAllDownloads() } returns flowOf(emptyList())
        every { purchaseDao.getAllPurchases() } returns flowOf(emptyList())

        val root = Json.parseToJsonElement(admin.exportJson(now = 1_700_000_000_000L)).jsonObject

        assertEquals("bustan-quran-export", root["format"]!!.jsonPrimitive.content)
        assertEquals(1_700_000_000_000L, root["exportedAt"]!!.jsonPrimitive.long)
        assertEquals("fam_1", root["family"]!!.jsonObject["id"]!!.jsonPrimitive.content)
        assertEquals("id", root["preferences"]!!.jsonObject["appLang"]!!.jsonPrimitive.content)
        assertEquals(
            false,
            root["preferences"]!!.jsonObject["wifiOnlyDownloads"]!!.jsonPrimitive.content.toBoolean()
        )

        val child = root["profiles"]!!.jsonArray.single().jsonObject
        assertEquals("Aisyah", child["profile"]!!.jsonObject["nickname"]!!.jsonPrimitive.content)
        assertEquals(1, child["progress"]!!.jsonArray.size)
        assertEquals(1, child["rewards"]!!.jsonArray.size)
        assertEquals(600, child["screenTime"]!!.jsonArray.single().jsonObject["secondsUsed"]!!.jsonPrimitive.int)
    }
    @Test
    fun `fresh install still exports a valid document`() = runTest {
        every { familyDao.getFamily() } returns flowOf(null)
        every { preferences.familyPreferences } returns flowOf(FamilyPreferences())
        every { downloadDao.getAllDownloads() } returns flowOf(emptyList())
        every { purchaseDao.getAllPurchases() } returns flowOf(emptyList())

        val root = Json.parseToJsonElement(admin.exportJson(now = 1L)).jsonObject

        assertTrue(root["family"] is JsonNull)
        assertTrue(root["profiles"]!!.jsonArray.isEmpty())
    }

    @Test
    fun `deleteAllData clears the user database and resets preferences`() = runTest {
        coEvery { preferences.resetToDefaults() } just Runs

        admin.deleteAllData()

        verify(exactly = 1) { database.clearAllTables() }
        coVerify(exactly = 1) { preferences.resetToDefaults() }
    }

}

