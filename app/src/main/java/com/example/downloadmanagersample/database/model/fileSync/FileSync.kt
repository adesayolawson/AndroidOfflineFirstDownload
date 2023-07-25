package com.example.downloadmanagersample.database.model.fileSync

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.downloadmanagersample.database.DatabaseConstantsSync
import com.example.downloadmanagersample.database.SyncEnums
import kotlinx.parcelize.Parcelize

@Entity(tableName = DatabaseConstantsSync.FileSyncTable.TABLE_NAME)
@Parcelize
data class FileSync(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstantsSync.FileSyncTable.ColumnNames.FILE_NAME)
    val fileName: String,

    @ColumnInfo(name = DatabaseConstantsSync.FileSyncTable.ColumnNames.MODULE)
    val module: SyncEnums.Modules,

    @ColumnInfo(name = DatabaseConstantsSync.FileSyncTable.ColumnNames.FILE_TYPE)
    val fileType: SyncEnums.FileType,

    @ColumnInfo(name = DatabaseConstantsSync.FileSyncTable.ColumnNames.FILE_PATH)
    val filePath: String,

    @ColumnInfo(name = DatabaseConstantsSync.FileSyncTable.ColumnNames.FILE_SYNC_STATE)
    val fileSyncState: SyncEnums.FileSyncState,

    @ColumnInfo(name = "file_url")
    val fileURL: String?,

    @ColumnInfo(name = "file_size")
    val fileSize: Long,

    @ColumnInfo(name = DatabaseConstantsSync.FileSyncTable.ColumnNames.SYNC_FLAG)
    val syncFlag: Int = 0
) : Parcelable
