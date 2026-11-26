package com.bustanquran.core.data.repository

import com.bustanquran.core.data.database.dao.AyahDao
import com.bustanquran.core.data.database.dao.GameContentDao
import com.bustanquran.core.data.database.dao.LetterDao
import com.bustanquran.core.data.database.dao.MetaDao
import com.bustanquran.core.data.database.dao.SurahDao
import com.bustanquran.core.data.database.dao.TajwidDao
import com.bustanquran.core.data.database.dao.TranslationDao
import com.bustanquran.core.data.database.dao.WordDao
import com.bustanquran.core.data.database.model.AyahEntity
import com.bustanquran.core.data.database.model.GameContentEntity
import com.bustanquran.core.data.database.model.LetterEntity
import com.bustanquran.core.data.database.model.SurahEntity
import com.bustanquran.core.data.database.model.TajwidMarkEntity
import com.bustanquran.core.data.database.model.TajwidRuleEntity
import com.bustanquran.core.data.database.model.TranslationEntity
import com.bustanquran.core.data.database.model.WordEntity
import java.security.MessageDigest
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun getSurahList(juz: Int? = null): Flow<List<SurahEntity>>
    fun getSurahById(id: Int): Flow<SurahEntity?>
    fun getAyahs(surahId: Int): Flow<List<AyahEntity>>
    fun getAyah(surahId: Int, ayahNo: Int): Flow<AyahEntity?>
    fun getWords(surahId: Int, ayahNo: Int): Flow<List<WordEntity>>
    fun getTranslation(surahId: Int, lang: String): Flow<List<TranslationEntity>>
    fun getGameItems(gameId: String, stage: String): Flow<List<GameContentEntity>>
    fun getLetters(): Flow<List<LetterEntity>>
    fun getTajwidRules(): Flow<List<TajwidRuleEntity>>
    fun getTajwidMarks(surahId: Int): Flow<List<TajwidMarkEntity>>
    suspend fun getVersion(): String?

    /**
     * Reads a single `meta` row, e.g. `audio_template` — the per-ayah MP3 URL
     * pattern the content importer stores next to the ayat.
     */
    suspend fun getMeta(key: String): String?
    fun calculateSha256(text: String): String
    fun verifyAyahChecksum(ayah: AyahEntity): Boolean
}

class ContentRepositoryImpl constructor(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val wordDao: WordDao,
    private val translationDao: TranslationDao,
    private val letterDao: LetterDao,
    private val tajwidDao: TajwidDao,
    private val gameContentDao: GameContentDao,
    private val metaDao: MetaDao
) : ContentRepository {

    override fun getSurahList(juz: Int?): Flow<List<SurahEntity>> {
        return if (juz != null) {
            surahDao.getSurahsByJuz(juz)
        } else {
            surahDao.getAllSurahs()
        }
    }

    override fun getSurahById(id: Int): Flow<SurahEntity?> = surahDao.getSurahById(id)

    override fun getAyahs(surahId: Int): Flow<List<AyahEntity>> = ayahDao.getAyahsBySurah(surahId)

    override fun getAyah(surahId: Int, ayahNo: Int): Flow<AyahEntity?> = ayahDao.getAyah(surahId, ayahNo)

    override fun getWords(surahId: Int, ayahNo: Int): Flow<List<WordEntity>> = wordDao.getWords(surahId, ayahNo)

    override fun getTranslation(surahId: Int, lang: String): Flow<List<TranslationEntity>> =
        translationDao.getTranslations(surahId, lang)

    override fun getGameItems(gameId: String, stage: String): Flow<List<GameContentEntity>> =
        gameContentDao.getGameItems(gameId, stage)

    override fun getLetters(): Flow<List<LetterEntity>> = letterDao.getAllLetters()

    override fun getTajwidRules(): Flow<List<TajwidRuleEntity>> = tajwidDao.getTajwidRules()

    override fun getTajwidMarks(surahId: Int): Flow<List<TajwidMarkEntity>> =
        tajwidDao.getTajwidMarksForSurah(surahId)

    override suspend fun getVersion(): String? = metaDao.getMeta("version")

    override suspend fun getMeta(key: String): String? = metaDao.getMeta(key)

    override fun calculateSha256(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override fun verifyAyahChecksum(ayah: AyahEntity): Boolean {
        val calculated = calculateSha256(ayah.textUthmani)
        return calculated.equals(ayah.checksum, ignoreCase = true)
    }
}
