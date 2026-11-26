package com.bustanquran.tools

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentImporterTest {

    @Test
    fun testParseTanzilLineAndVerifyIntegrity() {
        val line = "106|1|لِاِيْلٰفِ قُرَيْشٍۙ"
        val parsed = ContentImporter.parseTanzilLine(line)

        assertNotNull(parsed)
        assertEquals(106, parsed!!.surahId)
        assertEquals(1, parsed.ayahNo)
        assertEquals("لِاِيْلٰفِ قُرَيْشٍۙ", parsed.textUthmani)

        val expectedSha = ContentImporter.computeSha256("لِاِيْلٰفِ قُرَيْشٍۙ")
        assertEquals(expectedSha, parsed.checksum)

        val isValid = ContentImporter.verifyAyahIntegrity(parsed, expectedSha)
        assertTrue(isValid)
    }

    @Test(expected = IllegalStateException::class)
    fun testVerifyAyahIntegrityFailsOnTamperedChecksum() {
        val line = "106|1|لِاِيْلٰفِ قُرَيْشٍۙ"
        val parsed = ContentImporter.parseTanzilLine(line)
        assertNotNull(parsed)

        // Verifying with an incorrect expected checksum must throw IllegalStateException
        ContentImporter.verifyAyahIntegrity(parsed!!, "tampered_wrong_checksum")
    }
}
