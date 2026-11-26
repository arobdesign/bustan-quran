package com.bustanquran.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bustanquran.core.data.database.model.AyahEntity
import com.bustanquran.core.data.database.model.DuaEntity
import com.bustanquran.core.data.database.model.GameContentEntity
import com.bustanquran.core.data.database.model.LetterEntity
import com.bustanquran.core.data.database.model.MetaEntity
import com.bustanquran.core.data.database.model.ReciterEntity
import com.bustanquran.core.data.database.model.StoryEntity
import com.bustanquran.core.data.database.model.SurahEntity
import com.bustanquran.core.data.database.model.TajwidMarkEntity
import com.bustanquran.core.data.database.model.TajwidRuleEntity
import com.bustanquran.core.data.database.model.TranslationEntity
import com.bustanquran.core.data.database.model.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surah ORDER BY id ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surah WHERE juz_start = :juz ORDER BY id ASC")
    fun getSurahsByJuz(juz: Int): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surah WHERE id = :id LIMIT 1")
    fun getSurahById(id: Int): Flow<SurahEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)
}

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayah WHERE surah_id = :surahId ORDER BY ayah_no ASC")
    fun getAyahsBySurah(surahId: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayah WHERE surah_id = :surahId AND ayah_no = :ayahNo LIMIT 1")
    fun getAyah(surahId: Int, ayahNo: Int): Flow<AyahEntity?>

    @Query("SELECT * FROM ayah WHERE juz = :juz ORDER BY surah_id ASC, ayah_no ASC")
    fun getAyahsByJuz(juz: Int): Flow<List<AyahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: List<AyahEntity>)
}

@Dao
interface WordDao {
    @Query("SELECT * FROM word WHERE surah_id = :surahId AND ayah_no = :ayahNo ORDER BY word_no ASC")
    fun getWords(surahId: Int, ayahNo: Int): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)
}

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation WHERE surah_id = :surahId AND lang = :lang ORDER BY ayah_no ASC")
    fun getTranslations(surahId: Int, lang: String): Flow<List<TranslationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslations(translations: List<TranslationEntity>)
}

@Dao
interface LetterDao {
    @Query("SELECT * FROM letter ORDER BY id ASC")
    fun getAllLetters(): Flow<List<LetterEntity>>

    @Query("SELECT * FROM letter WHERE id = :id LIMIT 1")
    fun getLetterById(id: Int): Flow<LetterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetters(letters: List<LetterEntity>)
}

@Dao
interface TajwidDao {
    @Query("SELECT * FROM tajwid_rule ORDER BY id ASC")
    fun getTajwidRules(): Flow<List<TajwidRuleEntity>>

    @Query("SELECT * FROM tajwid_mark WHERE surah_id = :surahId ORDER BY ayah_no ASC, start_char ASC")
    fun getTajwidMarksForSurah(surahId: Int): Flow<List<TajwidMarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<TajwidRuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarks(marks: List<TajwidMarkEntity>)
}

@Dao
interface GameContentDao {
    @Query("SELECT * FROM game_content WHERE game_id = :gameId AND stage = :stage ORDER BY item_id ASC")
    fun getGameItems(gameId: String, stage: String): Flow<List<GameContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameContent(items: List<GameContentEntity>)
}

@Dao
interface DuaDao {
    @Query("SELECT * FROM dua ORDER BY id ASC")
    fun getAllDuas(): Flow<List<DuaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuas(duas: List<DuaEntity>)
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM story ORDER BY chapter_no ASC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)
}

@Dao
interface ReciterDao {
    @Query("SELECT * FROM reciter ORDER BY id ASC")
    fun getAllReciters(): Flow<List<ReciterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReciters(reciters: List<ReciterEntity>)
}

@Dao
interface MetaDao {
    @Query("SELECT value FROM meta WHERE `key` = :key LIMIT 1")
    suspend fun getMeta(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMeta(meta: MetaEntity)
}
