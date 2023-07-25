package com.example.downloadmanagersample

import android.app.DownloadManager
import java.io.File

interface DownloaderInterface {
    fun downloadFile(url: String, downloadFile: File): Pair<Long, DownloadManager>

}

