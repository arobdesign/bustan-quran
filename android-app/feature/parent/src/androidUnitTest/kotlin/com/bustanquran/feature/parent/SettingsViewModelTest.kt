package com.bustanquran.feature.parent

import com.bustanquran.core.data.preferences.FamilyPreferences
import com.bustanquran.core.data.preferences.UserPreferencesRepository
import com.bustanquran.core.data.repository.UserDataAdmin
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val admin: UserDataAdmin = mockk(relaxed = true)
    private val preferences: UserPreferencesRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(wifiOnly: Boolean = true): SettingsViewModel {
        every { preferences.familyPreferences } returns flowOf(FamilyPreferences(wifiOnlyDownloads = wifiOnly))
        return SettingsViewModel(admin, preferences).also { testDispatcher.scheduler.advanceUntilIdle() }
    }

    @Test
    fun `the toggle shows what was stored, not a hardcoded default`() = runTest {
        val viewModel = viewModel(wifiOnly = false)

        assertFalse(viewModel.uiState.value.wifiOnly)
    }

    @Test
    fun `flipping the toggle is written to the preferences`() = runTest {
        val viewModel = viewModel(wifiOnly = true)

        viewModel.setWifiOnly(false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.wifiOnly)
        coVerify(exactly = 1) { preferences.updateWifiOnlyDownloads(false) }
    }

    @Test
    fun `export hands the json document to the screen`() = runTest {
        coEvery { admin.exportJson(any()) } returns """{"format":"bustan-quran-export"}"""
        val viewModel = viewModel()
        var received: String? = null

        viewModel.exportJson { received = it }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("""{"format":"bustan-quran-export"}""", received)
        assertFalse(viewModel.uiState.value.busy)
    }

    @Test
    fun `a failed export reports null instead of crashing`() = runTest {
        coEvery { admin.exportJson(any()) } throws IllegalStateException("disk full")
        val viewModel = viewModel()
        var received: String? = "untouched"

        viewModel.exportJson { received = it }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(received)
        assertFalse(viewModel.uiState.value.busy)
    }

    @Test
    fun `delete wipes the data and only then tells the screen`() = runTest {
        val viewModel = viewModel()
        var wiped = false

        viewModel.deleteAllData { wiped = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(wiped)
        coVerify(exactly = 1) { admin.deleteAllData() }
    }
}
