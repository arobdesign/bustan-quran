package com.bustanquran.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.data.database.model.ChildProfileEntity
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.ProgressPill
import com.bustanquran.core.ui.components.QpkCard
import com.bustanquran.core.ui.components.QpkPillButton
import com.bustanquran.core.ui.components.QpkPrimaryButton
import com.bustanquran.core.ui.theme.QpkCardBorder
import com.bustanquran.core.ui.theme.QpkCoral
import com.bustanquran.core.ui.theme.QpkGold
import com.bustanquran.core.ui.theme.QpkInk
import com.bustanquran.core.ui.theme.QpkMint
import com.bustanquran.core.ui.theme.QpkMuted
import com.bustanquran.core.ui.theme.QpkParentBg
import com.bustanquran.core.ui.theme.QpkPillShape
import com.bustanquran.core.ui.theme.QpkSand
import com.bustanquran.core.ui.theme.QpkTeal
import com.bustanquran.core.ui.theme.QpkTypography
import com.bustanquran.core.ui.theme.QpkWhite

@Composable
fun PrivacyConsentScreen(
    viewModel: OnboardingViewModel,
    onContinue: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QpkParentBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = QpkTeal,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Child Privacy & Consent", style = QpkTypography.headlineLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Compliant with Google Play Families and GDPR. Parents control all data.",
                style = QpkTypography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Mandatory progress
            QpkCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Learning Progress", fontWeight = FontWeight.Bold)
                        Text(text = "Stored locally on device to track child's surahs & stars.", style = QpkTypography.bodyMedium)
                    }
                    ProgressPill(text = "Required")
                }
            }

            // 2. Backup recordings
            QpkCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Voice Recording Backup", fontWeight = FontWeight.Bold)
                        Text(text = "Upload voice to private cloud backup. Default OFF.", style = QpkTypography.bodyMedium)
                    }
                    Switch(
                        checked = uiState.backupRecordingsConsent,
                        onCheckedChange = { viewModel.setBackupRecordingsConsent(it) }
                    )
                }
            }

            // 3. Anonymous Analytics
            QpkCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Anonymous Statistics", fontWeight = FontWeight.Bold)
                        Text(text = "No Ad ID, no names. Helps us improve. Default OFF.", style = QpkTypography.bodyMedium)
                    }
                    Switch(
                        checked = uiState.analyticsConsent,
                        onCheckedChange = { viewModel.setAnalyticsConsent(it) }
                    )
                }
            }

            // Guardian consent checkbox (Mandatory before continue)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.guardianCheckboxChecked,
                    onCheckedChange = { viewModel.setGuardianCheckbox(it) }
                )
                Text(
                    text = "I confirm that I am the parent or legal guardian.",
                    style = QpkTypography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        QpkPrimaryButton(
            text = "Agree & Continue",
            onClick = { viewModel.savePrivacyConsent(onContinue) },
            enabled = uiState.guardianCheckboxChecked,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            isLargeTouchTarget = true
        )
    }
}

@Composable
fun SignInScreen(
    viewModel: OnboardingViewModel,
    onSignedIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onLocalModeSelected: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QpkParentBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Parent Account", style = QpkTypography.headlineLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Children do not need an account.", style = QpkTypography.bodyMedium)
            Spacer(modifier = Modifier.height(20.dp))

            // Segmented Tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(QpkMint.copy(alpha = 0.5f), QpkPillShape)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(if (!isSignUp) QpkWhite else Color.Transparent, QpkPillShape)
                        .clickable { isSignUp = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Sign In", fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Normal)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(if (isSignUp) QpkWhite else Color.Transparent, QpkPillShape)
                        .clickable { isSignUp = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Sign Up", fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Normal)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            if (!isSignUp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onForgotPassword) {
                        Text(text = "Forgot password?", color = QpkTeal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            QpkPrimaryButton(
                text = if (isSignUp) "Create Parent Account" else "Sign In",
                onClick = onSignedIn,
                modifier = Modifier.fillMaxWidth(),
                isLargeTouchTarget = true
            )
        }

        // Local-only mode (ACC-03)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Don't want to create an account? All learning works offline.",
                style = QpkTypography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            QpkPillButton(
                text = "Continue in Local-Only Mode",
                onClick = {
                    viewModel.enableLocalOnlyMode()
                    onLocalModeSelected()
                },
                modifier = Modifier.fillMaxWidth(),
                isLargeTouchTarget = true
            )
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QpkParentBg)
            .padding(24.dp)
    ) {
        BackButton(onClick = onBack)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Reset Password", style = QpkTypography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Enter your parent email to receive a password reset link.", style = QpkTypography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        if (sent) {
            QpkCard(backgroundColor = QpkMint) {
                Text(text = "A reset link has been sent to $email. Local learning progress remains completely safe.")
            }
        } else {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Parent Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            QpkPrimaryButton(
                text = "Send Reset Link",
                onClick = { sent = true },
                modifier = Modifier.fillMaxWidth(),
                isLargeTouchTarget = true
            )
        }
    }
}

@Composable
fun AddChildScreen(
    viewModel: OnboardingViewModel,
    onChildAdded: (String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val stages = listOf(
        "3-4" to "Little Stars (3–4 th)",
        "5-6" to "Explorers (5–6 th)",
        "7-10" to "Young Hafiz (7–10 th)"
    )
    val avatars = listOf("camel", "bird", "sheep", "bee", "whale", "elephant")
    val avatarEmojis = mapOf(
        "camel" to "🐪",
        "bird" to "🕊️",
        "sheep" to "🐑",
        "bee" to "🐝",
        "whale" to "🐋",
        "elephant" to "🐘"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QpkSand)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton(onClick = onBack)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add Child Profile", style = QpkTypography.headlineLarge)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Nickname only — no full names or personal data are collected.",
                style = QpkTypography.bodyMedium,
                color = QpkMuted
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Nickname field
            OutlinedTextField(
                value = uiState.profileNickname,
                onValueChange = { if (it.length <= 15) viewModel.setProfileNickname(it) },
                label = { Text("Child Nickname") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Learning Stage", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            stages.forEach { (key, label) ->
                val selected = uiState.profileStage == key
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(if (selected) QpkMint else QpkWhite, QpkPillShape)
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) QpkTeal else QpkCardBorder,
                            shape = QpkPillShape
                        )
                        .clickable { viewModel.setProfileStage(key) }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) QpkTeal else QpkInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Choose Avatar", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                avatars.forEach { av ->
                    val isSelected = uiState.profileAvatar == av
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(if (isSelected) QpkMint else QpkWhite, CircleShape)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) QpkTeal else QpkCardBorder,
                                shape = CircleShape
                            )
                            .clickable { viewModel.setProfileAvatar(av) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatarEmojis[av] ?: "🌟", fontSize = 24.sp)
                    }
                }
            }
        }

        QpkPrimaryButton(
            text = "Save Profile",
            onClick = {
                viewModel.saveChildProfile { profileId ->
                    onChildAdded(profileId)
                }
            },
            enabled = uiState.profileNickname.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            isLargeTouchTarget = true
        )
    }
}

