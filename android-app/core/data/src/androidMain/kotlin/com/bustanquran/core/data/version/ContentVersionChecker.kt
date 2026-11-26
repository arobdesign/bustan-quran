package com.bustanquran.core.data.version

import com.bustanquran.core.data.repository.ContentRepository
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlinx.serialization.Serializable

@Serializable
data class ContentManifest(
    val version: String,
    val contentDbUrl: String,
    val contentDbSha256: String
)

sealed interface VersionCheckResult {
    data object UpToDate : VersionCheckResult
    data class UpdateAvailable(val newVersion: String, val downloadUrl: String, val sha256: String) : VersionCheckResult
    data class SwappedSuccessfully(val newVersion: String) : VersionCheckResult
    data class Error(val message: String) : VersionCheckResult
}

class ContentVersionChecker constructor(
    private val repository: ContentRepository
) {

    suspend fun checkVersion(currentVersion: String?, manifest: ContentManifest): VersionCheckResult {
        val localVersion = currentVersion ?: repository.getVersion() ?: "1.0.0"
        return if (isNewerVersion(manifest.version, localVersion)) {
            VersionCheckResult.UpdateAvailable(
                newVersion = manifest.version,
                downloadUrl = manifest.contentDbUrl,
                sha256 = manifest.contentDbSha256
            )
        } else {
            VersionCheckResult.UpToDate
        }
    }

    fun verifyFileSha256(file: File, expectedSha256: String): Boolean {
        if (!file.exists()) return false
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        val computedHash = digest.digest().joinToString("") { "%02x".format(it) }
        return computedHash.equals(expectedSha256, ignoreCase = true)
    }

    fun swapContentDatabase(downloadedFile: File, destinationFile: File, expectedSha256: String): Boolean {
        if (!verifyFileSha256(downloadedFile, expectedSha256)) {
            return false
        }
        // Atomic replace
        val tempBackup = File(destinationFile.parentFile, "${destinationFile.name}.bak")
        try {
            if (destinationFile.exists()) {
                destinationFile.renameTo(tempBackup)
            }
            val success = downloadedFile.renameTo(destinationFile)
            if (success) {
                tempBackup.delete()
                return true
            } else {
                tempBackup.renameTo(destinationFile)
                return false
            }
        } catch (e: Exception) {
            if (tempBackup.exists()) {
                tempBackup.renameTo(destinationFile)
            }
            return false
        }
    }

    private fun isNewerVersion(remoteVersion: String, localVersion: String): Boolean {
        val remoteParts = remoteVersion.split(".").mapNotNull { it.toIntOrNull() }
        val localParts = localVersion.split(".").mapNotNull { it.toIntOrNull() }
        val maxLen = maxOf(remoteParts.size, localParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val l = localParts.getOrElse(i) { 0 }
            if (r > l) return true
            if (r < l) return false
        }
        return false
    }
}
