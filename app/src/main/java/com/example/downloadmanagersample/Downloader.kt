package com.example.downloadmanagersample

import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import androidx.core.net.toUri
import java.io.File

class Downloader(private val context: Context):DownloaderInterface {

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
