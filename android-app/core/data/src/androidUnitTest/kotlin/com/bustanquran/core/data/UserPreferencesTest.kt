package com.bustanquran.core.data

import com.bustanquran.core.data.preferences.FamilyPreferences
import com.bustanquran.core.data.preferences.FamilyPreferencesSerializer
import com.bustanquran.core.data.preferences.ProfilePreferences
import com.bustanquran.core.data.preferences.ProfilePreferencesSerializer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserPreferencesTest {

    @Test
    fun testFamilyPreferencesDefaultValuesFollowFamiliesPolicy() {
        val defaultPrefs = FamilyPreferences()

        // GDPR / Families strictest defaults (CLAUDE.md & PRD section 12)
        assertFalse("Backup recordings must default to OFF", defaultPrefs.backupRecordings)
        assertFalse("Analytics must default to OFF", defaultPrefs.analyticsOptIn)
        assertEquals("Teluk music policy default must be 'none'", "none", defaultPrefs.music)
        assertEquals(30, defaultPrefs.dailyLimitMin)
    }

    @Test
    fun testFamilyPreferencesSerializationRoundTrip() = runTest {
        val initial = FamilyPreferences(
            appLang = "ar",
            dailyLimitMin = 45,
            backupRecordings = true,
            analyticsOptIn = false,
            music = "nature"
        )

        val outputStream = ByteArrayOutputStream()
        FamilyPreferencesSerializer.writeTo(initial, outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val restored = FamilyPreferencesSerializer.readFrom(inputStream)

        assertEquals(initial.appLang, restored.appLang)
        assertEquals(initial.dailyLimitMin, restored.dailyLimitMin)
        assertEquals(initial.backupRecordings, restored.backupRecordings)
        assertEquals(initial.analyticsOptIn, restored.analyticsOptIn)
        assertEquals(initial.music, restored.music)
    }

    @Test
    fun testProfilePreferencesSerializationRoundTrip() = runTest {
        val initial = ProfilePreferences(
            appLang = "fr",
            meaningLang = "fr",
            reciterId = "child_girl",
            translit = false,
            tajwidColors = true,
            nightMode = true,
            textSizeLevel = 2
        )

        val outputStream = ByteArrayOutputStream()
        ProfilePreferencesSerializer.writeTo(initial, outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val restored = ProfilePreferencesSerializer.readFrom(inputStream)

        assertEquals(initial.appLang, restored.appLang)
        assertEquals(initial.meaningLang, restored.meaningLang)
        assertEquals(initial.reciterId, restored.reciterId)
        assertEquals(initial.translit, restored.translit)
        assertEquals(initial.tajwidColors, restored.tajwidColors)
        assertEquals(initial.nightMode, restored.nightMode)
        assertEquals(initial.textSizeLevel, restored.textSizeLevel)
    }
}
