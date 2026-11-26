package com.bustanquran.feature.billing

/**
 * PAY-01..04 — Play Billing 7+ product catalog & offline entitlement cache validator.
 */
enum class BillingProduct(val productId: String, val isSubscription: Boolean, val tierGranted: String, val maxProfiles: Int) {
    JUZ_AMMA_LIFETIME("juz_amma_lifetime", false, "juz_amma", 3),
    QURAN_30_LIFETIME("quran_30_lifetime", false, "quran_30", 6),
    FAMILY_MONTHLY("family_monthly", true, "family", 6),
    FAMILY_YEARLY("family_yearly", true, "family", 6)
}

data class CachedEntitlement(
    val activeTier: String = "free",
    val maxProfiles: Int = 1,
    val verifiedAtMs: Long = 0L,
    val expiresAtMs: Long? = null
) {
    companion object {
        const val RECHECK_INTERVAL_MS = 7L * 24 * 60 * 60 * 1000 // 7 days
    }

    fun needsServerRecheck(nowMs: Long): Boolean =
        activeTier != "free" && (nowMs - verifiedAtMs) >= RECHECK_INTERVAL_MS

    fun isActive(nowMs: Long): Boolean {
        if (activeTier == "free") return false
        return expiresAtMs == null || nowMs < expiresAtMs
    }
}

object EntitlementResolver {
    fun resolveFromProduct(productId: String, verifiedAtMs: Long, expiresAtMs: Long? = null): CachedEntitlement {
        val product = BillingProduct.entries.firstOrNull { it.productId == productId }
            ?: return CachedEntitlement()
        return CachedEntitlement(
            activeTier = product.tierGranted,
            maxProfiles = product.maxProfiles,
            verifiedAtMs = verifiedAtMs,
            expiresAtMs = expiresAtMs
        )
    }
}
