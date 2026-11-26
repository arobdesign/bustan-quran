package com.bustanquran.feature.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.theme.*

@Composable
private fun GameScaffold(title: String, emoji: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(QpkSand)) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = QpkInk) }
            Text("$emoji  $title", style = QpkTypography.titleLarge, fontWeight = FontWeight.Bold, color = QpkInk)
        }
        content()
    }
}

@Composable
private fun GamePlaceholderBody(description: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(description, style = QpkTypography.bodyLarge, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Text("Konten dari game_content (content.db)", style = QpkTypography.bodySmall, color = Color(0xFFAAAAAA), textAlign = TextAlign.Center)
        }
    }
}

fun reduceGameEvent(state: GameUiState, event: GameEvent): GameUiState {
    return when (event) {
        is GameEvent.AnswerSelected -> {
            val correct = event.answer == state.currentQuestion?.correctAnswer
            if (correct) {
                val next = state.currentIndex + 1
                val newCorrect = state.correct + 1
                if (next >= state.questions.size) {
                    state.copy(correct = newCorrect, selectedAnswer = event.answer, lastAnswerCorrect = true, isFinished = true)
                } else {
                    state.copy(correct = newCorrect, currentIndex = next, selectedAnswer = null, lastAnswerCorrect = null, showHint = false)
                }
            } else {
                // Never game-over: show hint + retry
                state.copy(selectedAnswer = event.answer, lastAnswerCorrect = false, showHint = true)
            }
        }
        GameEvent.NextQuestion -> {
            val next = state.currentIndex + 1
            if (next >= state.questions.size) state.copy(isFinished = true)
            else state.copy(currentIndex = next, selectedAnswer = null, lastAnswerCorrect = null, showHint = false)
        }
        GameEvent.RequestHint -> state.copy(showHint = true)
        GameEvent.Restart -> state.copy(currentIndex = 0, correct = 0, isFinished = false, selectedAnswer = null, lastAnswerCorrect = null, showHint = false)
    }
}

// BalonHuruf lives in `BalonHurufScreen.kt` (artboard 03, P-18 STEP 2A).
// SusunAyat lives in `SusunAyatScreen.kt` (artboard 25) and SambungAyat in
// `SambungAyatScreen.kt` (artboard 26); both draw from `content.db`.

// ─── Ikan Harakat (artboard 20) ──────────────────────────────────────────────
// Real, playable round built on the shared game framework: the child names the
// harakat shown on the letter. Finishing reports the score so the caller can
// persist a session log + stars.
private val HARAKAT_LETTERS = listOf("ب", "ت", "ن", "م", "ل", "ر")

private val HARAKAT_SIGNS = listOf(
    "\u064E" to "Fathah",
    "\u0650" to "Kasrah",
    "\u064F" to "Dhammah",
    "\u0652" to "Sukun"
)

internal fun ikanHarakatState(profileId: String = ""): GameUiState {
    val questions = HARAKAT_LETTERS.mapIndexed { index, letter ->
        val (sign, name) = HARAKAT_SIGNS[index % HARAKAT_SIGNS.size]
        GameQuestion(
            id = "ikan_$index",
            prompt = "Harakat apa ini?",
            promptArabic = "$letter$sign",
            correctAnswer = name,
            distractors = HARAKAT_SIGNS.filter { it.second != name }.map { it.second },
            hintText = "Perhatikan tanda di atas atau di bawah huruf."
        )
    }
    return GameUiState(
        gameId = "ikan_harakat",
        gameTitle = "Ikan Harakat",
        profileId = profileId,
        questions = questions,
        startTimeMs = System.currentTimeMillis()
    )
}

