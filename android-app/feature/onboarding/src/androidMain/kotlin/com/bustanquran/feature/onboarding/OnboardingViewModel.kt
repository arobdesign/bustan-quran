package com.bustanquran.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.data.database.model.ChildProfileEntity
import com.bustanquran.core.data.database.model.FamilyEntity
import com.bustanquran.core.data.preferences.UserPreferencesRepository
import com.bustanquran.core.data.repository.UserRepository
import com.bustanquran.feature.onboarding.parentgate.ParentGateChallenge
import com.bustanquran.feature.onboarding.parentgate.ParentGateChallengeGenerator
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val selectedLanguage: String = "en",
    val isRtl: Boolean = false,
    val isLocalOnlyMode: Boolean = false,
    val backupRecordingsConsent: Boolean = false,
    val analyticsConsent: Boolean = false,
    val guardianCheckboxChecked: Boolean = false,
    val profileNickname: String = "",
    val profileStage: String = "5-6",
    val profileAvatar: String = "camel",
    val parentGateChallenge: ParentGateChallenge = ParentGateChallengeGenerator.generate("en"),
    val parentGateFailedCount: Int = 0,
    val errorMessage: String? = null
)

class OnboardingViewModel constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    val family = userRepository.getFamily()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val profiles: StateFlow<List<ChildProfileEntity>> = userRepository.getProfiles("default_family")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            preferencesRepository.familyPreferences.collect { prefs ->
                _uiState.value = _uiState.value.copy(
                    selectedLanguage = prefs.appLang,
                    isRtl = prefs.appLang == "ar",
                    backupRecordingsConsent = prefs.backupRecordings,
                    analyticsConsent = prefs.analyticsOptIn
                )
            }
        }
    }

    fun selectLanguage(lang: String) {
        val isRtl = lang.lowercase() == "ar"
        _uiState.value = _uiState.value.copy(
            selectedLanguage = lang,
            isRtl = isRtl,
            parentGateChallenge = ParentGateChallengeGenerator.generate(lang)
        )
        viewModelScope.launch {
            preferencesRepository.updateAppLang(lang)
        }
    }

    fun setBackupRecordingsConsent(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(backupRecordingsConsent = enabled)
    }

    fun setAnalyticsConsent(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(analyticsConsent = enabled)
    }

    fun setGuardianCheckbox(checked: Boolean) {
        _uiState.value = _uiState.value.copy(guardianCheckboxChecked = checked)
    }

    fun savePrivacyConsent(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.guardianCheckboxChecked) return

        viewModelScope.launch {
            preferencesRepository.updateBackupRecordings(state.backupRecordingsConsent)
            preferencesRepository.updateAnalyticsOptIn(state.analyticsConsent)

            val consentJson = """
                {
                    "policyVersion": "1.0.0",
                    "timestamp": ${System.currentTimeMillis()},
                    "progressConsent": true,
                    "backupRecordings": ${state.backupRecordingsConsent},
                    "analyticsOptIn": ${state.analyticsConsent},
                    "guardianApproved": true
                }
            """.trimIndent()

            val familyId = family.value?.id ?: "default_family"
            userRepository.upsertFamily(
                FamilyEntity(
                    id = familyId,
                    plan = family.value?.plan ?: "free",
                    consentJson = consentJson
                )
            )
            onSuccess()
        }
    }

    fun enableLocalOnlyMode() {
        _uiState.value = _uiState.value.copy(isLocalOnlyMode = true)
    }

    fun setProfileNickname(name: String) {
        _uiState.value = _uiState.value.copy(profileNickname = name)
    }

    fun setProfileStage(stage: String) {
        _uiState.value = _uiState.value.copy(profileStage = stage)
    }

    fun setProfileAvatar(avatar: String) {
        _uiState.value = _uiState.value.copy(profileAvatar = avatar)
    }

    fun checkCanAddChild(onAllowed: () -> Unit, onLimitReached: () -> Unit) {
        viewModelScope.launch {
            val familyId = family.value?.id ?: "default_family"
            val count = userRepository.getProfileCount(familyId)
            val plan = family.value?.plan ?: "free"
            val maxAllowed = when (plan) {
                "juz_amma_lifetime" -> 3
                "quran_30_lifetime", "family_monthly", "family_yearly" -> 6
                else -> 1 // free plan allows 1
            }
            if (count < maxAllowed) {
                onAllowed()
            } else {
                onLimitReached()
            }
        }
    }

    fun saveChildProfile(onSuccess: (String) -> Unit) {
        val state = _uiState.value
        val nickname = state.profileNickname.trim()
        if (nickname.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Nickname cannot be empty")
            return
        }

        viewModelScope.launch {
            val familyId = family.value?.id ?: "default_family"
            val newProfileId = UUID.randomUUID().toString()
            val profile = ChildProfileEntity(
                id = newProfileId,
                familyId = familyId,
                nickname = nickname,
                stage = state.profileStage,
                avatar = state.profileAvatar
            )
            userRepository.upsertProfile(profile)
            _uiState.value = _uiState.value.copy(profileNickname = "", errorMessage = null)
            onSuccess(newProfileId)
        }
    }

    fun submitParentGateAnswer(answer: Int): Boolean {
        val state = _uiState.value
        return if (answer == state.parentGateChallenge.correctAnswer) {
            _uiState.value = _uiState.value.copy(parentGateFailedCount = 0)
            true
        } else {
            // New random question immediately after 1 wrong answer (ONB-02)
            _uiState.value = _uiState.value.copy(
                parentGateFailedCount = state.parentGateFailedCount + 1,
                parentGateChallenge = ParentGateChallengeGenerator.generate(state.selectedLanguage)
            )
            false
        }
    }

    fun refreshParentGateChallenge() {
        _uiState.value = _uiState.value.copy(
            parentGateChallenge = ParentGateChallengeGenerator.generate(_uiState.value.selectedLanguage)
        )
    }
}
