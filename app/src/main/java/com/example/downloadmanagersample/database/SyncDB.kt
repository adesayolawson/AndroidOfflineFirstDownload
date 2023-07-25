package com.example.downloadmanagersample.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.downloadmanagersample.database.model.fileSync.FileSync
import com.example.downloadmanagersample.database.model.fileSync.FileSyncDao

@Database(
    entities = [
        FileSync::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class SyncDB : RoomDatabase() {


    abstract fun fileSyncDao(): FileSyncDao


    companion object {

        @Volatile
        private var INSTANCE: SyncDB? = null

        fun getDatabase(context: Context): SyncDB {
            // If the INSTANCE is not null, then return it else create a new instance
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SyncDB::class.java,
                    "syncDB"
                ).addMigrations()
//                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()

                INSTANCE = instance

                // return instance
                instance
            }
        }

/*        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                with(database) {
                    execSQL(
                        "CREATE TABLE member_image_sync ( unique_member_id TEXT NOT NULL, " +
                            "ik_number TEXT NOT NULL, image_download_flag INTEGER NOT NULL," +
                            " message TEXT NOT NULL, image_size TEXT NOT NULL, " +
                            "PRIMARY KEY(unique_member_id))"
                    )

                    execSQL(
                        "CREATE TABLE file_sync ( file_name TEXT NOT NULL, " +
                            "file_type TEXT NOT NULL, module TEXT NOT NULL, sync_flag INTEGER NOT NULL," +
                            " file_path TEXT NOT NULL, " +
                            "PRIMARY KEY(file_name))"
                    )
                }
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                with(database) {
                    execSQL(
                        "CREATE TABLE ${DatabaseConstantsSync.AppUserLog.TABLE_NAME} " +
                            "(`${DatabaseConstantsSync.AppUserLog.ColumnNames.ID}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.STAFF_ID}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.DATE}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.MODULE}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.ACTIVITY}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.ACTION}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.SUBJECT}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.MISC}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.APP_VERSION}` TEXT NOT NULL, " +
                            "`${DatabaseConstantsSync.AppUserLog.ColumnNames.SYNC_FLAG}` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`${DatabaseConstantsSync.AppUserLog.ColumnNames.ID}`))"
                    )
                }
            }
        }

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // RiskLevelToVisitFreqMapping table
                database.execSQL(
                    "ALTER TABLE ${DatabaseConstantsSync.AppUserLog.TABLE_NAME} " +
                        "ADD COLUMN `${DatabaseConstantsSync.AppUserLog.ColumnNames.IMEI}` TEXT NOT NULL " +
                        "DEFAULT ''"

                )
            }
        }*/
    }
}
