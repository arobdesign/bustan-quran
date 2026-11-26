package com.bustanquran.feature.onboarding.parentgate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bustanquran.feature.onboarding.OnboardingViewModel
import com.bustanquran.feature.onboarding.ParentGateScreen

/**
 * Navigation guard ensuring no parent-only screen is accessible without solving the ParentGate.
 * (PRD ONB-03 requirement)
 */
object ParentGateSession {
    private var isUnlocked = false

    fun isParentAuthenticated(): Boolean = isUnlocked

    fun markAuthenticated() {
        isUnlocked = true
    }

    fun lock() {
        isUnlocked = false
    }
}

@Composable
fun ParentGateNavGuard(
    viewModel: OnboardingViewModel,
    onCancelled: () -> Unit,
    content: @Composable () -> Unit
) {
    var authenticated by remember { mutableStateOf(ParentGateSession.isParentAuthenticated()) }

    if (authenticated) {
        content()
    } else {
        ParentGateScreen(
            viewModel = viewModel,
            onPassed = {
                ParentGateSession.markAuthenticated()
                authenticated = true
            },
            onCancelled = onCancelled
        )
    }
}
