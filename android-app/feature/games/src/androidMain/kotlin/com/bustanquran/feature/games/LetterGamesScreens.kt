package com.bustanquran.feature.games

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.ui.theme.QpkInk
import com.bustanquran.core.ui.theme.QpkTypography

/**
 * Letter/vocabulary mini-games.
 *
 * These five games used to be structural placeholders that only printed a
 * description. They now run real rounds on the shared framework in
 * [GameEngine]/[GameHost]: the questions come from the static hijaiyah and
 * vocabulary tables below (no Qur’an text is used as a tappable object), and
 * every finished round reports correct/total/duration so the caller can persist
 * a session log and stars via `rememberRoundFinisher`.
 */

internal data class HijaiyahLetter(val glyph: String, val name: String)

internal val HIJAIYAH_LETTERS = listOf(
    HijaiyahLetter("ا", "Alif"),
    HijaiyahLetter("ب", "Ba"),
    HijaiyahLetter("ت", "Ta"),
    HijaiyahLetter("ث", "Tsa"),
    HijaiyahLetter("ج", "Jim"),
    HijaiyahLetter("ح", "Ha"),
    HijaiyahLetter("خ", "Kha"),
    HijaiyahLetter("د", "Dal"),
    HijaiyahLetter("ذ", "Dzal"),
    HijaiyahLetter("ر", "Ra"),
    HijaiyahLetter("ز", "Zai"),
    HijaiyahLetter("س", "Sin"),
    HijaiyahLetter("ش", "Syin"),
    HijaiyahLetter("ص", "Shad"),
    HijaiyahLetter("ض", "Dhad"),
    HijaiyahLetter("ط", "Tha"),
    HijaiyahLetter("ظ", "Zha"),
    HijaiyahLetter("ع", "‘Ain"),
    HijaiyahLetter("غ", "Ghain"),
    HijaiyahLetter("ف", "Fa"),
    HijaiyahLetter("ق", "Qaf"),
    HijaiyahLetter("ك", "Kaf"),
    HijaiyahLetter("ل", "Lam"),
    HijaiyahLetter("م", "Mim"),
    HijaiyahLetter("ن", "Nun"),
    HijaiyahLetter("و", "Wau"),
    HijaiyahLetter("ه", "Ha’"),
    HijaiyahLetter("ي", "Ya"),
)

/** Arabic noun → Indonesian meaning. Nouns only: safe to show without tafsir. */
internal data class ArabicWord(val arabic: String, val meaning: String)

internal val ARABIC_WORDS = listOf(
    ArabicWord("كِتَاب", "Buku"),
    ArabicWord("قَلَم", "Pena"),
    ArabicWord("بَيْت", "Rumah"),
    ArabicWord("مَاء", "Air"),
    ArabicWord("شَمْس", "Matahari"),
    ArabicWord("قَمَر", "Bulan"),
    ArabicWord("بَاب", "Pintu"),
    ArabicWord("يَد", "Tangan"),
    ArabicWord("نَار", "Api"),
    ArabicWord("سَمَاء", "Langit"),
)

/** Six questions is a short, finishable round for a 4–8 year old. */
internal const val LETTER_ROUND_SIZE = 6

/**
 * Builds the round for a letter game.
 *
 * @param askForGlyph when true the prompt is the letter *name* and the options
 *   are glyphs; otherwise the glyph is shown and the options are letter names.
 * @param hint shown after a wrong answer (never game-over).
 */
internal fun letterRound(
    gameId: String,
    title: String,
    prompt: String,
    hint: String,
    askForGlyph: Boolean,
    letters: List<HijaiyahLetter> = HIJAIYAH_LETTERS,
    profileId: String = ""
): GameUiState {
    val questions = letters.take(LETTER_ROUND_SIZE).mapIndexed { index, letter ->
        val others = letters.filter { it.name != letter.name }
        val distractors = (0 until 3).map { offset ->
            val other = others[(index + offset) % others.size]
            if (askForGlyph) other.glyph else other.name
        }
        GameQuestion(
            id = "${gameId}_$index",
            prompt = prompt,
            promptArabic = if (askForGlyph) letter.name else letter.glyph,
            correctAnswer = if (askForGlyph) letter.glyph else letter.name,
            distractors = distractors,
            hintText = hint
        )
    }
    return GameUiState(
        gameId = gameId,
        gameTitle = title,
        profileId = profileId,
        questions = questions,
        startTimeMs = System.currentTimeMillis()
    )
}

internal fun kebunHurufState(profileId: String = "") = letterRound(
    gameId = "game_kebun_huruf",
    title = "Kebun Huruf",
    prompt = "Huruf apa yang disiram?",
    hint = "Baca bentuk hurufnya dari kanan ke kiri.",
    askForGlyph = false,
    profileId = profileId
)

