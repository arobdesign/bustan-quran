package com.bustanquran.feature.islamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.data.database.model.LetterEntity
import com.bustanquran.core.data.repository.ContentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * State behind artboard 17 (`HijaiyahLesson.dc.html`).
 *
 * One `letter` row per lesson: the glyph, its four positional shapes, the
 * makhraj note and the example word all come from `content.db`. Nothing Arabic
 * is written here — only the tatweel fallback used when a shape is missing.
 */
data class LetterForms(
    val isolated: String,
    val initial: String,
    val medial: String,
    val finalForm: String,
) {
    /** The tiles in design order: alone, start, middle, end. */
    val tiles: List<String> get() = listOf(isolated, initial, medial, finalForm)
}

data class HijaiyahLetter(
    val id: Int,
    val glyph: String,
    val name: String,
    val forms: LetterForms,
    val makhrajNote: String,
    val exampleWord: String,
)

data class HijaiyahLessonData(
    val letters: List<HijaiyahLetter> = emptyList(),
) {
    val isEmpty: Boolean get() = letters.isEmpty()
    val total: Int get() = letters.size
}

/** What the screen holds before the database answers. */
val EmptyHijaiyahLessonData = HijaiyahLessonData()

class HijaiyahLessonViewModel(
    contentRepository: ContentRepository,
) : ViewModel() {

    val data: StateFlow<HijaiyahLessonData> = contentRepository.getLetters()
        .map { letters -> HijaiyahLessonData(letters.sortedBy { it.id }.map { it.toHijaiyahLetter() }) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EmptyHijaiyahLessonData)
}

internal fun LetterEntity.toHijaiyahLetter(): HijaiyahLetter = HijaiyahLetter(
    id = id,
    glyph = char,
    name = name,
    forms = parseLetterForms(formsJson, char),
    makhrajNote = makhrajNote,
    exampleWord = exampleWord,
)

/** The joining stroke Arabic uses to show a letter in its word shapes. */
private const val Tatweel = "\u0640"

/**
 * Letters that join to the letter before them but never to the one after, so
 * their "start" shape is the isolated glyph and their "middle" shape has only
 * the leading stroke. Everything else joins both ways.
 */
private val RightJoiningOnly = setOf(
    'ا', 'أ', 'إ', 'آ', 'ؤ', 'ئ', 'ة', 'ء', 'د', 'ذ', 'ر', 'ز', 'ژ', 'و', 'ى',
)

/**
 * Reads `letter.forms_json` — `{"isolated":"ج","initial":"جـ",…}`.
 *
 * The bundled fixture ships `{}`, so any missing shape falls back to the glyph
 * joined with tatweel, which is how the design draws its four tiles. The
 * fallback respects Arabic joining, so an alif is never given a forward stroke.
 */
internal fun parseLetterForms(formsJson: String, glyph: String): LetterForms {
    val json = runCatching { Json.parseToJsonElement(formsJson).jsonObject }.getOrNull()
    fun shape(vararg keys: String): String? = keys.firstNotNullOfOrNull { key ->
        json?.get(key)?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
    }
    val joinsForward = glyph.firstOrNull()?.let { it !in RightJoiningOnly } ?: true
    return LetterForms(
        isolated = shape("isolated", "alone") ?: glyph,
        initial = shape("initial", "start")
            ?: if (joinsForward) glyph + Tatweel else glyph,
        medial = shape("medial", "middle")
            ?: if (joinsForward) Tatweel + glyph + Tatweel else Tatweel + glyph,
        finalForm = shape("final", "end") ?: (Tatweel + glyph),
    )
}
