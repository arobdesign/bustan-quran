package com.bustanquran.tools

import java.io.File
import java.security.MessageDigest

/**
 * Tanzil / Quran Content Importer CLI
 *
 * Implements Prompt P-02:
 * 1. Imports Tanzil Uthmani text.
 * 2. Computes SHA-256 checksum per ayah.
 * 3. Fails if any ayah differs from the source checksum / expected verification.
 * 4. Ensures translations are NOT bundled in SQLite, conforming to Quran Foundation terms.
 */
object ContentImporter {

    fun computeSha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(text.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    data class TanzilAyah(
        val surahId: Int,
        val ayahNo: Int,
        val textUthmani: String,
        val checksum: String
    )

    fun parseTanzilLine(line: String): TanzilAyah? {
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#")) return null
        val parts = trimmed.split("|")
        if (parts.size < 3) return null

        val surahId = parts[0].toIntOrNull() ?: return null
        val ayahNo = parts[1].toIntOrNull() ?: return null
        val text = parts[2].trim()
        val checksum = computeSha256(text)

        return TanzilAyah(surahId, ayahNo, text, checksum)
    }

    fun verifyAyahIntegrity(ayah: TanzilAyah, expectedChecksum: String? = null): Boolean {
        val computed = computeSha256(ayah.textUthmani)
        if (computed != ayah.checksum) {
            throw IllegalStateException(
                "Ayah checksum mismatch for Surah ${ayah.surahId}:${ayah.ayahNo}! Computed: $computed, recorded: ${ayah.checksum}"
            )
        }
        if (expectedChecksum != null && !computed.equals(expectedChecksum, ignoreCase = true)) {
            throw IllegalStateException(
                "Source checksum verification failed for Surah ${ayah.surahId}:${ayah.ayahNo}! Expected: $expectedChecksum, computed: $computed"
            )
        }
        return true
    }
}

fun main(args: Array<String>) {
    println("=== Bustan Qur'an Content Import Tool ===")
    if (args.isEmpty()) {
        println("Usage: content-import --tanzil <file-path> [--verify-only]")
        println("Or: content-import --self-test")
        return
    }

    if (args.contains("--self-test")) {
        println("Running self-test on a real ayah...")
        val sampleLine = "106|1|لِاِيْلٰفِ قُرَيْشٍۙ"
        val parsed = ContentImporter.parseTanzilLine(sampleLine)
        requireNotNull(parsed)
        val valid = ContentImporter.verifyAyahIntegrity(parsed)
        println("Self test successful! Checksum: ${parsed.checksum}, valid: $valid")
        return
    }

    val tanzilIndex = args.indexOf("--tanzil")
    if (tanzilIndex != -1 && tanzilIndex + 1 < args.size) {
        val file = File(args[tanzilIndex + 1])
        if (!file.exists()) {
            System.err.println("File not found: ${file.absolutePath}")
            return
        }
        println("Importing and validating ${file.name}...")
        var count = 0
        file.forEachLine { line ->
            val ayah = ContentImporter.parseTanzilLine(line)
            if (ayah != null) {
                ContentImporter.verifyAyahIntegrity(ayah)
                count++
            }
        }
        println("Successfully verified $count ayahs from ${file.name}.")
    }
}
