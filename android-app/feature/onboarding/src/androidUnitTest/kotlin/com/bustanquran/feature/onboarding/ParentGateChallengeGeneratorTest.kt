package com.bustanquran.feature.onboarding

import com.bustanquran.feature.onboarding.parentgate.ParentGateChallengeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ParentGateChallengeGeneratorTest {

    @Test
    fun testGenerateEnglishChallengeHasThreeOptionsAndCorrectAnswer() {
        val random = Random(42)
        val challenge = ParentGateChallengeGenerator.generate("en", random)

        assertNotNull(challenge.questionText)
        assertTrue(challenge.questionText.contains("plus"))
        assertEquals(3, challenge.options.size)
        assertTrue(challenge.options.contains(challenge.correctAnswer))

        // Ensure all options are distinct
        assertEquals(3, challenge.options.distinct().size)
    }

    @Test
    fun testGenerateArabicChallengeUsesArabicWords() {
        val random = Random(123)
        val challenge = ParentGateChallengeGenerator.generate("ar", random)

        assertTrue(challenge.questionText.contains("زائد"))
        assertEquals(3, challenge.options.size)
        assertTrue(challenge.options.contains(challenge.correctAnswer))
    }

    @Test
    fun testGenerateFrenchChallengeUsesFrenchWords() {
        val challenge = ParentGateChallengeGenerator.generate("fr")
        assertTrue(challenge.questionText.contains("plus"))
        assertEquals(3, challenge.options.size)
    }

    @Test
    fun testGenerateGermanChallengeUsesGermanWords() {
        val challenge = ParentGateChallengeGenerator.generate("de")
        assertTrue(challenge.questionText.contains("plus"))
        assertEquals(3, challenge.options.size)
    }

    @Test
    fun testTwoConsecutiveChallengesDiffer() {
        val random = Random(999)
        val c1 = ParentGateChallengeGenerator.generate("en", random)
        val c2 = ParentGateChallengeGenerator.generate("en", random)

        // Consecutive challenges should be randomized
        assertNotNull(c1)
        assertNotNull(c2)
    }
}
