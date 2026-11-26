package com.bustanquran.feature.games

import com.bustanquran.core.data.database.model.RewardEntity
import com.bustanquran.core.data.database.model.SessionLogEntity
import com.bustanquran.core.data.repository.ActiveProfile
import com.bustanquran.core.data.repository.UserRepository
import java.util.UUID

/** What the UI needs after a finished round. */
data class RecordedSession(
    val stars: Int,
    val unlockedAnimalCard: String?,
    val newBadgeIds: List<String>
)

/** The narrow persistence surface the recorder needs; keeps it unit-testable. */
interface SessionLogStore {
    suspend fun countSessions(profileId: String): Int
    suspend fun totalStars(profileId: String): Int
    suspend fun hasReward(profileId: String, type: String, refId: String): Boolean
    suspend fun insertSessionLog(log: SessionLogEntity)
    suspend fun insertReward(reward: RewardEntity)
}

class UserRepositorySessionLogStore(
    private val repository: UserRepository
) : SessionLogStore {
    override suspend fun countSessions(profileId: String): Int = repository.countSessions(profileId)
    override suspend fun totalStars(profileId: String): Int = repository.totalStars(profileId)
    override suspend fun hasReward(profileId: String, type: String, refId: String): Boolean =
        repository.hasReward(profileId, type, refId)
    override suspend fun insertSessionLog(log: SessionLogEntity) = repository.insertSessionLog(log)
    override suspend fun insertReward(reward: RewardEntity) = repository.insertReward(reward)
}

/**
 * Turns a finished round into persisted progress: one `session_log` row, the
 * stars for that round, the badge rules (RWD-03) and the animal card that every
 * fifth session unlocks (RWD-02).
 *
 * Returns `null` when no child has been selected — nothing is written in that
 * case, so a fresh install can never accumulate orphan progress.
 */
class LearningSessionRecorder(
    private val store: SessionLogStore,
    private val activeProfile: ActiveProfile,
    private val now: () -> Long = System::currentTimeMillis
) {

    suspend fun recordGameCompleted(
        gameRefId: String,
        correct: Int,
        total: Int,
        durationSec: Int = 0
    ): RecordedSession? {
        val profileId = activeProfile.current() ?: return null
        val timestamp = now()

        val grant = RewardEngine.evaluateSessionCompletion(
            correct = correct,
            total = total,
            previousCompletedSessions = store.countSessions(profileId),
            previousTotalStars = store.totalStars(profileId),
            alreadyOwnedBadges = ownedBadgeIds(profileId)
        )

        store.insertSessionLog(
            SessionLogEntity(
                id = UUID.randomUUID().toString(),
                profileId = profileId,
                type = "game",
                refId = gameRefId,
                startedAt = timestamp - durationSec * 1_000L,
                durationSec = durationSec,
                correct = correct,
                total = total,
                stars = grant.starsEarned,
                updatedAt = timestamp
            )
        )

        grant.newlyUnlockedBadges.forEach { badge ->
            store.insertReward(
                RewardEntity(profileId = profileId, type = "badge", refId = badge.id, earnedAt = timestamp, updatedAt = timestamp)
            )
        }
        grant.unlockedAnimalCard?.let { card ->
            store.insertReward(
                RewardEntity(profileId = profileId, type = "card", refId = card, earnedAt = timestamp, updatedAt = timestamp)
            )
        }

        return RecordedSession(
            stars = grant.starsEarned,
            unlockedAnimalCard = grant.unlockedAnimalCard,
            newBadgeIds = grant.newlyUnlockedBadges.map { it.id }
        )
    }

    private suspend fun ownedBadgeIds(profileId: String): Set<String> =
        RewardEngine.BADGES
            .filter { badge -> store.hasReward(profileId, "badge", badge.id) }
            .map { badge -> badge.id }
            .toSet()
}
