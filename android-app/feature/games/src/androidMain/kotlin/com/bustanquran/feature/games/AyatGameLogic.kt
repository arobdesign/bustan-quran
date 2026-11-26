package com.bustanquran.feature.games

import kotlin.random.Random

/**
 * Pure state builders for the two ayah games of the design set — "Build the
 * Ayah" (`SusunAyat.dc.html`) and "Continue the Ayah" (`SambungAyat.dc.html`).
 *
 * Everything here works on whatever the caller passes in, so the same code
 * serves `content.db` today and a fuller content pack later; no Qur'an text is
 * ever written in Kotlin. The functions are deterministic (the shuffle is seeded
 * with the ayah number) so the UI can be unit-tested.
 */

/** One ayah as the games need it: the Uthmani text plus the kids translation. */
data class AyatSource(
    val number: Int,
    val text: String,
    val translation: String = "",
)

/** "Build the Ayah" — the ayat already placed, the ayah being built, the tray. */
data class SusunAyatState(
    val surahNameAr: String,
    val targetAyah: Int,
    val stage: Int,
    val stageTotal: Int,
    /** Ayat above the blank one, already assembled and shown with translations. */
    val completed: List<AyatSource>,
    /** Words of the target ayah that are printed, not draggable. */
    val fixedPrefix: String,
    /** One dashed slot per missing word; the value is the design's ghost hint. */
    val slots: List<String>,
    /** The missing words themselves, in ayah order — what the tray must supply. */
    val answers: List<String>,
    /** The tray: the missing words plus distractors, in a stable order. */
    val options: List<String>,
    /** Width hints for the faded row of the ayah after this one. */
    val nextAyahSlots: List<Int>,
) {
    val hasTarget: Boolean get() = slots.isNotEmpty()
}

/** "Continue the Ayah" — one prompt ayah and the candidate continuations. */
data class SambungAyatState(
    val surahNameAr: String,
    val stage: Int,
    val stageTotal: Int,
    val prompt: AyatSource,
    val options: List<String>,
    val correctIndex: Int,
) {
    val hasOptions: Boolean get() = options.isNotEmpty()
}

/** The design shows three tray chips in "Build the Ayah". */
private const val OptionCount = 3

/** The design shows two dashed word slots. */
private const val SlotCount = 2

/** The faded next-ayah row cycles these box widths, as drawn in the design. */
private val GhostWidths = listOf(60, 44, 60, 70, 52)

/**
 * Splits [ayahs] at [targetIndex]: everything above it is already assembled, the
 * target ayah loses its last [SlotCount] words into the tray, and the ayah after
 * it becomes the faded "not started" row.
 */
fun buildSusunAyatState(
    surahNameAr: String,
    ayahs: List<AyatSource>,
    targetIndex: Int = (ayahs.size - 2).coerceAtLeast(0),
): SusunAyatState {
    if (ayahs.isEmpty()) {
        return SusunAyatState(
            surahNameAr = surahNameAr,
            targetAyah = 0,
            stage = 0,
            stageTotal = 0,
            completed = emptyList(),
            fixedPrefix = "",
            slots = emptyList(),
            answers = emptyList(),
            options = emptyList(),
            nextAyahSlots = emptyList(),
        )
    }

    val index = targetIndex.coerceIn(0, ayahs.lastIndex)
    val target = ayahs[index]
    val words = target.text.split(WHITESPACE).filter { it.isNotBlank() }
    val blankCount = if (words.size > 1) minOf(SlotCount, words.size) else words.size
    val fixedWords = words.dropLast(blankCount)
    val blankWords = words.takeLast(blankCount)

    val slots = blankWords.map { word -> word.firstOrNull()?.let(::ghostHint).orEmpty() }
    val options = (blankWords + distractorsFor(ayahs, index, blankWords))
        .shuffled(Random(target.number.toLong()))

    val next = ayahs.getOrNull(index + 1)

    return SusunAyatState(
        surahNameAr = surahNameAr,
        targetAyah = target.number,
        stage = index + 1,
        stageTotal = ayahs.size,
        completed = ayahs.take(index),
        fixedPrefix = fixedWords.joinToString(" "),
        slots = slots,
        answers = blankWords,
        options = options,
        nextAyahSlots = next?.let(::ghostRow).orEmpty(),
    )
}

/**
 * Pairs [ayahs] so that [stageIndex] is the prompt and the ayah after it is the
 * answer; the remaining ayat become the wrong continuations.
 */
fun buildSambungAyatState(
    surahNameAr: String,
    ayahs: List<AyatSource>,
    stageIndex: Int = 0,
): SambungAyatState {
    if (ayahs.isEmpty()) {
        return SambungAyatState(
            surahNameAr = surahNameAr,
            stage = 0,
            stageTotal = 0,
            prompt = AyatSource(number = 0, text = ""),
            options = emptyList(),
            correctIndex = -1,
        )
    }

    val index = stageIndex.coerceIn(0, ayahs.lastIndex)
    val prompt = ayahs[index]
    val answer = ayahs.getOrNull(index + 1) ?: ayahs.first()
    val wrong = ayahs
        .filterIndexed { i, _ -> i != index && ayahs[i].number != answer.number }
        .map { it.text }

    // Same deterministic shuffle as the tray above so screenshots are stable.
    val options = (listOf(answer.text) + wrong.take(OptionCount - 1))
        .shuffled(Random(prompt.number.toLong()))

    return SambungAyatState(
        surahNameAr = surahNameAr,
        stage = index + 1,
        stageTotal = ayahs.size,
        prompt = prompt,
        options = options,
        correctIndex = options.indexOf(answer.text),
    )
}

/** "﴿٢﴾"-style marker: the design closes every ayah with its number. */
fun arabicAyahMarker(number: Int): String {
    val digits = number.toString().map { digit ->
        if (digit in '0'..'9') ('\u0660' + (digit - '0')) else digit
    }
    return "\uFD3F${digits.joinToString("")}\uFD3E"
}

// ─── internals ──────────────────────────────────────────────────────────────

private val WHITESPACE = Regex("\\s+")

/** The design prints the first letter of a missing word inside its slot. */
private fun ghostHint(char: Char): String =
    if (char in '\u0600'..'\u06FF') char.toString() else ""

/** Words of the other ayat that are not already in the tray, in reading order. */
private fun distractorsFor(
    ayahs: List<AyatSource>,
    targetIndex: Int,
    blankWords: List<String>,
): List<String> {
    val needed = (OptionCount - blankWords.size).coerceAtLeast(0)
    if (needed == 0) return emptyList()
    return ayahs
        .filterIndexed { i, _ -> i != targetIndex }
        .flatMap { it.text.split(WHITESPACE) }
        .filter { it.isNotBlank() && it !in blankWords }
        .distinct()
        .take(needed)
}

/** One dashed box per word of the ayah after the target, widths cycling. */
private fun ghostRow(ayah: AyatSource): List<Int> {
    val count = ayah.text.split(WHITESPACE).count { it.isNotBlank() }
    return List(count) { GhostWidths[it % GhostWidths.size] }
}
