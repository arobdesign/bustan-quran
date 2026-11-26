
package com.bustanquran.feature.games

/**
 * P-09 — Reusable game framework (GAM-01..05)
 * Rules:
 *  - Never game-over: wrong answer → hint + retry
 *  - Qur'an text never used as shootable/falling object
 *  - Content sourced from game_content table (content.db)
 *  - Two levels: Level 1 (hints always shown), Level 2 (hints hidden until 1st wrong)
 */

data class GameResult(
    val gameId: String,
    val profileId: String,
    val correct: Int,
    val total: Int,
    val durationSec: Int
) {
    val accuracyPct: Int get() = if (total == 0) 0 else (correct * 100) / total
    val starCount: Int get() = when {
        accuracyPct >= 90 -> 3
        accuracyPct >= 60 -> 2
        else -> 1
    }
}

data class GameQuestion(
    val id: String,
    val prompt: String,         // could be letter name, image label, etc.
    val promptArabic: String,   // from content.db — displayed as-is
    val correctAnswer: String,
    val distractors: List<String>,
    val hintText: String = ""
)

sealed class GameEvent {
    data class AnswerSelected(val answer: String) : GameEvent()
    object NextQuestion : GameEvent()
    object RequestHint : GameEvent()
    object Restart : GameEvent()
}

data class GameUiState(
    val gameId: String = "",
    val gameTitle: String = "",
    val profileId: String = "",
    val questions: List<GameQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val correct: Int = 0,
    val showHint: Boolean = false,
    val lastAnswerCorrect: Boolean? = null,
    val selectedAnswer: String? = null,
    val isFinished: Boolean = false,
    val level: Int = 1,         // 1 = hints always, 2 = hints after wrong
    val startTimeMs: Long = 0L
) {
    val currentQuestion: GameQuestion? get() = questions.getOrNull(currentIndex)
    val round: Int get() = currentIndex + 1
    val total: Int get() = questions.size
    val progress: Float get() = if (total == 0) 0f else currentIndex.toFloat() / total
}
