package com.example.downloadmanagersample.database.model.mediaData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.downloadmanagersample.database.DatabaseConstantsSync
import com.example.downloadmanagersample.database.SyncEnums
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = DatabaseConstantsSync.MediaDataTable.TABLE_NAME)
data class MediaData(
    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.MEDIA_ID)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.MEDIA_ID)
    @PrimaryKey
    @Expose
    var mediaId: String,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.FILE_TYPE)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.FILE_TYPE)
    @Expose
    var fileType: SyncEnums.FileType?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.FILE_NAME)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.FILE_NAME)
    @Expose
    var fileName: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.LANGUAGE)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.LANGUAGE)
    @Expose
    var language: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.LENGTH)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.LENGTH)
    @Expose
    var length: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.MODULE)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.MODULE)
    @Expose
    var module: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.TRANSCRIPT)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.TRANSCRIPT)
    @Expose
    var transcript: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.PHASE)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.PHASE)
    @Expose
    var phase: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.USER_ROLES)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.USER_ROLES)
    @Expose
    var userRoles: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.MEDIA_GROUP)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.MEDIA_GROUP)
    @Expose
    var mediaGroup: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.PUBLIC_URL)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.PUBLIC_URL)
    @Expose
    var publicUrl: String?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.DEFAULT_FLAG)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.DEFAULT_FLAG)
    @Expose
    var defaultFlag: Int?,

    @SerializedName(DatabaseConstantsSync.MediaDataTable.ColumnNames.SIZE)
    @ColumnInfo(name = DatabaseConstantsSync.MediaDataTable.ColumnNames.SIZE)
    @Expose
    var size: String?,

) {
    override fun toString(): String {
        return "MediaData(\n" +
            "mediaId='$mediaId'\n" +
            "fileType='$fileType'\n" +
            "fileName='$fileName'\n" +
            "language ='$language'\n" +
            "length='$length'\n" +
            "module='$module'\n" +
            "phase='$phase'\n" +
            "userRoles='$userRoles'\n" +
            "mediaGroup='$mediaGroup'\n" +
            "publicUrl='$publicUrl'\n" +
            "defaultFlag='$defaultFlag'\n" +
            "size='$size'\n" +
            ")"
    }
}
