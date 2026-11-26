package com.bustanquran.feature.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.audio.LoopMode
import com.bustanquran.core.audio.PlayerController
import com.bustanquran.core.audio.PlayerState
import com.bustanquran.core.audio.RecitationPlayer
import com.bustanquran.core.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Read model for artboard 09. Surah metadata, ayah text and translations come
 * from `content.db` through [ContentRepository]; the loop/repeat/speed state is
 * what the player sheet edits, and [PlayerController] turns that state into
 * "stream this ayah next" calls on [RecitationPlayer].
 */
data class SurahPlayerUiState(
    val surahName: String = "",
    val surahNumber: Int = 0,
    val surahNameAr: String = "",
    val ayahCount: Int = 0,
    val revelation: String = "",
    val ayahs: List<AyahItem> = emptyList(),
    val playerState: PlayerState = PlayerState(),
    /** Set when the ayah MP3 cannot be streamed; the screen offers a retry. */
    val audioError: String? = null,
)

class SurahPlayerViewModel(
    private val contentRepository: ContentRepository,
    private val recitationPlayer: RecitationPlayer,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurahPlayerUiState())
    val uiState: StateFlow<SurahPlayerUiState> = _uiState.asStateFlow()

    private var loadedSurahId: Int? = null
    private var audioTemplate: String? = null

    /** Ayah whose file is paused, so [onPlay] can resume instead of restart. */
    private var pausedAtAyah: Int? = null

    private val controller = PlayerController(
        onStateChanged = { state ->
            _uiState.value = _uiState.value.copy(playerState = state)
        },
        onPlayFile = { surahId, ayahNo -> streamAyah(surahId, ayahNo) },
    )

    init {
        recitationPlayer.onCompleted = { controller.onAyahCompleted() }
        recitationPlayer.onError = { reason ->
            _uiState.value = _uiState.value.copy(
                audioError = reason,
                playerState = _uiState.value.playerState.copy(isPlaying = false),
            )
        }
    }

    fun load(surahId: Int) {
        if (loadedSurahId == surahId) return
        loadedSurahId = surahId
        pausedAtAyah = null
        viewModelScope.launch {
            val surah = contentRepository.getSurahById(surahId).first()
            val ayat = contentRepository.getAyahs(surahId).first()
            val translations = contentRepository.getTranslation(surahId, TRANSLATION_LANG)
                .first()
                .ifEmpty { contentRepository.getTranslation(surahId, "en").first() }
            audioTemplate = contentRepository.getMeta(AUDIO_TEMPLATE_KEY)
            val total = surah?.ayahCount?.takeIf { it > 0 } ?: ayat.size
            _uiState.value = SurahPlayerUiState(
                surahName = surah?.nameTr.orEmpty(),
                surahNumber = surahId,
                surahNameAr = surah?.nameAr.orEmpty(),
                ayahCount = total,
                revelation = surah?.revelation.orEmpty().replaceFirstChar { it.uppercase() },
                ayahs = ayat.map { ayah ->
                    AyahItem(
                        ayahNo = ayah.ayahNo,
                        arabicText = ayah.textUthmani,
                        translation = translations
                            .firstOrNull { it.ayahNo == ayah.ayahNo }
                            ?.textKids
                            .orEmpty(),
                    )
                },
            )
            val previous = controller.getState()
            controller.load(
                surahId = surahId,
                totalAyahs = total,
                loopMode = previous.loopMode,
                targetRepeats = previous.targetRepeats,
            )
        }
    }

    fun onPlay() {
        if (_uiState.value.audioError != null) {
            _uiState.value = _uiState.value.copy(audioError = null)
        }
        val state = controller.getState()
        if (pausedAtAyah == state.currentAyah && !state.isFinished) {
            // Continue the file that was paused instead of restarting the ayah.
            pausedAtAyah = null
            recitationPlayer.resume()
            controller.resume()
        } else {
            pausedAtAyah = null
            controller.play()
        }
    }

    fun onPause() {
        recitationPlayer.pause()
        controller.pause()
        pausedAtAyah = controller.getState().currentAyah
    }

    fun onPrev() = controller.seekToAyah(controller.getState().currentAyah - 1)

    fun onNext() = controller.seekToAyah(controller.getState().currentAyah + 1)

    fun onAyahTap(ayahNo: Int) = controller.seekToAyah(ayahNo)

    fun onLoopModeChange(mode: LoopMode) = controller.setLoopMode(mode)

    fun onRepeatChange(repeats: Int) = controller.setTargetRepeats(repeats)

    fun onSpeedChange(speed: Float) {
        controller.setSpeed(speed)
        recitationPlayer.setSpeed(controller.getState().speed)
    }

    fun onPauseAfterAyahChange(seconds: Int) = controller.setPauseAfterAyah(seconds)

    fun onAudioErrorDismiss() {
        _uiState.value = _uiState.value.copy(audioError = null)
    }

    /** Retry from the `AudioMissing` sheet: restart the current ayah. */
    fun onRetryAudio() {
        val state = controller.getState()
        _uiState.value = _uiState.value.copy(audioError = null)
        streamAyah(state.surahId, state.currentAyah)
    }

    private fun streamAyah(surahId: Int, ayahNo: Int) {
        val template = audioTemplate ?: DEFAULT_AUDIO_TEMPLATE
        pausedAtAyah = null
        recitationPlayer.play(
            audioUrl(template, surahId, ayahNo),
            _uiState.value.playerState.speed,
        )
    }

    override fun onCleared() {
        recitationPlayer.release()
        super.onCleared()
    }

    companion object {
        const val AUDIO_TEMPLATE_KEY = "audio_template"

        /** Fallback when `content.db` predates the importer (no `meta` row yet). */
        const val DEFAULT_AUDIO_TEMPLATE =
            "https://cdn.equran.id/audio-partial/Misyari-Rasyid-Al-Afasi/{surah}{ayah}.mp3"

        private const val TRANSLATION_LANG = "id"

        /**
         * Expands `meta.audio_template`, e.g.
         * `.../Misyari-Rasyid-Al-Afasi/{surah}{ayah}.mp3` for 106:2 becomes
         * `.../Misyari-Rasyid-Al-Afasi/106002.mp3`.
         */
        fun audioUrl(template: String, surahId: Int, ayahNo: Int): String = template
            .replace("{surah}", surahId.toString().padStart(3, '0'))
            .replace("{ayah}", ayahNo.toString().padStart(3, '0'))
    }
}
