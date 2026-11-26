package com.bustanquran.core.data.di

import android.content.Context
import androidx.room.Room
import com.bustanquran.core.data.database.ContentDatabase
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
import com.bustanquran.core.data.repository.ContentRepository
import com.bustanquran.core.data.repository.ContentRepositoryImpl
import com.bustanquran.core.data.version.ContentVersionChecker
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin bindings for the read-only Qur'an content graph.
 *
 * The application context is passed in instead of being resolved from Koin so this
 * module never touches Android-only Koin APIs; when the databases move to
 * commonMain the same declaration can be reused from the iOS entry point.
 */
fun contentDataModule(context: Context): Module = module {

    single<ContentDatabase> {
        Room.databaseBuilder(
            context,
            ContentDatabase::class.java,
            "content.db"
        )
            .createFromAsset("content.db")
            .build()
    }

    single<SurahDao> { get<ContentDatabase>().surahDao() }
    single<AyahDao> { get<ContentDatabase>().ayahDao() }
    single<WordDao> { get<ContentDatabase>().wordDao() }
    single<TranslationDao> { get<ContentDatabase>().translationDao() }
    single<LetterDao> { get<ContentDatabase>().letterDao() }
    single<TajwidDao> { get<ContentDatabase>().tajwidDao() }
    single<GameContentDao> { get<ContentDatabase>().gameContentDao() }
    single<DuaDao> { get<ContentDatabase>().duaDao() }
    single<StoryDao> { get<ContentDatabase>().storyDao() }
    single<ReciterDao> { get<ContentDatabase>().reciterDao() }
    single<MetaDao> { get<ContentDatabase>().metaDao() }

    singleOf(::ContentRepositoryImpl) bind ContentRepository::class
    singleOf(::ContentVersionChecker)
}
