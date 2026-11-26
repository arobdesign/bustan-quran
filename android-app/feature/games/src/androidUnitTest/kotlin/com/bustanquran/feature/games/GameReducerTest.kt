package com.bustanquran.feature.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameReducerTest {

    private fun sampleState() = GameUiState(
        gameId = "balon_huruf",
        gameTitle = "Balon Huruf",
        questions = listOf(
            GameQuestion("1", "Q1", "1", "A", listOf("A", "B"), "Hint 1"),
            GameQuestion("2", "Q2", "2", "B", listOf("A", "B"), "Hint 2")
        )
    )

    @Test
    fun `wrong answer shows hint and never ends game`() {
        val next = reduceGameEvent(sampleState(), GameEvent.AnswerSelected("B"))
        assertFalse(next.isFinished)
        assertEquals(false, next.lastAnswerCorrect)
        assertTrue(next.showHint)
        assertEquals(0, next.currentIndex)
    }

    @Test
    fun `correct answers advance and finish game`() {
        val s1 = reduceGameEvent(sampleState(), GameEvent.AnswerSelected("A"))
        assertEquals(1, s1.currentIndex)
        assertEquals(1, s1.correct)
        assertFalse(s1.isFinished)

        val s2 = reduceGameEvent(s1, GameEvent.AnswerSelected("B"))
        assertTrue(s2.isFinished)
        assertEquals(2, s2.correct)
    }
}
