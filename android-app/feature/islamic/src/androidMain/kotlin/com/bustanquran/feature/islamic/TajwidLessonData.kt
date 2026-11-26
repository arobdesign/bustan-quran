package com.bustanquran.feature.islamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.data.database.model.AyahEntity
import com.bustanquran.core.data.database.model.TajwidRuleEntity
import com.bustanquran.core.data.repository.ContentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * State behind artboard 18 (`TajwidLesson.dc.html`).
 *
 * The rule list is `content.db`'s `tajwid_rule` table (eight rules, each with its
 * own colour and description key) and the example ayah is read from the `ayah`
 * table. The one thing the database does not carry yet is the letter list per
 * rule, so Qalqalah's five letters stay here as teaching data until that column
 * lands.
 */
val QalqalahLetters: List<String> = listOf("ق", "ط", "ب", "ج", "د")

/** Name the design's lesson teaches; the view model opens this rule first. */
const val QalqalahRuleName = "Qalqalah"

/** The surah the design quotes (Al-Ikhlas), read straight from the content pack. */
const val TajwidExampleSurahId = 112

data class TajwidLessonState(
    val ruleName: String = "",
    val rulePosition: Int = 0,
    val ruleTotal: Int = 0,
    val ruleColorHex: String = "",
    val descriptionKey: String = "",
    val letters: List<String> = emptyList(),
    val exampleSurahName: String = "",
    val exampleAyahNo: Int = 0,
    val exampleAyah: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = -1,
) {
    val isEmpty: Boolean get() = ruleName.isBlank()
    val hasExample: Boolean get() = exampleAyah.isNotBlank()
    val hasOptions: Boolean get() = correctIndex >= 0
}

/** What the screen holds before the database answers. */
val EmptyTajwidLessonState = TajwidLessonState()

class TajwidLessonViewModel(
    contentRepository: ContentRepository,
) : ViewModel() {

    val data: StateFlow<TajwidLessonState> = combine(
        contentRepository.getTajwidRules(),
        contentRepository.getAyahs(TajwidExampleSurahId),
        contentRepository.getSurahById(TajwidExampleSurahId),
        contentRepository.getLetters(),
    ) { rules, ayahs, surah, letters ->
        buildTajwidLesson(
            rules = rules,
            exampleSurahName = surah?.nameTr.orEmpty(),
            exampleAyahs = ayahs,
            contentLetters = letters.map { it.char },
            ruleLetters = QalqalahLetters,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EmptyTajwidLessonState)
}

/**
 * Pure assembly so the rule choice and the quick-check options are testable
 * without Room. Qalqalah wins because it is the design's lesson; any other rule
 * set still renders with the first rule.
 */
fun buildTajwidLesson(
    rules: List<TajwidRuleEntity>,
    exampleSurahName: String,
    exampleAyahs: List<AyahEntity>,
    contentLetters: List<String>,
    ruleLetters: List<String>,
): TajwidLessonState {
    val ordered = rules.sortedBy { it.id }
    val rule = ordered.firstOrNull { it.name.equals(QalqalahRuleName, ignoreCase = true) }
        ?: ordered.firstOrNull()
        ?: return EmptyTajwidLessonState
    val example = exampleAyahs.sortedBy { it.ayahNo }.firstOrNull()
    val (options, correctIndex) = buildTajwidCheckOptions(contentLetters, ruleLetters)
    return TajwidLessonState(
        ruleName = rule.name,
        rulePosition = ordered.indexOf(rule) + 1,
        ruleTotal = ordered.size,
        ruleColorHex = rule.colorHex,
        descriptionKey = rule.descriptionKey,
        letters = ruleLetters,
        exampleSurahName = exampleSurahName,
        exampleAyahNo = example?.ayahNo ?: 0,
        exampleAyah = example?.textUthmani.orEmpty(),
        options = options,
        correctIndex = correctIndex,
    )
}

/**
 * Three tiles for the quick check: one rule letter that is right and two letters
 * from `content.db` that are not, laid out wrong / right / wrong like the design.
 * When the letter table cannot supply two distractors the row simply gets fewer
 * tiles — the correct index still points at the right one.
 */
fun buildTajwidCheckOptions(
    contentLetters: List<String>,
    ruleLetters: List<String>,
): Pair<List<String>, Int> {
    val correct = ruleLetters.getOrNull(1) ?: ruleLetters.firstOrNull()
    if (correct == null) return emptyList<String>() to -1
    val distractors = contentLetters
        .filter { it.isNotBlank() && it !in ruleLetters }
        .distinct()
        .take(2)
    val options = buildList {
        distractors.getOrNull(0)?.let(::add)
        add(correct)
        distractors.getOrNull(1)?.let(::add)
    }
    return options to options.indexOf(correct)
}
