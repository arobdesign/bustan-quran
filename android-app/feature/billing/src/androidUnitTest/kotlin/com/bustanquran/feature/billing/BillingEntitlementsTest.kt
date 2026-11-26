package com.bustanquran.feature.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingEntitlementsTest {

    @Test
    fun `resolveFromProduct maps all 4 SKU tiers accurately`() {
        val juz = EntitlementResolver.resolveFromProduct("juz_amma_lifetime", 1000L)
        assertEquals("juz_amma", juz.activeTier)
        assertEquals(3, juz.maxProfiles)

        val fam = EntitlementResolver.resolveFromProduct("family_yearly", 1000L, expiresAtMs = 999_999L)
        assertEquals("family", fam.activeTier)
        assertEquals(6, fam.maxProfiles)
        assertTrue(fam.isActive(2000L))
        assertFalse(fam.isActive(1_000_000L))
    }

    @Test
    fun `needsServerRecheck triggers after 7 days`() {
        val ent = EntitlementResolver.resolveFromProduct("quran_30_lifetime", verifiedAtMs = 1000L)
        val sixDaysLater = 1000L + 6L * 24 * 60 * 60 * 1000
        val sevenDaysLater = 1000L + 7L * 24 * 60 * 60 * 1000
        assertFalse(ent.needsServerRecheck(sixDaysLater))
        assertTrue(ent.needsServerRecheck(sevenDaysLater))
    }
}
