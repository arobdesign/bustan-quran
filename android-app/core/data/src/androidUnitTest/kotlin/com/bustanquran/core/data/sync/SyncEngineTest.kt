package com.bustanquran.core.data.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncEngineTest {

    @Test
    fun `mergeProgress takes max stars and max statusRank`() {
        val local = SyncProgressRow("p1", 114, 1, stars = 2, statusRank = 2, updatedAt = 100L)
        val remote = SyncProgressRow("p1", 114, 1, stars = 3, statusRank = 1, updatedAt = 200L)
        val merged = SyncEngine.mergeProgress(local, remote)
        assertEquals(3, merged.stars)
        assertEquals(2, merged.statusRank)
    }

    @Test
    fun `mergeSettings takes latest updatedAt`() {
        val local = SyncSettingsRow("font", "large", updatedAt = 500L)
        val remote = SyncSettingsRow("font", "small", updatedAt = 400L)
        assertEquals("large", SyncEngine.mergeSettings(local, remote).value)
    }

    @Test
    fun `recordings never upload unless opt-in and parent account exist`() {
        assertFalse(SyncEngine.shouldUploadRecording(hasParentAccount = false, backupRecordingsOptIn = true))
        assertFalse(SyncEngine.shouldUploadRecording(hasParentAccount = true, backupRecordingsOptIn = false))
        assertTrue(SyncEngine.shouldUploadRecording(hasParentAccount = true, backupRecordingsOptIn = true))
    }

    @Test
    fun `chunkForUpload caps batches at 500`() {
        val items = (1..1200).toList()
        val batches = SyncEngine.chunkForUpload(items)
        assertEquals(3, batches.size)
        assertEquals(500, batches[0].size)
        assertEquals(500, batches[1].size)
        assertEquals(200, batches[2].size)
    }
}
