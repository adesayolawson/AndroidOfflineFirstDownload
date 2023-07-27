package com.example.downloadmanagersample.database.model.fileSync

import android.content.Context
import android.os.Environment
import com.example.downloadmanagersample.database.SyncDB
import com.example.downloadmanagersample.database.SyncEnums
import java.io.File

class FileSyncRepo(private val context: Context) {
    private val fileSyncDao = SyncDB.getDatabase(context).fileSyncDao()
    fun getCountByModuleAndSyncState(
        module: SyncEnums.Modules,
        fileSyncState: SyncEnums.FileSyncState
    ): Int {
        return fileSyncDao.getCountByModuleAndSyncState(module, fileSyncState)
    }

    fun getFileSyncByModuleAndState(
        module: SyncEnums.Modules,
        fileSyncState: SyncEnums.FileSyncState
    ): FileSync? {
        return fileSyncDao.getFileSyncByModuleAndState(module, fileSyncState)
    }

    fun getFileSyncsByModuleAndState(
        module: SyncEnums.Modules,
        fileSyncState: SyncEnums.FileSyncState
    ): List<FileSync> {
        return fileSyncDao.getFileSyncsByModuleAndState(module, fileSyncState)
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

    /**
     * get a file based on the file sync object
     * @param context: the context of the calling class
     * @param fileSync: the filesSync object that holds reference to the file object
     * @return The file object else null
     * */
    fun getFileFromFileSync(context: Context, fileSync: FileSync): File? {
        val fileDirectory =
            context.getExternalFilesDir("${getDirectoryType(fileSync.fileType)}/${fileSync.filePath}")
        val file = File(fileDirectory,fileSync.fileName)
        return if (file.exists()) file else null
    }
}
