package com.bustanquran.feature.games

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The five letter/vocabulary games used to be placeholders. These tests pin the
 * properties that make a round playable: every question has one correct answer,
 * three *distinct* wrong options, and a round the child can actually finish.
 */
class LetterGamesTest {

    private val factories: List<Pair<String, () -> GameUiState>> = listOf(
        "kebun_huruf" to { kebunHurufState() },
        "bayangan" to { bayanganHurufState() },
        "lari_hijaiyah" to { lariHijaiyahState() },
        "memori_kata" to { memoriKataState() },
        "pasar_huruf" to { pasarHurufState() }
    )

    @Test
    fun everyRoundIsPlayable() {
        factories.forEach { (name, factory) ->
            val state = factory()
            assertTrue(state.total >= LETTER_ROUND_SIZE, "$name: round too short")
            assertEquals(0, state.correct, "$name: starts at zero")
            state.questions.forEach { question ->
                val options = listOf(question.correctAnswer) + question.distractors
                assertEquals(
                    4, options.toSet().size,
                    "$name/${question.id}: options must be distinct, got $options"
                )
                assertTrue(question.correctAnswer.isNotBlank(), "$name/${question.id}: empty answer")
                assertTrue(question.promptArabic.isNotBlank(), "$name/${question.id}: empty prompt")
            }
        }
    }

    @Test
    fun answeringEverythingCorrectlyFinishesTheRound() {
        factories.forEach { (name, factory) ->
            var state = factory()
            while (!state.isFinished) {
                val answer = state.currentQuestion!!.correctAnswer
                state = reduceGameEvent(state, GameEvent.AnswerSelected(answer))
            }
            assertEquals(state.total, state.correct, "$name: perfect round keeps every point")
        }
    }

    @Test
    fun wrongAnswerNeverEndsTheRound() {
        var state = kebunHurufState()
        val question = state.currentQuestion!!
        state = reduceGameEvent(state, GameEvent.AnswerSelected(question.distractors.first()))
        assertTrue(!state.isFinished, "a wrong answer must not end the round")
        assertTrue(state.showHint, "a wrong answer shows the hint")
        assertEquals(0, state.correct, "a wrong answer scores nothing")
    }

    @Test
    fun letterNamesAndGlyphsAreUnique() {
        assertEquals(
            HIJAIYAH_LETTERS.size, HIJAIYAH_LETTERS.map { it.name }.toSet().size,
            "duplicate letter names would make an option ambiguous"
        )
        assertEquals(
            HIJAIYAH_LETTERS.size, HIJAIYAH_LETTERS.map { it.glyph }.toSet().size,
            "duplicate glyphs would make an option ambiguous"
        )
    }

    @Test
    fun wordRoundCanBeAskedInBothDirections() {
        val word = ARABIC_WORDS.first()
        val meaningAsked = memoriKataState().questions.first()
        assertEquals(word.arabic, meaningAsked.promptArabic)
        assertEquals(word.meaning, meaningAsked.correctAnswer)

        val arabicAsked = pasarHurufState().questions.first()
        assertEquals(word.meaning, arabicAsked.promptArabic)
        assertEquals(word.arabic, arabicAsked.correctAnswer)
    }
}
