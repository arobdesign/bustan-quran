package com.bustanquran.core.audio

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

/**
 * ExoPlayer-backed [RecitationPlayer].
 *
 * The per-ayah MP3 URLs come from `content.db` (`meta.audio_template`, filled by
 * `tools/scripts/fetch_quran_content.py` from the eQuran.id API), so this class
 * only has to stream one file at a time and report completions back to
 * [PlayerController].
 *
 * ExoPlayer instances must live on a single thread; every caller here is the
 * player view model, which is confined to the main dispatcher.
 */
@OptIn(UnstableApi::class)
class ExoRecitationPlayer(private val context: Context) : RecitationPlayer {

    private var player: ExoPlayer? = null

    override var onCompleted: (() -> Unit)? = null
    override var onError: ((String) -> Unit)? = null

    private fun ensurePlayer(): ExoPlayer = player ?: ExoPlayer.Builder(context)
        .build()
        .also { exo ->
            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) onCompleted?.invoke()
                }

                override fun onPlayerError(error: PlaybackException) {
                    onError?.invoke(error.errorCodeName)
                }
            })
            player = exo
        }

    override fun play(url: String, speed: Float) {
        val exo = ensurePlayer()
        exo.setMediaItem(MediaItem.fromUri(url))
        exo.setPlaybackParameters(PlaybackParameters(speed))
        exo.prepare()
        exo.playWhenReady = true
    }

    override fun pause() {
        player?.pause()
    }

    override fun resume() {
        player?.play()
    }

    override fun setSpeed(speed: Float) {
        player?.setPlaybackParameters(PlaybackParameters(speed))
    }

    override fun stop() {
        player?.stop()
        player?.clearMediaItems()
    }

    override fun release() {
        player?.release()
        player = null
    }
}
