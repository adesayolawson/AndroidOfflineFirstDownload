package com.example.downloadmanagersample.database

object SyncEnums {

    // Add modules here as they are being added to the app
    enum class Modules {
        MEDIA
    }

    enum class FileType {
        VIDEO, AUDIO, IMAGE, DOCUMENT
    }

    enum class FileSyncState {
        UPLOAD_PENDING, UPLOAD_SUCCESSFUL, DOWNLOAD_PENDING,
        DOWNLOAD_SUCCESSFUL, DOWNLOAD_FAILED, UPLOAD_FAILED, DOWNLOAD_RUNNING
    }
}
