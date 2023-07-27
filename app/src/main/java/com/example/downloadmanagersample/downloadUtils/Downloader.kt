package com.example.downloadmanagersample.downloadUtils

import android.app.DownloadManager
import android.content.Context
import androidx.core.net.toUri
import java.io.File

class Downloader(context: Context): DownloaderInterface {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, downloadFile:File): Pair<Long, DownloadManager> {
        val request = DownloadManager.Request(url.toUri())
            .setAllowedOverMetered(true)
            .setRequiresCharging(false)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setTitle(downloadFile.name)
            .setDestinationUri(downloadFile.toUri())
        return downloadManager.enqueue(request) to downloadManager
    }


}
