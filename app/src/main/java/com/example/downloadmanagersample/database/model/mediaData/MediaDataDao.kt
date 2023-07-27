package com.example.downloadmanagersample.database.model.mediaData

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.downloadmanagersample.database.DatabaseConstantsSync
import com.example.downloadmanagersample.database.model.mediaData.MediaData

@Dao
interface MediaDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mediaData: MediaData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mediaDataList: List<MediaData>)


    @Query("SELECT * FROM ${DatabaseConstantsSync.MediaDataTable.TABLE_NAME} WHERE " +
            "${DatabaseConstantsSync.MediaDataTable.ColumnNames.MEDIA_GROUP} =:mediaGroup ")
    fun getMediaByGroupName(mediaGroup:String):List<MediaData>

    @Query("SELECT count(*) FROM ${DatabaseConstantsSync.MediaDataTable.TABLE_NAME} ")
    fun getCountOfDataInDB():LiveData<Int>
}
