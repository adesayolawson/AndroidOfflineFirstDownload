package com.example.downloadmanagersample

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.downloadmanagersample.database.SyncEnums
import com.example.downloadmanagersample.database.model.fileSync.FileSync
import com.example.downloadmanagersample.database.model.fileSync.FileSyncRepo
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.io.File
import kotlin.random.Random

class MediaDownloadWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val fileSyncRepo = FileSyncRepo(applicationContext)
    private val fileSyncSentIn = parseFileSync(inputData.getString(MainActivity.FILE_TO_DOWNLOAD_KEY)) //convert passed in file sync

    // used passed in file sync or file sync from db if the worker is triggered by auto sync
    private val mediaFileSync = fileSyncSentIn ?: fileSyncRepo.getFileSyncByModuleAndState(
        SyncEnums.Modules.MEDIA,
        SyncEnums.FileSyncState.DOWNLOAD_PENDING
    )

    override suspend fun doWork(): Result {
        if (mediaFileSync == null) {
            return Result.success()
        }
        startForegroundService()// used to persist the worker and make sure it stays alive as long as the download is alive
        val mediaDirectory =
            "${fileSyncRepo.getDirectoryType(mediaFileSync.fileType)}/${mediaFileSync.filePath}" // the directory where the file is to be saved
        val mediaFile =
            File(applicationContext.getExternalFilesDir(mediaDirectory), mediaFileSync.fileName)
        val downloadPair = Downloader(applicationContext).downloadFile(
            url = mediaFileSync.fileURL ?: "",
            downloadFile = mediaFile
        )
        val downloadID = downloadPair.first
        val downloadManager = downloadPair.second
        try {
            checkDownloadStateAndUpdateFileSync(downloadManager, downloadID, mediaFileSync)
        } catch (e: Exception) {
            e.printStackTrace()
            fileSyncRepo.insertFileSync(mediaFileSync.copy(fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_FAILED))
        }
        return Result.success()
    }

    private fun parseFileSync(fileSyncJson: String?): FileSync? {
        if (fileSyncJson.isNullOrBlank()) {
            return null
        }
        return Gson().fromJson(fileSyncJson, FileSync::class.java)
    }

    private suspend fun checkDownloadStateAndUpdateFileSync(
        downloadManager: DownloadManager,
        downloadID: Long,
        fileSync: FileSync
    ) {
        val downloadState = getDownloadInfo(
            downloadManager = downloadManager,
            downloadID = downloadID,
            infoColumn = DownloadManager.COLUMN_STATUS,
            defaultInfo = DownloadManager.STATUS_FAILED
        )
        // reasons to keep worker alive
        val reasonsForPersistence = listOf(
            DownloadManager.STATUS_RUNNING,
            DownloadManager.STATUS_PENDING,
            DownloadManager.STATUS_PAUSED
        )
        if (reasonsForPersistence.any { it == downloadState }) {
            //use recursion with 1 second delay to simulate listener
            delay(1000)
            checkDownloadStateAndUpdateFileSync(downloadManager, downloadID, fileSync)
        }
        when (downloadState) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                fileSyncRepo.insertFileSync(fileSync.copy(fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_SUCCESSFUL))
            }
            DownloadManager.STATUS_FAILED -> {
                fileSyncRepo.insertFileSync(fileSync.copy(fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_FAILED))
                //todo: implement finer failure handling
                val failureReason = getDownloadInfo(
                    downloadManager = downloadManager,
                    downloadID = downloadID,
                    infoColumn = DownloadManager.COLUMN_REASON,
                    defaultInfo = DownloadManager.ERROR_CANNOT_RESUME
                )
            }
        }
    }

    private fun getDownloadInfo(
        downloadManager: DownloadManager,
        downloadID: Long,
        infoColumn: String,
        defaultInfo: Int
    ): Int {
        val query = DownloadManager.Query()
        query.setFilterById(downloadID) // filter your download by download Id
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(infoColumn)
            return cursor.getInt(statusIndex)
        }
        return defaultInfo
    }

    private suspend fun startForegroundService() {
        val channelID = "1212313131313"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = "Media Download Agric OS"
            val descriptionText = "This handles the downloading of media to the device notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelID, name, importance)
            notificationChannel.description = descriptionText

            val notificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("Downloading Application Media ")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }
}