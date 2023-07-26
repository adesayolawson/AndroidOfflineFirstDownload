package com.example.downloadmanagersample

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
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
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val FILE_TO_DOWNLOAD_KEY = "media_file_to_download"
        const val NOTIFICATION_CHANNEL_ID = "media_notification_channel_id"
        const val WORK_TAG = "media_download_worker"
    }

    private val fileSyncRepo = FileSyncRepo(applicationContext)
    private val fileSyncSentIn =
        parseFileSync(inputData.getString(MainActivity.FILE_TO_DOWNLOAD_KEY)) //convert passed in file sync

    // used passed in file sync or file sync from db if the worker is triggered by auto sync
    private val mediaFileSyncs =
        if (fileSyncSentIn != null) {
            listOf(fileSyncSentIn)
        } else {
            fileSyncRepo.getFileSyncsByModuleAndState(
                SyncEnums.Modules.MEDIA,
                SyncEnums.FileSyncState.DOWNLOAD_PENDING
            )
        }

    override suspend fun doWork(): Result {
        if (mediaFileSyncs.isEmpty()) {
            return Result.success()
        }
        startForegroundService()// used to persist the worker and make sure it stays alive as long as the download is alive
        mediaFileSyncs.forEach { mediaFileSync ->
            val mediaDirectory =
                "${fileSyncRepo.getDirectoryType(mediaFileSync.fileType)}/${mediaFileSync.filePath}" // the directory where the file is to be saved
            //create file object to store the downloaded information
            val mediaFile =
                File(applicationContext.getExternalFilesDir(mediaDirectory), mediaFileSync.fileName)
            if (mediaFile.exists()) {
                fileSyncRepo.insertFileSync(
                    mediaFileSync.copy(
                        fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_SUCCESSFUL,
                        fileSize = mediaFile.length()
                    )
                )
                return@forEach
            }
            val downloadPair = Downloader(applicationContext).downloadFile(
                url = mediaFileSync.fileURL ?: "",
                downloadFile = mediaFile
            )
            val downloadID = downloadPair.first
            val downloadManager = downloadPair.second
            try {
                checkDownloadStateAndUpdateFileSync(
                    downloadManager,
                    downloadID,
                    mediaFileSync,
                    mediaFile
                )
            } catch (e: Exception) {
                e.printStackTrace()
                fileSyncRepo.insertFileSync(mediaFileSync.copy(fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_FAILED))
                downloadManager.remove(downloadID)
            }
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
        fileSync: FileSync,
        file: File
    ) {
        // update file sync to reflect that download is running to avoid downloading files twice
        fileSyncRepo.insertFileSync(fileSync.copy(fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_RUNNING))


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
            checkDownloadStateAndUpdateFileSync(downloadManager, downloadID, fileSync, file)
        }
        when (downloadState) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                fileSyncRepo.insertFileSync(
                    fileSync.copy(
                        fileSyncState = SyncEnums.FileSyncState.DOWNLOAD_SUCCESSFUL,
                        fileSize = file.length()
                    )
                )
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
                Log.i("Failure Reason", failureReason.toString())
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
        val channelID = NOTIFICATION_CHANNEL_ID
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
        // create notification for file being downloaded
        if (fileSyncSentIn != null) {
            setForeground(
                ForegroundInfo(
                    Random.nextInt(),
                    NotificationCompat.Builder(applicationContext, channelID)
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setContentText("Downloading ${fileSyncSentIn.fileName} ")
                        .setContentTitle("Download in progress")
                        .build()
                )
            )
        } else {
            setForeground(
                ForegroundInfo(
                    Random.nextInt(),
                    NotificationCompat.Builder(applicationContext, channelID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentText("Downloading Application Media ")
                        .setContentTitle("Download in progress")
                        .build()
                )
            )
        }
    }
}