package com.bustanquran.core.audio

/**
 * AUD-01..08 — the slice of a media engine the surah player actually needs.
 *
 * [PlayerController] owns the repeat/loop state machine but knows nothing about
 * Android; this interface is the seam that lets it drive ExoPlayer in the app
 * and a fake player in unit tests.
 */
interface RecitationPlayer {

    /** Fired when the ayah file currently playing reaches its end. */
    var onCompleted: (() -> Unit)?

    /** Fired with a human readable reason when the file cannot be played. */
    var onError: ((String) -> Unit)?

    /** Starts [url] from the beginning at [speed] (1.0 = normal). */
    fun play(url: String, speed: Float = 1.0f)

    /** Keeps the current file loaded so [resume] continues where it paused. */
    fun pause()

    fun resume()

    fun setSpeed(speed: Float)

    fun stop()

    fun release()
}
