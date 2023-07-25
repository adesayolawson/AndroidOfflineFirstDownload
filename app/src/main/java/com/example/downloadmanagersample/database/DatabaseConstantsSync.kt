package com.example.downloadmanagersample.database

object DatabaseConstantsSync {
    object SyncStatusTable {
        const val TABLE_NAME = "sync_status"
        object ColumnNames {
            const val MODULE = "module"
            const val TABLE_NAME = "table_name"
            const val STATUS = "status"
            const val TYPE = "type"
            const val MESSAGE = "message"
            const val LAST_SYNC_SUCCESS_TIME = "last_sync_success_time"
            const val LAST_SYNC_ATTEMPT_TIME = "last_sync_attempt_time"
        }
    }

    object MemberImageSyncTable {
        const val TABLE_NAME = "member_image_sync"

        object ColumnNames {
            const val UNIQUE_MEMBER_ID = "unique_member_id"
            const val IMAGE_DOWNLOAD_FLAG = "image_download_flag"
            const val IK_NUMBER = "ik_number"
            const val MESSAGE = "message"
            const val IMAGE_SIZE = "image_size"
        }
    }

    object FileSyncTable {
        const val TABLE_NAME = "file_sync"

        object ColumnNames {
            const val SYNC_FLAG = "sync_flag"
            const val FILE_NAME = "file_name"
            const val FILE_TYPE = "file_type"
            const val FILE_PATH = "file_path"
            const val FILE_SYNC_STATE = "file_sync_state"
            const val MODULE = "module"
        }
    }

    object AppUserLog {
        const val TABLE_NAME = "app_user_log"

        object ColumnNames {
            const val ID = "id"
            const val STAFF_ID = "staff_id"
            const val DATE = "date"
            const val MODULE = "module"
            const val ACTIVITY = "activity"
            const val ACTION = "action"
            const val SUBJECT = "subject"
            const val MISC = "misc"
            const val IMEI = "imei"
            const val APP_VERSION = "app_version"
            const val SYNC_FLAG = "sync_flag"
        }
    }
}
