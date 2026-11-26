package com.bustanquran.feature.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardEngineTest {

    @Test
    fun `calculateStars follows 90 and 60 percent thresholds`() {
        assertEquals(3, RewardEngine.calculateStars(9, 10))
        assertEquals(3, RewardEngine.calculateStars(10, 10))
        assertEquals(2, RewardEngine.calculateStars(6, 10))
        assertEquals(2, RewardEngine.calculateStars(8, 10))
        assertEquals(1, RewardEngine.calculateStars(5, 10))
        assertEquals(1, RewardEngine.calculateStars(0, 10))
    }

    @Test
    fun `animal card unlocked every 5 sessions`() {
        val r4 = RewardEngine.evaluateSessionCompletion(10, 10, 3, 9, emptySet())
        assertNull(r4.unlockedAnimalCard)

        val r5 = RewardEngine.evaluateSessionCompletion(10, 10, 4, 12, emptySet())
        assertNotNull(r5.unlockedAnimalCard)
    }

    @Test
    fun `first badge unlocked on first session`() {
        val r = RewardEngine.evaluateSessionCompletion(8, 10, 0, 0, emptySet())
        assertTrue(r.newlyUnlockedBadges.any { it.id == "first_step" })
    }

    @Test
    fun `already owned badges are not duplicated`() {
        val r = RewardEngine.evaluateSessionCompletion(10, 10, 0, 0, setOf("first_step"))
        assertTrue(r.newlyUnlockedBadges.none { it.id == "first_step" })
    }
}
