package com.bustanquran.feature.quran.home

import com.bustanquran.core.data.database.model.ChildProfileEntity
import com.bustanquran.core.data.database.model.ScreenTimeEntity
import com.bustanquran.core.data.database.model.SessionLogEntity
import com.bustanquran.core.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ScreenTimeGuard triggers when secondsUsed reaches dailyLimit plus extra`() {
        val guard = ScreenTimeGuard(dailyLimitMinutes = 30)
        assertFalse(guard.isLimitReached(secondsUsed = 1799, extraMinutes = 0))
        assertTrue(guard.isLimitReached(secondsUsed = 1800, extraMinutes = 0))
        assertFalse(guard.isLimitReached(secondsUsed = 1800, extraMinutes = 10))
        assertTrue(guard.isLimitReached(secondsUsed = 2400, extraMinutes = 10))
    }

    @Test
    fun `loadProfile loads stage goal, stars, and screen time`() = runTest {
        val repo = mockk<UserRepository>(relaxed = true)
        every { repo.getProfile("p1") } returns flowOf(
            ChildProfileEntity(
                id = "p1", familyId = "f1", nickname = "Amina", stage = "3-4",
                avatar = "fox", updatedAt = 1L, deleted = false
            )
        )
        every { repo.getRecentLogs("p1", any()) } returns flowOf(
            listOf(
                SessionLogEntity(id = "l1", profileId = "p1", type = "game", refId = "balon", stars = 3),
                SessionLogEntity(id = "l2", profileId = "p1", type = "hafalan", refId = "114", stars = 2)
            )
        )
        every { repo.getScreenTime("p1", any()) } returns flowOf(
            ScreenTimeEntity(profileId = "p1", date = "2026-11-25", secondsUsed = 120, extraGranted = 0)
        )

        val vm = HomeViewModel(repo)
        vm.loadProfile("p1")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("Amina", state.nickname)
        assertEquals("3-4", state.stage)
        assertEquals(10, state.dailyGoalMinutes)
        assertEquals(5, state.stars)
        assertEquals(120, state.secondsToday)
        assertFalse(state.isTimeLimitReached)
    }

    @Test
    fun `grantExtraScreenTimeMinutes lifts the rest screen and persists ten minutes`() = runTest {
        val repo = mockk<UserRepository>(relaxed = true)
        val date = java.time.LocalDate.now().toString()
        var extraGrantedMinutes = 0
        every { repo.getProfile("p1") } returns flowOf(
            ChildProfileEntity(
                id = "p1", familyId = "f1", nickname = "Amina", stage = "5-6",
                avatar = "fox", updatedAt = 1L, deleted = false
            )
        )
        every { repo.getRecentLogs("p1", any()) } returns flowOf(emptyList())
        // 30 minutes limit for the 5-6 stage, exactly used up.
        every { repo.getScreenTime("p1", date) } answers {
            flowOf(
                ScreenTimeEntity(
                    profileId = "p1",
                    date = date,
                    secondsUsed = 1800,
                    extraGranted = extraGrantedMinutes
                )
            )
        }
        coEvery { repo.addExtraScreenTimeMinutes("p1", date, 10) } answers { extraGrantedMinutes += 10 }

        val vm = HomeViewModel(repo)
        vm.loadProfile("p1")
        advanceUntilIdle()
        assertTrue(vm.uiState.value.isTimeLimitReached)

        vm.grantExtraScreenTimeMinutes()
        advanceUntilIdle()

        assertEquals(1800, vm.uiState.value.secondsToday)
        assertFalse(vm.uiState.value.isTimeLimitReached)
        assertEquals(10, extraGrantedMinutes)
    }
}