@Composable
fun IkanHarakatScreen(
    onRoundFinished: (correct: Int, total: Int, durationSec: Int) -> Unit = { _, _, _ -> },
    onBackClicked: () -> Unit = {}
) {
    var state by remember { mutableStateOf(ikanHarakatState()) }
    // Wall-clock of the round, so the saved session carries a real duration.
    var startedAtMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            val elapsedSec = ((System.currentTimeMillis() - startedAtMs) / 1_000L).toInt()
            onRoundFinished(state.correct, state.total, elapsedSec)
        } else {
            // Covers the first round and every "Main Lagi" restart.
            startedAtMs = System.currentTimeMillis()
        }
    }

    GameHost(
        state = state,
        onEvent = { event -> state = reduceGameEvent(state, event) },
        onBackClicked = onBackClicked
    ) {
        val question = state.currentQuestion
        if (question != null) {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    question.promptArabic,
                    fontSize = 88.sp,
                    color = QpkInk,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(24.dp))
                AnswerGrid(
                    options = listOf(question.correctAnswer) + question.distractors,
                    selectedAnswer = state.selectedAnswer,
                    correctAnswer = question.correctAnswer,
                    lastAnswerCorrect = state.lastAnswerCorrect,
                    onSelect = { answer -> state = reduceGameEvent(state, GameEvent.AnswerSelected(answer)) }
                )
            }
        }
    }
}

// Memori Kata, Kebun Huruf, Bayangan Huruf, Lari Hijaiyah and Pasar Huruf are
// real rounds in `LetterGamesScreens.kt` (shared `QuizRoundScreen`).

@Composable
fun GKeretaSurahScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Kereta Surah", "🚂", onFinished) {
        GamePlaceholderBody("Urutan nama surah Juz Amma naik kereta.\nNama surah dari content.db.")
    }
}

@Composable
fun GBintangScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Bintang Hafalan", "⭐", onFinished) {
        GamePlaceholderBody("Kumpulkan bintang dengan menjawab pertanyaan hafalan.")
    }
}

@Composable
fun GSambungHurufScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Sambung Huruf", "✏️", onFinished) {
        GamePlaceholderBody("Sambungkan huruf terpisah menjadi kata.")
    }
}

@Composable
fun GAyatHilangScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Ayat Hilang", "❓", onFinished) {
        GamePlaceholderBody("Isi kata yang hilang dari ayat.\nSemua teks dari content.db.")
    }
}

@Composable
fun GUrutkanAyatScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Urutkan Ayat", "🔢", onFinished) {
        GamePlaceholderBody("Urutkan ayat-ayat surah dari awal hingga akhir.")
    }
}

@Composable
fun GTebakSurahScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Tebak Surah", "🤔", onFinished) {
        GamePlaceholderBody("Tebak nama surah dari penggalan ayat yang diputar.")
    }
}

@Composable
fun GSusunArtiScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Susun Arti", "📚", onFinished) {
        GamePlaceholderBody("Susun terjemahan ayat kata per kata.")
    }
}

@Composable
fun GKuisKisahScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Kuis Kisah", "📖", onFinished) {
        GamePlaceholderBody("Jawab pertanyaan tentang kisah nabi dari konten.")
    }
}

@Composable
fun GKeluargaScreen(onFinished: () -> Unit = {}) {
    GameScaffold("Keluarga Huruf", "👨‍👩‍👧", onFinished) {
        GamePlaceholderBody("Pasangkan huruf dengan keluarga bentuknya (awal/tengah/akhir/tunggal).")
    }
}

// Reward lives in `RewardScreen.kt` (artboard 06, P-18 STEP 2A).

@Composable
fun KoleksiKartuScreen(onBackClicked: () -> Unit = {}) {
    GameScaffold("Koleksi Kartu", "🃏", onBackClicked) {
        GamePlaceholderBody("Koleksi kartu hewan dari sesi yang diselesaikan.\nSatu kartu setiap 5 sesi selesai (RWD-01..05).")
    }
}

@Composable
fun CertificateScreen(onBackClicked: () -> Unit = {}) {
    GameScaffold("Sertifikat Khatam", "📜", onBackClicked) {
        GamePlaceholderBody("Sertifikat digital setelah khatam Juz Amma (v1.1).")
    }
}
