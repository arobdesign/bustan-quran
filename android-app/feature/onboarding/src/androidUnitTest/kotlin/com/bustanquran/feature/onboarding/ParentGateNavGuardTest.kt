package com.bustanquran.feature.onboarding

import com.bustanquran.feature.onboarding.parentgate.ParentGateSession
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ParentGateNavGuardTest {

    @Before
    fun setUp() {
        ParentGateSession.lock()
    }

    @Test
    fun testParentSessionInitiallyLocked() {
        assertFalse("Parent screen must be locked initially (ONB-03)", ParentGateSession.isParentAuthenticated())
    }

    @Test
    fun testMarkAuthenticatedUnlocksSession() {
        assertFalse(ParentGateSession.isParentAuthenticated())
        ParentGateSession.markAuthenticated()
        assertTrue(ParentGateSession.isParentAuthenticated())
    }

    @Test
    fun testLockRelocksSession() {
        ParentGateSession.markAuthenticated()
        assertTrue(ParentGateSession.isParentAuthenticated())

        ParentGateSession.lock()
        assertFalse(ParentGateSession.isParentAuthenticated())
    }
}
