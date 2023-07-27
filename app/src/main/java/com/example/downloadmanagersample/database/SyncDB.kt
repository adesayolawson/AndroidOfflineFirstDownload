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
                    .build()

                INSTANCE = instance

                // return instance
                instance
            }
        }
    }
}
