package com.bustanquran.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah")
data class SurahEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "name_ar")
    val nameAr: String,
    @ColumnInfo(name = "name_tr")
    val nameTr: String,
    @ColumnInfo(name = "ayah_count")
    val ayahCount: Int,
    val revelation: String, // makki or madani
    @ColumnInfo(name = "juz_start")
    val juzStart: Int
)

@Entity(
    tableName = "ayah",
    primaryKeys = ["surah_id", "ayah_no"]
)
data class AyahEntity(
    @ColumnInfo(name = "surah_id")
    val surahId: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    val page: Int,
    val juz: Int,
    @ColumnInfo(name = "text_uthmani")
    val textUthmani: String,
    @ColumnInfo(name = "text_simple")
    val textSimple: String,
    val checksum: String // sha256 of text_uthmani
)

@Entity(
    tableName = "word",
    primaryKeys = ["surah_id", "ayah_no", "word_no"]
)
data class WordEntity(
    @ColumnInfo(name = "surah_id")
    val surahId: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    @ColumnInfo(name = "word_no")
    val wordNo: Int,
    val text: String,
    val translit: String
)

@Entity(
    tableName = "translation",
    primaryKeys = ["surah_id", "ayah_no", "lang"]
)
data class TranslationEntity(
    @ColumnInfo(name = "surah_id")
    val surahId: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    val lang: String, // en, fr, de
    @ColumnInfo(name = "text_kids")
    val textKids: String,
    @ColumnInfo(name = "source_id")
    val sourceId: String
)

@Entity(
    tableName = "word_meaning",
    primaryKeys = ["surah_id", "ayah_no", "word_no", "lang"]
)
data class WordMeaningEntity(
    @ColumnInfo(name = "surah_id")
    val surahId: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    @ColumnInfo(name = "word_no")
    val wordNo: Int,
    val lang: String,
    val meaning: String
)

@Entity(tableName = "letter")
data class LetterEntity(
    @PrimaryKey
    val id: Int,
    val char: String,
    val name: String,
    @ColumnInfo(name = "forms_json")
    val formsJson: String,
    @ColumnInfo(name = "makhraj_note")
    val makhrajNote: String,
    @ColumnInfo(name = "example_word")
    val exampleWord: String
)

@Entity(tableName = "tajwid_rule")
data class TajwidRuleEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "color_hex")
    val colorHex: String,
    @ColumnInfo(name = "description_key")
    val descriptionKey: String
)

@Entity(
    tableName = "tajwid_mark",
    primaryKeys = ["surah_id", "ayah_no", "start_char", "end_char", "rule_id"]
)
data class TajwidMarkEntity(
    @ColumnInfo(name = "surah_id")
    val surahId: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    @ColumnInfo(name = "start_char")
    val startChar: Int,
    @ColumnInfo(name = "end_char")
    val endChar: Int,
    @ColumnInfo(name = "rule_id")
    val ruleId: Int
)

@Entity(tableName = "dua")
data class DuaEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "title_key")
    val titleKey: String,
    @ColumnInfo(name = "text_ar")
    val textAr: String,
    val translit: String,
    @ColumnInfo(name = "audio_file")
    val audioFile: String
)

@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "prophet_key")
    val prophetKey: String,
    @ColumnInfo(name = "chapter_no")
    val chapterNo: Int,
    @ColumnInfo(name = "audio_file")
    val audioFile: String,
    @ColumnInfo(name = "duration_sec")
    val durationSec: Int
)

@Entity(
    tableName = "game_content",
    primaryKeys = ["game_id", "item_id", "stage"]
)
data class GameContentEntity(
    @ColumnInfo(name = "game_id")
    val gameId: String,
    @ColumnInfo(name = "item_id")
    val itemId: String,
    val stage: String, // 3-4, 5-6, 7-10
    @ColumnInfo(name = "payload_json")
    val payloadJson: String
)

@Entity(tableName = "reciter")
data class ReciterEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name_key")
    val nameKey: String,
    val type: String, // child, adult
    val bitrate: Int,
    @ColumnInfo(name = "license_id")
    val licenseId: String
)

@Entity(tableName = "source")
data class SourceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val license: String,
    val url: String,
    @ColumnInfo(name = "verified_by")
    val verifiedBy: String,
    @ColumnInfo(name = "verified_at")
    val verifiedAt: String
)

@Entity(tableName = "meta")
data class MetaEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
