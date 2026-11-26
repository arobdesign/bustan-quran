package com.bustanquran.feature.quran.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.data.repository.UserRepository
import java.time.LocalDate
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeUiState(
    val nickname: String = "",
    val stage: String = "5-6",
    val stars: Int = 0,
    val secondsToday: Int = 0,
    val dailyGoalMinutes: Int = 15,
    val isTimeLimitReached: Boolean = false
)

/** Tracks active foreground learning time for the selected child and applies the daily limit. */
class ScreenTimeGuard(private val dailyLimitMinutes: Int) {
    fun isLimitReached(secondsUsed: Int, extraMinutes: Int): Boolean =
        secondsUsed >= (dailyLimitMinutes + extraMinutes) * 60
}

class HomeViewModel constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var activeProfileId: String? = null
    private var guard = ScreenTimeGuard(30)
    private var trackingJob: Job? = null
    private var isHomeVisible = false

    fun loadProfile(profileId: String?) {
        if (profileId.isNullOrBlank() || profileId == activeProfileId) return
        activeProfileId = profileId
        viewModelScope.launch {
            val profile = userRepository.getProfile(profileId).first()
            val goal = when (profile?.stage) {
                "3-4" -> 10
                "7-10" -> 20
                else -> 15
            }
            val screenTimeLimit = when (profile?.stage) {
                "3-4" -> 20
                "7-10" -> 45
                else -> 30
            }
            guard = ScreenTimeGuard(screenTimeLimit)
            val stars = userRepository.getRecentLogs(profileId, limit = 10_000).first().sumOf { it.stars }
            val date = LocalDate.now().toString()
            val today = userRepository.getScreenTime(profileId, date).first()
            _uiState.value = HomeUiState(
                nickname = profile?.nickname.orEmpty(),
                stage = profile?.stage ?: "5-6",
                stars = stars,
                secondsToday = today?.secondsUsed ?: 0,
                dailyGoalMinutes = goal,
                isTimeLimitReached = guard.isLimitReached(today?.secondsUsed ?: 0, today?.extraGranted ?: 0)
            )
            if (isHomeVisible) startTracking(profileId)
        }
    }

    fun setHomeVisible(visible: Boolean) {
        isHomeVisible = visible
        if (!visible) {
            trackingJob?.cancel()
            trackingJob = null
            return
        }
        val profileId = activeProfileId ?: return
        startTracking(profileId)
    }

    private fun startTracking(profileId: String) {
        if (trackingJob?.isActive == true) return
        trackingJob = viewModelScope.launch {
            val date = LocalDate.now().toString()
            val today = userRepository.getScreenTime(profileId, date).first()
            var seconds = today?.secondsUsed ?: 0
            val extra = today?.extraGranted ?: 0
            var pendingSeconds = 0
            // Re-sync from the database on every resume: a parent may have granted
            // extra minutes while this screen was in the back stack.
            _uiState.value = _uiState.value.copy(
                secondsToday = seconds,
                isTimeLimitReached = guard.isLimitReached(seconds, extra)
            )
            try {
                while (!guard.isLimitReached(seconds, extra)) {
                    kotlinx.coroutines.delay(1_000)
                    seconds += 1
                    pendingSeconds += 1
                    _uiState.value = _uiState.value.copy(
                        secondsToday = seconds,
                        isTimeLimitReached = guard.isLimitReached(seconds, extra)
                    )
                    if (pendingSeconds >= 15) {
                        userRepository.addScreenTimeSeconds(profileId, date, pendingSeconds)
                        pendingSeconds = 0
                    }
                }
            } finally {
                if (pendingSeconds > 0) withContext(NonCancellable) {
                    userRepository.addScreenTimeSeconds(profileId, date, pendingSeconds)
                }
            }
        }
    }

    /**
     * PAR-04 — the parent-approved "+10 minutes" from the rest screen.
     *
     * Suspending on purpose: the caller pops the rest screen only once the grant
     * is in the database, otherwise [startTracking] would read the old allowance
     * on resume and bounce the child straight back to the rest screen.
     */
    suspend fun grantExtraScreenTimeMinutes(minutes: Int = EXTRA_MINUTES_PER_GRANT) {
        val profileId = activeProfileId ?: return
        val date = LocalDate.now().toString()
        userRepository.addExtraScreenTimeMinutes(profileId, date, minutes)
        val today = userRepository.getScreenTime(profileId, date).first()
        val seconds = today?.secondsUsed ?: _uiState.value.secondsToday
        val extra = today?.extraGranted ?: minutes
        _uiState.value = _uiState.value.copy(
            secondsToday = seconds,
            isTimeLimitReached = guard.isLimitReached(seconds, extra)
        )
        if (isHomeVisible) startTracking(profileId)
    }

    companion object {
        /** Extra learning time a parent unlocks from the rest screen. */
        const val EXTRA_MINUTES_PER_GRANT = 10
    }
}
