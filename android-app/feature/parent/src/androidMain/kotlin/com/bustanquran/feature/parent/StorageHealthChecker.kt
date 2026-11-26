package com.bustanquran.feature.parent

import java.security.MessageDigest

/**
 * P-13 — Storage & SHA-256 verification utilities for audio downloads (DLD-01..03).
 */
data class JuzAudioPackage(
    val juzNumber: Int,
    val reciterId: String,
    val sizeMb: Int,
    val sha256: String,
    val isDownloaded: Boolean = false
)

object StorageHealthChecker {
    const val LOW_STORAGE_THRESHOLD_MB = 300L

    fun isLowStorage(freeMb: Long): Boolean = freeMb < LOW_STORAGE_THRESHOLD_MB

    fun sha256Hex(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyChecksum(bytes: ByteArray, expectedSha256: String): Boolean =
        sha256Hex(bytes).equals(expectedSha256, ignoreCase = true)
}
