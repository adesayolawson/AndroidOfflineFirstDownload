package com.example.downloadmanagersample.database

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.downloadmanagersample.MediaDownloadWorker
import com.example.downloadmanagersample.database.model.fileSync.FileSync
import com.google.gson.Gson

object Utils {
    /**
     * Function to start the media sync worker
     * @param context: the context of the calling class
     * @param fileSync: the fileSync object to be used in downloading the data,
     * default value of null when worker is triggered by auto sync
     * */
    fun startMediaSync(context: Context, fileSync: FileSync? = null) {
        val workManagerInstance = WorkManager.getInstance(context)

        // use the file name as a work tag to enable worker be enqueued uniquely
        val workTag = fileSync?.fileName ?: MediaDownloadWorker.WORK_TAG
        // file sync will be passed in if the worker is called from a typical download place
        val fileSyncGson = if (fileSync == null) {
            null
        } else {
            Gson().toJson(fileSync) // convert file sync to json string to be used in worker
        }
        // data to be passed into the worker
        val inputFileSyncData =
            Data.Builder().putString(MediaDownloadWorker.FILE_TO_DOWNLOAD_KEY, fileSyncGson).build()


        val mediaDownloadRequest = OneTimeWorkRequestBuilder<MediaDownloadWorker>()
            .setInputData(inputFileSyncData)
            .setConstraints(Constraints(NetworkType.CONNECTED, requiresDeviceIdle = false))
            .addTag(workTag)
            .build()
        workManagerInstance.beginUniqueWork(
            workTag, ExistingWorkPolicy.KEEP, mediaDownloadRequest
        ).enqueue()
    }
}