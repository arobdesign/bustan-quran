package com.bustanquran.feature.games

/**
 * RWD-01..05 — Pure Kotlin reward calculation.
 * Rules:
 * - Stars: >=90% = 3, >=60% = 2, completed = 1
 * - New animal card every 5 completed sessions
 * - 8 badges with visible criteria
 * - No purchasable currency
 * - Broken streaks never remove earned stars, cards, or badges
 */
data class BadgeDefinition(
    val id: String,
    val title: String,
    val criteria: String,
    val requiredSessions: Int = 0,
    val requiredStars: Int = 0
)

data class RewardGrant(
    val starsEarned: Int,
    val unlockedAnimalCard: String?,
    val newlyUnlockedBadges: List<BadgeDefinition>
)

object RewardEngine {

    val ANIMAL_CARDS = listOf(
        "Unta (Camel)", "Gajah (Elephant)", "Lebah (Bee)", "Semut (Ant)",
        "Burung Hudhud (Hoopoe)", "Ikan Paus (Whale)", "Domba (Sheep)", "Merpati (Dove)"
    )

    val BADGES = listOf(
        BadgeDefinition("first_step", "Langkah Pertama", "Selesaikan 1 sesi belajar", requiredSessions = 1),
        BadgeDefinition("five_sessions", "Rajin Belajar", "Selesaikan 5 sesi belajar", requiredSessions = 5),
        BadgeDefinition("ten_sessions", "Sahabat Qur'an", "Selesaikan 10 sesi belajar", requiredSessions = 10),
        BadgeDefinition("twenty_sessions", "Pejuang Juz 'Amma", "Selesaikan 20 sesi belajar", requiredSessions = 20),
        BadgeDefinition("star_10", "Kolektor 10 Bintang", "Kumpulkan 10 bintang", requiredStars = 10),
        BadgeDefinition("star_30", "Bintang Cemerlang", "Kumpulkan 30 bintang", requiredStars = 30),
        BadgeDefinition("star_50", "Cahaya Bintang", "Kumpulkan 50 bintang", requiredStars = 50),
        BadgeDefinition("star_100", "Master Bintang", "Kumpulkan 100 bintang", requiredStars = 100)
    )

    fun calculateStars(correct: Int, total: Int): Int {
        if (total <= 0) return 1
        val pct = (correct * 100) / total
        return when {
            pct >= 90 -> 3
            pct >= 60 -> 2
            else -> 1
        }
    }

    fun evaluateSessionCompletion(
        correct: Int,
        total: Int,
        previousCompletedSessions: Int,
        previousTotalStars: Int,
        alreadyOwnedBadges: Set<String>
    ): RewardGrant {
        val starsEarned = calculateStars(correct, total)
        val newSessions = previousCompletedSessions + 1
        val newTotalStars = previousTotalStars + starsEarned

        // New animal card every 5 completed sessions
        val unlockedCard = if (newSessions % 5 == 0) {
            val cardIdx = ((newSessions / 5) - 1) % ANIMAL_CARDS.size
            ANIMAL_CARDS[cardIdx]
        } else null

        val newBadges = BADGES.filter { badge ->
            !alreadyOwnedBadges.contains(badge.id) &&
                newSessions >= badge.requiredSessions &&
                newTotalStars >= badge.requiredStars
        }

        return RewardGrant(
            starsEarned = starsEarned,
            unlockedAnimalCard = unlockedCard,
            newlyUnlockedBadges = newBadges
        )
    }
}
