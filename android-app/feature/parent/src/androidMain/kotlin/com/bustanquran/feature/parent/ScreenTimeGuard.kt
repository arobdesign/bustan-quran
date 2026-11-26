package com.bustanquran.feature.parent

/**
 * PAR-04, SET-01..03 — Foreground learning time guard per profile per day.
 * When limit is reached, caller navigates to IstirahatScreen.
 * +10 minutes extension is ONLY unlocked after ParentGate passes.
 */
data class ScreenTimeState(
    val profileId: String,
    val dateIso: String,
    val usedSeconds: Int = 0,
    val limitMinutes: Int = 30,
    val extraMinutesGranted: Int = 0
) {
    val totalAllowedSeconds: Int get() = (limitMinutes + extraMinutesGranted) * 60
    val isLimitReached: Boolean get() = limitMinutes > 0 && usedSeconds >= totalAllowedSeconds
    val remainingSeconds: Int get() = (totalAllowedSeconds - usedSeconds).coerceAtLeast(0)
}

class ScreenTimeGuard(initialState: ScreenTimeState) {
    private var state = initialState

    fun tick(elapsedSeconds: Int): ScreenTimeState {
        if (elapsedSeconds > 0) {
            state = state.copy(usedSeconds = state.usedSeconds + elapsedSeconds)
        }
        return state
    }

    /** Must only be invoked after ParentGate verification. */
    fun grantExtraTenMinutes(parentGatePassed: Boolean): ScreenTimeState {
        if (!parentGatePassed) return state
        state = state.copy(extraMinutesGranted = state.extraMinutesGranted + 10)
        return state
    }

    fun currentState(): ScreenTimeState = state
}

object ParentDataExporter {
    /** Generates a GDPR-compliant JSON export string without external dependencies. */
    fun exportSummaryJson(
        familyId: String,
        profileCount: Int,
        totalStars: Int,
        exportedAtIso: String
    ): String {
        return """
            {
              "family_id": "$familyId",
              "profile_count": $profileCount,
              "total_stars": $totalStars,
              "exported_at": "$exportedAtIso"
            }
        """.trimIndent()
    }
}
