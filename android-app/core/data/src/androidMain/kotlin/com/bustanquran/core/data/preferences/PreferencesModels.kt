package com.bustanquran.core.data.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
data class FamilyPreferences(
    val appLang: String = "en",
    val dailyLimitMin: Int = 30,
    val backupRecordings: Boolean = false, // Strictest default (GDPR/Families)
    val analyticsOptIn: Boolean = false,   // Strictest default (GDPR/Families)
    val music: String = "none",           // "none", "rebana", "nature" (Teluk default: none)
    val wifiOnlyDownloads: Boolean = true // Download over Wi-Fi only (parent setting)
)

@Serializable
data class ProfilePreferences(
    val appLang: String = "en",
    val meaningLang: String = "en",
    val reciterId: String = "child_boy",
    val translit: Boolean = true,
    val tajwidColors: Boolean = true,
    val nightMode: Boolean = false,
    val textSizeLevel: Int = 1 // 0: small, 1: medium, 2: large
)

object FamilyPreferencesSerializer : Serializer<FamilyPreferences> {
    override val defaultValue: FamilyPreferences = FamilyPreferences()

    override suspend fun readFrom(input: InputStream): FamilyPreferences {
        try {
            val jsonString = input.readBytes().decodeToString()
            if (jsonString.isBlank()) return defaultValue
            return Json.decodeFromString(FamilyPreferences.serializer(), jsonString)
        } catch (serialization: SerializationException) {
            throw CorruptionException("Cannot read FamilyPreferences", serialization)
        }
    }

    override suspend fun writeTo(t: FamilyPreferences, output: OutputStream) {
        val jsonString = Json.encodeToString(FamilyPreferences.serializer(), t)
        output.write(jsonString.encodeToByteArray())
    }
}

object ProfilePreferencesSerializer : Serializer<ProfilePreferences> {
    override val defaultValue: ProfilePreferences = ProfilePreferences()

    override suspend fun readFrom(input: InputStream): ProfilePreferences {
        try {
            val jsonString = input.readBytes().decodeToString()
            if (jsonString.isBlank()) return defaultValue
            return Json.decodeFromString(ProfilePreferences.serializer(), jsonString)
        } catch (serialization: SerializationException) {
            throw CorruptionException("Cannot read ProfilePreferences", serialization)
        }
    }

    override suspend fun writeTo(t: ProfilePreferences, output: OutputStream) {
        val jsonString = Json.encodeToString(ProfilePreferences.serializer(), t)
        output.write(jsonString.encodeToByteArray())
    }
}
