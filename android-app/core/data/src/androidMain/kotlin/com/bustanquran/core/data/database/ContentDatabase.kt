package com.bustanquran.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bustanquran.core.data.database.dao.AyahDao
import com.bustanquran.core.data.database.dao.DuaDao
import com.bustanquran.core.data.database.dao.GameContentDao
import com.bustanquran.core.data.database.dao.LetterDao
import com.bustanquran.core.data.database.dao.MetaDao
import com.bustanquran.core.data.database.dao.ReciterDao
import com.bustanquran.core.data.database.dao.StoryDao
import com.bustanquran.core.data.database.dao.SurahDao
import com.bustanquran.core.data.database.dao.TajwidDao
import com.bustanquran.core.data.database.dao.TranslationDao
import com.bustanquran.core.data.database.dao.WordDao
import com.bustanquran.core.data.database.model.AyahEntity
import com.bustanquran.core.data.database.model.DuaEntity
import com.bustanquran.core.data.database.model.GameContentEntity
import com.bustanquran.core.data.database.model.LetterEntity
import com.bustanquran.core.data.database.model.MetaEntity
import com.bustanquran.core.data.database.model.ReciterEntity
import com.bustanquran.core.data.database.model.SourceEntity
import com.bustanquran.core.data.database.model.StoryEntity
import com.bustanquran.core.data.database.model.SurahEntity
import com.bustanquran.core.data.database.model.TajwidMarkEntity
import com.bustanquran.core.data.database.model.TajwidRuleEntity
import com.bustanquran.core.data.database.model.TranslationEntity
import com.bustanquran.core.data.database.model.WordEntity
import com.bustanquran.core.data.database.model.WordMeaningEntity

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        WordEntity::class,
        TranslationEntity::class,
        WordMeaningEntity::class,
        LetterEntity::class,
        TajwidRuleEntity::class,
        TajwidMarkEntity::class,
        DuaEntity::class,
        StoryEntity::class,
        GameContentEntity::class,
        ReciterEntity::class,
        SourceEntity::class,
        MetaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ContentDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun wordDao(): WordDao
    abstract fun translationDao(): TranslationDao
    abstract fun letterDao(): LetterDao
    abstract fun tajwidDao(): TajwidDao
    abstract fun gameContentDao(): GameContentDao
    abstract fun duaDao(): DuaDao
    abstract fun storyDao(): StoryDao
    abstract fun reciterDao(): ReciterDao
    abstract fun metaDao(): MetaDao
}