internal fun bayanganHurufState(profileId: String = "") = letterRound(
    gameId = "game_bayangan",
    title = "Bayangan Huruf",
    prompt = "Huruf mana yang bentuknya seperti ini?",
    hint = "Cocokkan nama huruf dengan bentuknya.",
    askForGlyph = true,
    letters = HIJAIYAH_LETTERS.drop(4),
    profileId = profileId
)

internal fun lariHijaiyahState(profileId: String = "") = letterRound(
    gameId = "game_lari_hijaiyah",
    title = "Lari Hijaiyah",
    prompt = "Lompat ke huruf ini!",
    hint = "Lihat titik dan lengkung hurufnya dengan teliti.",
    askForGlyph = true,
    letters = HIJAIYAH_LETTERS.drop(10),
    profileId = profileId
)

/** Word → meaning (Memori Kata) and meaning → word (Pasar Huruf). */
internal fun wordRound(
    gameId: String,
    title: String,
    prompt: String,
    hint: String,
    askForArabic: Boolean,
    words: List<ArabicWord> = ARABIC_WORDS,
    profileId: String = ""
): GameUiState {
    val questions = words.mapIndexed { index, word ->
        val others = words.filter { it.meaning != word.meaning }
        val distractors = (0 until 3).map { offset ->
            val other = others[(index + offset) % others.size]
            if (askForArabic) other.arabic else other.meaning
        }
        GameQuestion(
            id = "${gameId}_$index",
            prompt = prompt,
            promptArabic = if (askForArabic) word.meaning else word.arabic,
            correctAnswer = if (askForArabic) word.arabic else word.meaning,
            distractors = distractors,
            hintText = hint
        )
    }
    return GameUiState(
        gameId = gameId,
        gameTitle = title,
        profileId = profileId,
        questions = questions,
        startTimeMs = System.currentTimeMillis()
    )
}

internal fun memoriKataState(profileId: String = "") = wordRound(
    gameId = "game_memori_kata",
    title = "Memori Kata",
    prompt = "Apa arti kata ini?",
    hint = "Ingat benda di sekitarmu yang mirip kata ini.",
    askForArabic = false,
    profileId = profileId
)

internal fun pasarHurufState(profileId: String = "") = wordRound(
    gameId = "game_pasar_huruf",
    title = "Pasar Huruf",
    prompt = "Barang apa ini dalam bahasa Arab?",
    hint = "Baca kata Arabnya, lalu pilih yang sesuai.",
    askForArabic = true,
    profileId = profileId
)

/**
 * One screen for every plain "read the prompt, tap the answer" round.
 * The caller only supplies the initial [stateFactory] and gets the real
 * correct/total/duration back, exactly like [IkanHarakatScreen].
 */
@Composable
internal fun QuizRoundScreen(
    stateFactory: () -> GameUiState,
    onRoundFinished: (correct: Int, total: Int, durationSec: Int) -> Unit,
    onBackClicked: () -> Unit
) {
    var state by remember { mutableStateOf(stateFactory()) }
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
                    question.prompt,
                    style = QpkTypography.bodyLarge,
                    color = QpkInk,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    question.promptArabic,
                    // Single letters/words read better large; full words shrink a bit.
                    fontSize = if (question.promptArabic.length <= 2) 88.sp else 44.sp,
                    color = QpkInk,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
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

@Composable
fun MemoriKataScreen(
    onRoundFinished: (correct: Int, total: Int, durationSec: Int) -> Unit = { _, _, _ -> },
    onBackClicked: () -> Unit = {}
) = QuizRoundScreen({ memoriKataState() }, onRoundFinished, onBackClicked)

@Composable
fun GKebunHurufScreen(
    onRoundFinished: (correct: Int, total: Int, durationSec: Int) -> Unit = { _, _, _ -> },
    onBackClicked: () -> Unit = {}
) = QuizRoundScreen({ kebunHurufState() }, onRoundFinished, onBackClicked)

@Composable
fun GBayanganScreen(
    onRoundFinished: (correct: Int, total: Int, durationSec: Int) -> Unit = { _, _, _ -> },
    onBackClicked: () -> Unit = {}
) = QuizRoundScreen({ bayanganHurufState() }, onRoundFinished, onBackClicked)

@Composable
fun GLariHijaiyahScreen(
    onRoundFinished: (correct: Int, total: Int, durationSec: Int) -> Unit = { _, _, _ -> },
    onBackClicked: () -> Unit = {}
) = QuizRoundScreen({ lariHijaiyahState() }, onRoundFinished, onBackClicked)

@Composable
fun GPasarHurufScreen(
    onRoundFinished: (correct: Int, total: Int, durationSec: Int) -> Unit = { _, _, _ -> },
    onBackClicked: () -> Unit = {}
) = QuizRoundScreen({ pasarHurufState() }, onRoundFinished, onBackClicked)
