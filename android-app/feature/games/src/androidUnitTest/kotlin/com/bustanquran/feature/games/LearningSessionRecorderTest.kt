package com.bustanquran.feature.games

import com.bustanquran.core.data.database.model.RewardEntity
import com.bustanquran.core.data.database.model.SessionLogEntity
import com.bustanquran.core.data.repository.ActiveProfile
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningSessionRecorderTest {

    private class FakeStore : SessionLogStore {
        val logs = mutableListOf<SessionLogEntity>()
        val rewards = mutableListOf<RewardEntity>()

        override suspend fun countSessions(profileId: String): Int =
            logs.count { it.profileId == profileId && !it.deleted }

        override suspend fun totalStars(profileId: String): Int =
            logs.filter { it.profileId == profileId && !it.deleted }.sumOf { it.stars }

        override suspend fun hasReward(profileId: String, type: String, refId: String): Boolean =
            rewards.any { it.profileId == profileId && it.type == type && it.refId == refId }

        override suspend fun insertSessionLog(log: SessionLogEntity) {
            logs += log
        }

        override suspend fun insertReward(reward: RewardEntity) {
            rewards += reward
        }
    }

    private val store = FakeStore()
    private val activeProfile = ActiveProfile()
    private val recorder = LearningSessionRecorder(store, activeProfile, now = { 1_000_000L })

    @Test
    fun `no selected child means nothing is written`() = runTest {
        val result = recorder.recordGameCompleted("game_ikan_harakat", correct = 6, total = 6)

        assertNull(result)
        assertTrue(store.logs.isEmpty())
        assertTrue(store.rewards.isEmpty())
    }

    @Test
    fun `finished round stores one session log with stars`() = runTest {
        activeProfile.select("child-1")

        val result = recorder.recordGameCompleted(
            gameRefId = "game_ikan_harakat",
            correct = 9,
            total = 10,
            durationSec = 30
        )

        assertEquals(3, result?.stars)
        assertEquals(1, store.logs.size)
        val log = store.logs.single()
        assertEquals("child-1", log.profileId)
        assertEquals("game", log.type)
        assertEquals("game_ikan_harakat", log.refId)
        assertEquals(9, log.correct)
        assertEquals(10, log.total)
        assertEquals(3, log.stars)
        assertEquals(30, log.durationSec)
        assertEquals(1_000_000L, log.updatedAt)
        assertEquals(1_000_000L - 30_000L, log.startedAt)
    }

    @Test
    fun `first finished round grants the first step badge once`() = runTest {
        activeProfile.select("child-1")

        val first = recorder.recordGameCompleted("game_ikan_harakat", correct = 4, total = 6)
        val second = recorder.recordGameCompleted("game_ikan_harakat", correct = 4, total = 6)

        assertTrue(first?.newBadgeIds?.contains("first_step") == true)
        assertTrue(second?.newBadgeIds?.isEmpty() == true)
        assertEquals(1, store.rewards.count { it.refId == "first_step" })
    }

    @Test
    fun `fifth finished round unlocks an animal card`() = runTest {
        activeProfile.select("child-1")

        repeat(5) {
            recorder.recordGameCompleted("game_ikan_harakat", correct = 6, total = 6)
        }

        assertEquals(5, store.logs.size)
        assertTrue(store.rewards.any { it.type == "card" })
    }
}
