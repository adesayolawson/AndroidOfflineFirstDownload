package com.example.downloadmanagersample.database.model.fileSync

import android.content.Context
import android.os.Environment
import com.example.downloadmanagersample.database.SyncDB
import com.example.downloadmanagersample.database.SyncEnums

class FileSyncRepo(val context: Context) {
    private val fileSyncDao = SyncDB.getDatabase(context).fileSyncDao()

    fun getFileSyncByModuleAndState(module: SyncEnums.Modules,fileSyncState: SyncEnums.FileSyncState): FileSync?{
        return fileSyncDao.getFileSyncByModuleAndState(module, fileSyncState)
    }

    fun getDirectoryType(fileType: SyncEnums.FileType): String {
        SyncEnums.FileType.IMAGE
        return when (fileType) {
            SyncEnums.FileType.IMAGE -> Environment.DIRECTORY_PICTURES
            SyncEnums.FileType.VIDEO -> "Videos"
            SyncEnums.FileType.AUDIO -> Environment.DIRECTORY_MUSIC
            SyncEnums.FileType.DOCUMENT -> Environment.DIRECTORY_DOCUMENTS
        }
    }

    fun insertFileSync(fileSync: FileSync) {
        fileSyncDao.insertFileSync(fileSync)
    }

    fun insertFileSyncs(fileSyncs: List<FileSync>) {
        fileSyncDao.insertFileSyncs(fileSyncs)
    }
}
