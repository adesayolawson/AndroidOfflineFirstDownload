package com.example.downloadmanagersample.database.model.mediaData

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.downloadmanagersample.database.SyncDB

class MediaDataRepository(context: Context) {
    private val mediaDataDao = SyncDB.getDatabase(context).mediaDataDao()

    fun insert(mediaData: MediaData) = mediaDataDao.insert(mediaData)

    fun insert(mediaDataList: List<MediaData>) {
        if (mediaDataList.isNotEmpty()) {
            mediaDataDao.insert(mediaDataList)
        }
    }

    fun getMediaDataByGroupName(mediaGroup:String):List<MediaData>{
        return mediaDataDao.getMediaByGroupName(mediaGroup)
    }

    fun getCountOfDataInDB(): LiveData<Int>{
        return mediaDataDao.getCountOfDataInDB()
    }
}
