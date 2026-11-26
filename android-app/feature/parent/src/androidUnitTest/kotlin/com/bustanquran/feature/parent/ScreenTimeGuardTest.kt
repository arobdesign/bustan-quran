package com.bustanquran.feature.parent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenTimeGuardTest {

    @Test
    fun `tick triggers limit reached when threshold exceeded`() {
        val guard = ScreenTimeGuard(ScreenTimeState("p1", "2026-11-25", limitMinutes = 1))
        assertFalse(guard.tick(59).isLimitReached)
        assertTrue(guard.tick(1).isLimitReached)
    }

    @Test
    fun `grantExtraTenMinutes requires parentGatePassed`() {
        val guard = ScreenTimeGuard(ScreenTimeState("p1", "2026-11-25", usedSeconds = 60, limitMinutes = 1))
        assertTrue(guard.currentState().isLimitReached)

        // Without parent gate -> stays locked
        assertTrue(guard.grantExtraTenMinutes(parentGatePassed = false).isLimitReached)

        // With parent gate -> unlocked for +10 mins
        val unlocked = guard.grantExtraTenMinutes(parentGatePassed = true)
        assertFalse(unlocked.isLimitReached)
        assertEquals(10, unlocked.extraMinutesGranted)
    }

    @Test
    fun `exportSummaryJson formats expected fields`() {
        val json = ParentDataExporter.exportSummaryJson("fam-1", 2, 45, "2026-11-25T10:00:00Z")
        assertTrue(json.contains("\"family_id\": \"fam-1\""))
        assertTrue(json.contains("\"total_stars\": 45"))
    }
}
