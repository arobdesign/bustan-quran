
package com.bustanquran.core.audio

/** AUD-01..08 — Pure Kotlin player state machine; NO Android dependencies.
 *  ExoPlayer is wired separately in AudioService so this stays unit-testable. */

enum class LoopMode { PER_AYAH, PER_SURAH }

data class PlayerState(
    val surahId: Int = 0,
    val totalAyahs: Int = 0,
    val currentAyah: Int = 1,
    val loopMode: LoopMode = LoopMode.PER_AYAH,
    val targetRepeats: Int = 1,          // 0 = infinite
    val currentRepeat: Int = 1,
    val pauseAfterAyahSec: Int = 0,      // 0, 2, or 4
    val speed: Float = 1.0f,
    val isPlaying: Boolean = false,
    val isFinished: Boolean = false
) {
    val displayRepeat: String
        get() = if (targetRepeats == 0) "∞" else "$currentRepeat/$targetRepeats"
    val displayPosition: String
        get() = "Ayah $currentAyah of $totalAyahs · Repeat $displayRepeat"
}

class PlayerController(
    private val onStateChanged: (PlayerState) -> Unit = {},
    private val onPlayFile: (surahId: Int, ayahNo: Int) -> Unit = { _, _ -> },
    private val onFinished: () -> Unit = {}
) {
    private var state = PlayerState()
        set(value) { field = value; onStateChanged(value) }

    fun load(surahId: Int, totalAyahs: Int, loopMode: LoopMode = LoopMode.PER_AYAH, targetRepeats: Int = 1) {
        state = PlayerState(
            surahId = surahId,
            totalAyahs = totalAyahs,
            loopMode = loopMode,
            targetRepeats = targetRepeats,
            currentAyah = 1,
            currentRepeat = 1
        )
    }

    fun play() { state = state.copy(isPlaying = true); playCurrentFile() }
    fun pause() { state = state.copy(isPlaying = false) }
    fun resume() { state = state.copy(isPlaying = true) }
    fun seekToAyah(ayahNo: Int) {
        state = state.copy(currentAyah = ayahNo.coerceIn(1, state.totalAyahs), currentRepeat = 1)
        if (state.isPlaying) playCurrentFile()
    }
    fun setLoopMode(mode: LoopMode) { state = state.copy(loopMode = mode) }
    fun setTargetRepeats(n: Int) { state = state.copy(targetRepeats = n, currentRepeat = 1) }
    fun setSpeed(s: Float) { state = state.copy(speed = s.coerceIn(0.5f, 2.0f)) }
    fun setPauseAfterAyah(sec: Int) { state = state.copy(pauseAfterAyahSec = sec) }

    /** Called by ExoPlayer wrapper when an ayah file finishes playing. */
    fun onAyahCompleted() {
        val s = state
        when (s.loopMode) {
            LoopMode.PER_AYAH -> handlePerAyahCompletion(s)
            LoopMode.PER_SURAH -> handlePerSurahCompletion(s)
        }
    }

    private fun handlePerAyahCompletion(s: PlayerState) {
        val infinite = s.targetRepeats == 0
        val doneRepeating = !infinite && s.currentRepeat >= s.targetRepeats
        if (!doneRepeating) {
            // repeat same ayah
            state = s.copy(currentRepeat = if (infinite) s.currentRepeat + 1 else s.currentRepeat + 1)
            playCurrentFile()
        } else {
            // advance to next ayah, reset repeat counter
            val next = s.currentAyah + 1
            if (next > s.totalAyahs) {
                state = s.copy(isPlaying = false, isFinished = true)
                onFinished()
            } else {
                state = s.copy(currentAyah = next, currentRepeat = 1)
                playCurrentFile()
            }
        }
    }

    private fun handlePerSurahCompletion(s: PlayerState) {
        val next = s.currentAyah + 1
        if (next <= s.totalAyahs) {
            state = s.copy(currentAyah = next)
            playCurrentFile()
        } else {
            // finished one full pass of surah
            val infinite = s.targetRepeats == 0
            val doneRepeating = !infinite && s.currentRepeat >= s.targetRepeats
            if (!doneRepeating) {
                state = s.copy(currentAyah = 1, currentRepeat = s.currentRepeat + 1)
                playCurrentFile()
            } else {
                state = s.copy(isPlaying = false, isFinished = true)
                onFinished()
            }
        }
    }

    private fun playCurrentFile() {
        onPlayFile(state.surahId, state.currentAyah)
    }

    fun getState(): PlayerState = state
}
