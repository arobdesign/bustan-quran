
package com.bustanquran.feature.hafalan

import kotlin.math.roundToInt

/**
 * Simplified SM-2 spaced-repetition scheduler (HFZ-04).
 * Operates on (ease, intervalDays, dueAt) stored in ayah_progress.
 * Never uses child data for external logging.
 */
data class ReviewResult(
    val ease: Float,
    val intervalDays: Int,
    val dueAtMs: Long
)

object ReviewScheduler {
    private const val MIN_EASE = 1.3f
    private const val DEFAULT_EASE = 2.5f

    /**
     * @param quality 0..5 — 0-2 fail, 3-5 pass (SM-2 convention)
     * @param currentEase stored ease factor
     * @param currentInterval current interval in days
     * @param nowMs current time in ms
     */
    fun schedule(quality: Int, currentEase: Float = DEFAULT_EASE, currentInterval: Int = 0, nowMs: Long): ReviewResult {
        val pass = quality >= 3
        val newEase = (currentEase + 0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
            .coerceAtLeast(MIN_EASE)

        val newInterval = when {
            !pass -> 1
            currentInterval == 0 -> 1
            currentInterval == 1 -> 3
            else -> (currentInterval * newEase).roundToInt().coerceAtLeast(currentInterval + 1)
        }

        val dueAt = nowMs + newInterval * 24L * 60 * 60 * 1000
        return ReviewResult(ease = newEase, intervalDays = newInterval, dueAtMs = dueAt)
    }
}
