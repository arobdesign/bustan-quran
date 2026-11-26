package com.bustanquran.core.data.di

import android.content.Context
import androidx.room.Room
import com.bustanquran.core.data.database.UserDatabase
import com.bustanquran.core.data.database.dao.AyahProgressDao
import com.bustanquran.core.data.database.dao.ChildProfileDao
import com.bustanquran.core.data.database.dao.DownloadDao
import com.bustanquran.core.data.database.dao.FamilyDao
import com.bustanquran.core.data.database.dao.PurchaseDao
import com.bustanquran.core.data.database.dao.RecordingDao
import com.bustanquran.core.data.database.dao.RewardDao
import com.bustanquran.core.data.database.dao.ScreenTimeDao
import com.bustanquran.core.data.database.dao.SessionLogDao
import com.bustanquran.core.data.preferences.UserPreferencesRepository
import com.bustanquran.core.data.preferences.UserPreferencesRepositoryImpl
import com.bustanquran.core.data.repository.ActiveProfile
import com.bustanquran.core.data.repository.UserDataAdmin
import com.bustanquran.core.data.repository.UserDataAdminImpl
import com.bustanquran.core.data.repository.UserRepository
import com.bustanquran.core.data.repository.UserRepositoryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin bindings for the per-device user graph (family, profiles, progress,
 * rewards, screen time, purchases and preferences).
 */
fun userDataModule(context: Context): Module = module {

    single<UserDatabase> {
        Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user.db"
        )
            .addMigrations(UserDatabase.MIGRATION_1_2)
            .build()
    }

    single<FamilyDao> { get<UserDatabase>().familyDao() }
    single<ChildProfileDao> { get<UserDatabase>().childProfileDao() }
    single<AyahProgressDao> { get<UserDatabase>().ayahProgressDao() }
    single<SessionLogDao> { get<UserDatabase>().sessionLogDao() }
    single<RecordingDao> { get<UserDatabase>().recordingDao() }
    single<RewardDao> { get<UserDatabase>().rewardDao() }
    single<ScreenTimeDao> { get<UserDatabase>().screenTimeDao() }
    single<DownloadDao> { get<UserDatabase>().downloadDao() }
    single<PurchaseDao> { get<UserDatabase>().purchaseDao() }

    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::UserDataAdminImpl) bind UserDataAdmin::class
    single<ActiveProfile> { ActiveProfile() }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(context) }
}

/** Convenience entry point that registers the whole data layer. */
fun dataModules(context: Context): List<Module> = listOf(
    contentDataModule(context),
    userDataModule(context)
)
