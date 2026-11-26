package com.bustanquran.core.data

import androidx.sqlite.db.SupportSQLiteDatabase
import com.bustanquran.core.data.database.UserDatabase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class UserDatabaseMigrationTest {

    @Test
    fun testMigration1To2ExecutesSuccessfully() {
        val db: SupportSQLiteDatabase = mockk(relaxed = true)

        // Execute migration 1 -> 2
        UserDatabase.MIGRATION_1_2.migrate(db)

        // Verify index creation was executed
        verify(exactly = 1) {
            db.execSQL("CREATE INDEX IF NOT EXISTS index_ayah_progress_updated_at ON ayah_progress(updated_at)")
        }
    }
}
