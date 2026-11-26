package com.bustanquran.feature.onboarding.parentgate

import kotlin.random.Random

data class ParentGateChallenge(
    val questionText: String,
    val correctAnswer: Int,
    val options: List<Int>
)

object ParentGateChallengeGenerator {

    private val numberWordsEn = mapOf(
        1 to "One", 2 to "Two", 3 to "Three", 4 to "Four", 5 to "Five",
        6 to "Six", 7 to "Seven", 8 to "Eight", 9 to "Nine", 10 to "Ten"
    )

    private val numberWordsAr = mapOf(
        1 to "واحد", 2 to "اثنان", 3 to "ثلاثة", 4 to "أربعة", 5 to "خمسة",
        6 to "ستة", 7 to "سبعة", 8 to "ثمانية", 9 to "تسعة", 10 to "عشرة"
    )

    private val numberWordsFr = mapOf(
        1 to "Un", 2 to "Deux", 3 to "Trois", 4 to "Quatre", 5 to "Cinq",
        6 to "Six", 7 to "Sept", 8 to "Huit", 9 to "Neuf", 10 to "Dix"
    )

    private val numberWordsDe = mapOf(
        1 to "Eins", 2 to "Zwei", 3 to "Drei", 4 to "Vier", 5 to "Fünf",
        6 to "Sechs", 7 to "Sieben", 8 to "Acht", 9 to "Neun", 10 to "Zehn"
    )

    fun generate(lang: String = "en", random: Random = Random): ParentGateChallenge {
        val a = random.nextInt(3, 10)
        val b = random.nextInt(2, 9)
        val answer = a + b

        val wordA: String
        val wordB: String
        val plusWord: String

        when (lang.lowercase()) {
            "ar" -> {
                wordA = numberWordsAr[a] ?: a.toString()
                wordB = numberWordsAr[b] ?: b.toString()
                plusWord = "زائد"
            }
            "fr" -> {
                wordA = numberWordsFr[a] ?: a.toString()
                wordB = numberWordsFr[b] ?: b.toString()
                plusWord = "plus"
            }
            "de" -> {
                wordA = numberWordsDe[a] ?: a.toString()
                wordB = numberWordsDe[b] ?: b.toString()
                plusWord = "plus"
            }
            else -> {
                wordA = numberWordsEn[a] ?: a.toString()
                wordB = numberWordsEn[b] ?: b.toString()
                plusWord = "plus"
            }
        }

        val question = "$wordA $plusWord $wordB"

        // Generate 2 plausible wrong options
        val wrong1 = if (answer > 3 && random.nextBoolean()) answer - 2 else answer + 2
        val wrong2 = if (answer > 2 && random.nextBoolean()) answer - 1 else answer + 3
        val options = listOf(answer, wrong1, wrong2).distinct().let {
            if (it.size < 3) (it + listOf(answer + 4, answer - 3)).distinct().take(3) else it
        }.shuffled(random)

        return ParentGateChallenge(
            questionText = question,
            correctAnswer = answer,
            options = options
        )
    }
}
