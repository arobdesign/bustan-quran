package com.bustanquran.feature.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.data.database.model.AyahEntity
import com.bustanquran.core.data.database.model.TranslationEntity
import com.bustanquran.core.data.repository.ContentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Both ayah games draw from one place: `content.db`.
 *
 * The shipped fixture carries ayah rows for a single surah (106 · Quraysh), so
 * the id is a constant rather than a parameter. The ViewModel never types any
 * Qur'an text itself; when the full content pack lands, the constant becomes an
 * argument and the screens stay as they are.
 */
const val GameSurahId = 106

/** Kids translation language the games read; the design shows English. */
const val GameTranslationLang = "en"

/** What both games need: the surah name and its ayat, in order. */
data class AyatGameData(
    val surahNameAr: String = "",
    val surahNameTr: String = "",
    val ayahs: List<AyatSource> = emptyList(),
) {
    val isEmpty: Boolean get() = ayahs.isEmpty()
}

/** State the screens show before the database answers. */
val EmptySusunAyatState = SusunAyatState(
    surahNameAr = "",
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

/** One ViewModel for both ayah games; Koin creates one per back-stack entry. */
class AyatGameViewModel(
    contentRepository: ContentRepository,
) : ViewModel() {

    val data: StateFlow<AyatGameData> = combine(
        contentRepository.getSurahById(GameSurahId),
        contentRepository.getAyahs(GameSurahId),
        contentRepository.getTranslation(GameSurahId, GameTranslationLang),
    ) { surah, ayahs, translations ->
        AyatGameData(
            surahNameAr = surah?.nameAr.orEmpty(),
            surahNameTr = surah?.nameTr.orEmpty(),
            ayahs = ayahs.toAyatSources(translations),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AyatGameData())
}

/** Drops the fixtures' metadata rows and attaches the kids translation. */
private fun List<AyahEntity>.toAyatSources(
    translations: List<TranslationEntity>,
): List<AyatSource> = asSequence()
    .sortedBy { it.ayahNo }
    .map { ayah ->
        AyatSource(
            number = ayah.ayahNo,
            text = ayah.textUthmani,
            translation = translations.firstOrNull { it.ayahNo == ayah.ayahNo }?.textKids.orEmpty(),
        )
    }
    .toList()
