package com.bustanquran.core.data

import com.bustanquran.core.data.repository.ContentRepository
import com.bustanquran.core.data.version.ContentManifest
import com.bustanquran.core.data.version.ContentVersionChecker
import com.bustanquran.core.data.version.VersionCheckResult
import io.mockk.coEvery
import io.mockk.mockk
import java.io.File
import java.security.MessageDigest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ContentVersionCheckerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val repository: ContentRepository = mockk()
    private lateinit var versionChecker: ContentVersionChecker

    @Before
    fun setUp() {
        versionChecker = ContentVersionChecker(repository)
    }

    @Test
    fun testCheckVersionDetectsNewVersion() = runTest {
        coEvery { repository.getVersion() } returns "1.0.0"

        val manifest = ContentManifest(
            version = "1.1.0",
            contentDbUrl = "https://example.com/content-v1.1.0.db",
            contentDbSha256 = "dummy_sha256"
        )

        val result = versionChecker.checkVersion("1.0.0", manifest)
        assertTrue(result is VersionCheckResult.UpdateAvailable)
        val update = result as VersionCheckResult.UpdateAvailable
        assertEquals("1.1.0", update.newVersion)
    }

    @Test
    fun testCheckVersionUpToDate() = runTest {
        val manifest = ContentManifest(
            version = "1.0.0",
            contentDbUrl = "https://example.com/content-v1.0.0.db",
            contentDbSha256 = "dummy_sha256"
        )

        val result = versionChecker.checkVersion("1.0.0", manifest)
        assertTrue(result is VersionCheckResult.UpToDate)
    }

    @Test
    fun testSwapContentDatabaseVerifiesChecksumBeforeReplacing() {
        val destFile = tempFolder.newFile("dest.db").apply {
            writeText("old_content")
        }
        val downloadedFile = tempFolder.newFile("downloaded.db").apply {
            writeText("new_verified_content")
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val correctSha = digest.digest("new_verified_content".toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

        // Swap with invalid checksum must FAIL and NOT alter destination
        val failedSwap = versionChecker.swapContentDatabase(downloadedFile, destFile, "wrong_hash")
        assertFalse(failedSwap)
        assertEquals("old_content", destFile.readText())

        // Swap with valid checksum must SUCCEED and replace destination
        val successfulSwap = versionChecker.swapContentDatabase(downloadedFile, destFile, correctSha)
        assertTrue(successfulSwap)
        assertEquals("new_verified_content", destFile.readText())
    }
}
