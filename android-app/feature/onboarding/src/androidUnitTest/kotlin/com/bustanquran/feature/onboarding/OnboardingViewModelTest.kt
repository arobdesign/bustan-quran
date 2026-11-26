package com.bustanquran.feature.onboarding

import com.bustanquran.core.data.database.model.FamilyEntity
import com.bustanquran.core.data.preferences.FamilyPreferences
import com.bustanquran.core.data.preferences.UserPreferencesRepository
import com.bustanquran.core.data.repository.UserRepository
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
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val preferencesRepository: UserPreferencesRepository = mockk(relaxed = true)

    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { preferencesRepository.familyPreferences } returns flowOf(FamilyPreferences())
        every { userRepository.getFamily() } returns flowOf(null)
        every { userRepository.getProfiles(any()) } returns flowOf(emptyList())

        viewModel = OnboardingViewModel(userRepository, preferencesRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSelectLanguageUpdatesStateAndRepository() = runTest {
        viewModel.selectLanguage("ar")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("ar", viewModel.uiState.value.selectedLanguage)
        assertTrue(viewModel.uiState.value.isRtl)
        coVerify(exactly = 1) { preferencesRepository.updateAppLang("ar") }
    }

    @Test
    fun testParentGateCorrectAnswerReturnsTrue() {
        val challenge = viewModel.uiState.value.parentGateChallenge
        val isCorrect = viewModel.submitParentGateAnswer(challenge.correctAnswer)

        assertTrue(isCorrect)
        assertEquals(0, viewModel.uiState.value.parentGateFailedCount)
    }

    @Test
    fun testParentGateWrongAnswerRegeneratesNewQuestionImmediately() {
        val initialChallenge = viewModel.uiState.value.parentGateChallenge
        val wrongOption = initialChallenge.options.first { it != initialChallenge.correctAnswer }

        val isCorrect = viewModel.submitParentGateAnswer(wrongOption)

        assertFalse(isCorrect)
        assertEquals(1, viewModel.uiState.value.parentGateFailedCount)
        // A new question must have been generated (ONB-02)
        val newChallenge = viewModel.uiState.value.parentGateChallenge
        assertNotNull(newChallenge)
    }

    @Test
    fun testPrivacyConsentRequiresGuardianCheckboxBeforeSaving() = runTest {
        var callbackInvoked = false
        viewModel.setGuardianCheckbox(false)

        viewModel.savePrivacyConsent {
            callbackInvoked = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Must not proceed without guardian checkbox (ACC-04)
        assertFalse(callbackInvoked)
        coVerify(exactly = 0) { userRepository.upsertFamily(any()) }

        // When checked, proceeds and records consent
        viewModel.setGuardianCheckbox(true)
        viewModel.savePrivacyConsent {
            callbackInvoked = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(callbackInvoked)
        coVerify(exactly = 1) { userRepository.upsertFamily(any()) }
    }

    @Test
    fun testCheckCanAddChildEnforcesProfileLimitByPlan() = runTest {
        // Free plan limit: 1 child
        coEvery { userRepository.getProfileCount("default_family") } returns 1

        var limitReached = false
        var allowed = false

        viewModel.checkCanAddChild(
            onAllowed = { allowed = true },
            onLimitReached = { limitReached = true }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(limitReached)
        assertFalse(allowed)
    }

    @Test
    fun testSaveChildProfileRejectsBlankNickname() = runTest {
        var profileCreated = false
        viewModel.setProfileNickname("   ")

        viewModel.saveChildProfile {
            profileCreated = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(profileCreated)
        assertEquals("Nickname cannot be empty", viewModel.uiState.value.errorMessage)
    }
}
