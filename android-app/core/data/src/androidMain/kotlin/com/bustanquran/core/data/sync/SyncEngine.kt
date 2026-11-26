package com.bustanquran.core.data.sync

/**
 * SYN-01..03 — Offline-first sync conflict resolver & batch planner.
 * Rules:
 * - Only runs when a parent account exists (local-only mode skips sync).
 * - Batch size <= 500 rows.
 * - Stars / memorisation status conflict resolution = max.
 * - Settings conflict resolution = latest updatedAt.
 * - Recordings upload ONLY when backupRecordings = true.
 */
data class SyncProgressRow(
    val profileId: String,
    val surahId: Int,
    val ayahNo: Int,
    val stars: Int,
    val statusRank: Int, // 0=not_started, 1=learning, 2=memorised
    val updatedAt: Long
)

data class SyncSettingsRow(
    val key: String,
    val value: String,
    val updatedAt: Long
)

object SyncEngine {
    const val MAX_BATCH_SIZE = 500

    fun shouldSync(hasParentAccount: Boolean): Boolean = hasParentAccount

    fun shouldUploadRecording(hasParentAccount: Boolean, backupRecordingsOptIn: Boolean): Boolean =
        hasParentAccount && backupRecordingsOptIn

    fun mergeProgress(local: SyncProgressRow, remote: SyncProgressRow): SyncProgressRow {
        return SyncProgressRow(
            profileId = local.profileId,
            surahId = local.surahId,
            ayahNo = local.ayahNo,
            stars = maxOf(local.stars, remote.stars),
            statusRank = maxOf(local.statusRank, remote.statusRank),
            updatedAt = maxOf(local.updatedAt, remote.updatedAt)
        )
    }

    fun mergeSettings(local: SyncSettingsRow, remote: SyncSettingsRow): SyncSettingsRow =
        if (remote.updatedAt > local.updatedAt) remote else local

    fun <T> chunkForUpload(rows: List<T>): List<List<T>> =
        rows.chunked(MAX_BATCH_SIZE)
}
