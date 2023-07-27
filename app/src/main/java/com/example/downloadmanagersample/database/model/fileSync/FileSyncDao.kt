package com.example.downloadmanagersample.database.model.fileSync

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.downloadmanagersample.database.DatabaseConstantsSync
import com.example.downloadmanagersample.database.SyncEnums

@Dao
abstract class FileSyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFileSync(fileSync: FileSync)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAllFileSync(list: List<FileSync>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFileSyncs(fileSyncs: List<FileSync>)

    @Query(
        "SELECT * FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE sync_flag =0 " +
            "AND ${DatabaseConstantsSync.FileSyncTable.ColumnNames.MODULE} = :module LIMIT 1"
    )
    abstract fun getFileToUpload(module: SyncEnums.Modules): FileSync?

    @Query(
        "SELECT * FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE sync_flag =0 " +
            "AND ${DatabaseConstantsSync.FileSyncTable.ColumnNames.MODULE} = :module"
    )
    abstract fun getFilesToUpload(module: SyncEnums.Modules): List<FileSync>?

    @Query("SELECT count(*) FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE sync_flag = 0 ")
    abstract fun getUploadFileCount(): Int

    @Query("SELECT * FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE file_name = :fileName")
    abstract fun getFileSyncByName(fileName: String): FileSync?

    @Query(
        "SELECT * FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE " +
            "${DatabaseConstantsSync.FileSyncTable.ColumnNames.FILE_PATH} = :fileDir"
    )
    abstract fun getFileSyncByDirectory(fileDir: String): List<FileSync>

    @Query(
        "SELECT * FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE " +
            "${DatabaseConstantsSync.FileSyncTable.ColumnNames.SYNC_FLAG} = 3 AND " +
            "${DatabaseConstantsSync.FileSyncTable.ColumnNames.MODULE} = :moduleName"
    )
    abstract fun getFilesSyncDownloadListByModule(moduleName: String): List<FileSync>?

    @Query(
        "SELECT * FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE " +
                "${DatabaseConstantsSync.FileSyncTable.ColumnNames.FILE_SYNC_STATE} =:fileSyncState AND " +
                "${DatabaseConstantsSync.FileSyncTable.ColumnNames.MODULE} =:module LIMIT 1"
    )
    abstract fun getFileSyncByModuleAndState(module: SyncEnums.Modules,fileSyncState: SyncEnums.FileSyncState): FileSync?
    @Query(
        "SELECT * FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE " +
                "${DatabaseConstantsSync.FileSyncTable.ColumnNames.FILE_SYNC_STATE} =:fileSyncState AND " +
                "${DatabaseConstantsSync.FileSyncTable.ColumnNames.MODULE} =:module"
    )
    abstract fun getFileSyncsByModuleAndState(module: SyncEnums.Modules,fileSyncState: SyncEnums.FileSyncState): List<FileSync>

    @Query(
        "SELECT count(*) FROM ${DatabaseConstantsSync.FileSyncTable.TABLE_NAME} WHERE " +
                "${DatabaseConstantsSync.FileSyncTable.ColumnNames.FILE_SYNC_STATE} =:fileSyncState AND " +
                "${DatabaseConstantsSync.FileSyncTable.ColumnNames.MODULE} =:module"
    )
    abstract fun getCountByModuleAndSyncState(module: SyncEnums.Modules, fileSyncState: SyncEnums.FileSyncState): Int

}
