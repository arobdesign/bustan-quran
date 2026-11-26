
package com.bustanquran.feature.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bustanquran.core.ui.theme.*

/**
 * GameHost — reusable scaffold for all mini-games.
 * Provides: top bar, round counter, voice instruction, hint button,
 * and result handling. Never shows game-over — wrong → hint + retry.
 */
@Composable
fun GameHost(
    state: GameUiState,
    onEvent: (GameEvent) -> Unit,
    onVoiceInstruction: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    gameContent: @Composable () -> Unit
) {
    if (state.isFinished) {
        GameResultScreen(
            correct = state.correct,
            total = state.total,
            onContinue = onBackClicked,
            onRestart = { onEvent(GameEvent.Restart) }
        )
        return
    }

    Column(Modifier.fillMaxSize().background(QpkSand)) {
        // Top bar
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = QpkInk) }
            Spacer(Modifier.weight(1f))
            Text(state.gameTitle, style = QpkTypography.titleMedium, fontWeight = FontWeight.Bold, color = QpkInk)
            Spacer(Modifier.weight(1f))
            Text("${state.round}/${state.total}", style = QpkTypography.labelLarge, color = QpkTeal, fontWeight = FontWeight.Bold)
        }

        // Progress bar
        LinearProgressIndicator(
            progress = { state.progress },
            Modifier.fillMaxWidth().height(6.dp),
            color = QpkTeal, trackColor = QpkSand
        )

        // Voice instruction bar
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            state.currentQuestion?.prompt?.let {
                Text(it, style = QpkTypography.bodyMedium, color = QpkInk, modifier = Modifier.weight(1f))
            }
            IconButton(onClick = onVoiceInstruction) {
                Icon(Icons.Default.PlayArrow, "Listen instruction", tint = QpkTeal)
            }
            if (state.level == 1 || (state.level == 2 && state.lastAnswerCorrect == false)) {
                IconButton(onClick = { onEvent(GameEvent.RequestHint) }) {
                    Icon(Icons.Default.Info, "Hint", tint = QpkSaffron)
                }
            }
        }

        // Hint card
        if (state.showHint && state.currentQuestion?.hintText?.isNotBlank() == true) {
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = QpkSaffron.copy(0.15f))
            ) {
                Text(
                    text = "💡 ${state.currentQuestion?.hintText}",
                    modifier = Modifier.padding(12.dp),
                    style = QpkTypography.bodySmall, color = QpkInk
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // Wrong answer feedback
        if (state.lastAnswerCorrect == false) {
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = QpkCoral.copy(0.15f))
            ) {
                Text("Hampir! Coba lagi 🌟", Modifier.padding(12.dp), style = QpkTypography.bodyMedium,
                    color = QpkCoral, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
        }

        // Game-specific content
        Box(Modifier.weight(1f)) { gameContent() }
    }
}

// ─── Generic multiple-choice answer grid ─────────────────────────────────────
@Composable
fun AnswerGrid(
    options: List<String>,
    selectedAnswer: String?,
    correctAnswer: String,
    lastAnswerCorrect: Boolean?,
    onSelect: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { option ->
                    val isSelected = option == selectedAnswer
                    val isCorrect = option == correctAnswer
                    val bgColor = when {
                        isSelected && lastAnswerCorrect == true && isCorrect -> QpkMint
                        isSelected && lastAnswerCorrect == false && !isCorrect -> QpkCoral.copy(0.3f)
                        else -> Color.White
                    }
                    Button(
                        onClick = { if (lastAnswerCorrect != true) onSelect(option) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = bgColor, contentColor = QpkInk),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text(option, style = QpkTypography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ─── GameResult screen ────────────────────────────────────────────────────────
@Composable
fun GameResultScreen(correct: Int, total: Int, onContinue: () -> Unit, onRestart: () -> Unit) {
    val pct = if (total == 0) 0 else (correct * 100) / total
    // Same thresholds the persisted reward uses, so the summary can never
    // disagree with what the child actually earned.
    val stars = RewardEngine.calculateStars(correct, total)
    Box(Modifier.fillMaxSize().background(QpkSand), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("⭐".repeat(stars), style = QpkTypography.displayMedium)
            Spacer(Modifier.height(16.dp))
            Text("$correct / $total Benar", style = QpkTypography.headlineLarge, fontWeight = FontWeight.Bold, color = QpkInk)
            Text("$pct%", style = QpkTypography.displaySmall, color = QpkTeal, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))
            Button(onClick = onContinue, Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = QpkTeal)) { Text("Lanjut") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onRestart, Modifier.fillMaxWidth()) { Text("Main Lagi") }
        }
    }
}
