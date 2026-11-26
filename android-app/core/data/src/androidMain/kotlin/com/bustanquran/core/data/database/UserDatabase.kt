package com.bustanquran.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bustanquran.core.data.database.dao.AyahProgressDao
import com.bustanquran.core.data.database.dao.ChildProfileDao
import com.bustanquran.core.data.database.dao.DownloadDao
import com.bustanquran.core.data.database.dao.FamilyDao
import com.bustanquran.core.data.database.dao.PurchaseDao
import com.bustanquran.core.data.database.dao.RecordingDao
import com.bustanquran.core.data.database.dao.RewardDao
import com.bustanquran.core.data.database.dao.ScreenTimeDao
import com.bustanquran.core.data.database.dao.SessionLogDao
import com.bustanquran.core.data.database.model.AyahProgressEntity
import com.bustanquran.core.data.database.model.ChildProfileEntity
import com.bustanquran.core.data.database.model.DownloadEntity
import com.bustanquran.core.data.database.model.FamilyEntity
import com.bustanquran.core.data.database.model.PurchaseEntity
import com.bustanquran.core.data.database.model.RecordingEntity
import com.bustanquran.core.data.database.model.RewardEntity
import com.bustanquran.core.data.database.model.ScreenTimeEntity
import com.bustanquran.core.data.database.model.SessionLogEntity

@Database(
    entities = [
        FamilyEntity::class,
        ChildProfileEntity::class,
        AyahProgressEntity::class,
        SessionLogEntity::class,
        RecordingEntity::class,
        RewardEntity::class,
        ScreenTimeEntity::class,
        DownloadEntity::class,
        PurchaseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun familyDao(): FamilyDao
    abstract fun childProfileDao(): ChildProfileDao
    abstract fun ayahProgressDao(): AyahProgressDao
    abstract fun sessionLogDao(): SessionLogDao
    abstract fun recordingDao(): RecordingDao
    abstract fun rewardDao(): RewardDao
    abstract fun screenTimeDao(): ScreenTimeDao
    abstract fun downloadDao(): DownloadDao
    abstract fun purchaseDao(): PurchaseDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Example explicit migration adding sync index
                db.execSQL("CREATE INDEX IF NOT EXISTS index_ayah_progress_updated_at ON ayah_progress(updated_at)")
            }
        }
    }
}
