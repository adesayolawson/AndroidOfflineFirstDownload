package com.example.downloadmanagersample.database

object SyncEnums {
    enum class SyncStates {
        SUCCESS, FAILED, NEVER_SYNCED, NOT_SYNCED
    }
    enum class SyncType {
        UPLOAD, DOWNLOAD
    }

    // Add modules here as they are being added to the app
    enum class Modules {
        ID, HR_MEMBERS, HRFO, FARMING, PORTFOLIO_MANAGER, COLLECTION_CENTRE, TFM, POULTRY, PFM, PAYMENT_COLLECTION, FERTILIZER_SALES,MEDIA
    }

    enum class FileType {
        VIDEO, AUDIO, IMAGE, DOCUMENT
    }

    enum class FileSyncState{
        UPLOAD_PENDING, UPLOAD_SUCCESSFUL, DOWNLOAD_PENDING,
        DOWNLOAD_SUCCESSFUL,DOWNLOAD_FAILED, UPLOAD_FAILED
    }
}
