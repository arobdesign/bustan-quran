package com.bustanquran.feature.parent

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageHealthCheckerTest {

    @Test
    fun `isLowStorage warns below 300 MB`() {
        assertTrue(StorageHealthChecker.isLowStorage(299))
        assertFalse(StorageHealthChecker.isLowStorage(300))
    }

    @Test
    fun `verifyChecksum matches valid SHA-256`() {
        val data = "bustan-quran-audio".toByteArray()
        val hash = StorageHealthChecker.sha256Hex(data)
        assertTrue(StorageHealthChecker.verifyChecksum(data, hash))
        assertFalse(StorageHealthChecker.verifyChecksum(data, "deadbeef"))
    }
}
