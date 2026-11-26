
package com.bustanquran.feature.hafalan

import org.junit.Assert.*
import org.junit.Test

class ReviewSchedulerTest {
    private val NOW = 1_700_000_000_000L

    @Test fun `quality below 3 resets interval to 1 day`() {
        val r = ReviewScheduler.schedule(quality = 2, currentInterval = 10, nowMs = NOW)
        assertEquals(1, r.intervalDays)
    }

    @Test fun `first correct answer gives 1 day interval`() {
        val r = ReviewScheduler.schedule(quality = 4, currentInterval = 0, nowMs = NOW)
        assertEquals(1, r.intervalDays)
    }

    @Test fun `second correct answer gives 3 day interval`() {
        val r = ReviewScheduler.schedule(quality = 4, currentInterval = 1, nowMs = NOW)
        assertEquals(3, r.intervalDays)
    }

    @Test fun `third correct increases by ease factor`() {
        val r = ReviewScheduler.schedule(quality = 5, currentEase = 2.5f, currentInterval = 3, nowMs = NOW)
        assertTrue(r.intervalDays >= 4)
    }

    @Test fun `ease never drops below MIN 1_3`() {
        val r = ReviewScheduler.schedule(quality = 0, currentEase = 1.3f, currentInterval = 1, nowMs = NOW)
        assertTrue(r.ease >= 1.3f)
    }

    @Test fun `dueAt is after nowMs`() {
        val r = ReviewScheduler.schedule(quality = 4, currentInterval = 1, nowMs = NOW)
        assertTrue(r.dueAtMs > NOW)
    }
}
