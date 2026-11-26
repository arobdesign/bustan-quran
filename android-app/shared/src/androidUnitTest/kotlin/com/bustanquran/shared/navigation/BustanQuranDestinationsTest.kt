package com.bustanquran.shared.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BustanQuranDestinationsTest {

    @Test
    fun testCoreDestinationsAreDefined() {
        val destinations = listOf(
            BustanQuranDestinations.MAIN,
            BustanQuranDestinations.HOME,
            BustanQuranDestinations.ONBOARDING,
            BustanQuranDestinations.PARENT_GATE,
            BustanQuranDestinations.SURAH_PLAYER,
            BustanQuranDestinations.QURAN_LIBRARY,
            BustanQuranDestinations.MUSHAF_PAGE,
            BustanQuranDestinations.HAFALAN,
            BustanQuranDestinations.HAFALAN_MAP,
            BustanQuranDestinations.GAME_LIST,
            BustanQuranDestinations.BALON_HURUF,
            BustanQuranDestinations.SUSUN_AYAT,
            BustanQuranDestinations.PARENT_DASHBOARD,
            BustanQuranDestinations.PAYWALL,
            BustanQuranDestinations.SETTINGS,
            BustanQuranDestinations.REWARD,
            BustanQuranDestinations.KOLEKSI_KARTU
        )

        // All destinations must be unique non-blank strings
        val uniqueSet = destinations.toSet()
        assertEquals(destinations.size, uniqueSet.size)
        destinations.forEach { route ->
            assertTrue("Route should not be blank", route.isNotBlank())
        }
    }

    @Test
    fun testAllMiniGameDestinationsExist() {
        val gameDestinations = listOf(
            BustanQuranDestinations.BALON_HURUF,
            BustanQuranDestinations.SUSUN_AYAT,
            BustanQuranDestinations.IKAN_HARAKAT,
            BustanQuranDestinations.MEMORI_KATA,
            BustanQuranDestinations.SAMBUNG_AYAT,
            BustanQuranDestinations.G_KEBUN_HURUF,
            BustanQuranDestinations.G_BAYANGAN,
            BustanQuranDestinations.G_KERETA_SURAH,
            BustanQuranDestinations.G_BINTANG,
            BustanQuranDestinations.G_SAMBUNG_HURUF,
            BustanQuranDestinations.G_LARI_HIJAIYAH,
            BustanQuranDestinations.G_AYAT_HILANG,
            BustanQuranDestinations.G_URUTKAN_AYAT,
            BustanQuranDestinations.G_PASAR_HURUF,
            BustanQuranDestinations.G_TEBAK_SURAH,
            BustanQuranDestinations.G_SUSUN_ARTI,
            BustanQuranDestinations.G_KUIS_KISAH,
            BustanQuranDestinations.G_KELUARGA
        )

        assertEquals(18, gameDestinations.size)
    }
}
