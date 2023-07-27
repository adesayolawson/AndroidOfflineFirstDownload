package com.example.downloadmanagersample.database

object DatabaseConstantsSync {
    object FileSyncTable {
        const val TABLE_NAME = "file_sync"
        object ColumnNames {
            const val SYNC_FLAG = "sync_flag"
            const val FILE_NAME = "file_name"
            const val FILE_TYPE = "file_type"
            const val FILE_PATH = "file_path"
            const val FILE_SYNC_STATE = "file_sync_state"
            const val MODULE = "module"
        }
    }
}
