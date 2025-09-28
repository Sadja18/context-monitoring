package com.example.contextmonitoring.data

import android.provider.BaseColumns

object DatabaseContract {
    const val DATABASE_NAME = "context_monitoring.db"
    const val DATABASE_VERSION = 1

    object DraftRecordingsTable : BaseColumns {
        const val TABLE_NAME = "draft_recordings"
        const val COLUMN_VIDEO_PATH = "video_path"
        const val COLUMN_AUDIO_PATH = "audio_path"
        const val COLUMN_SYMPTOMS = "symptoms"
        const val COLUMN_STARTED_AT = "started_at"
    }

    object HistoryTable : BaseColumns {
        const val TABLE_NAME = "history"
        const val COLUMN_VIDEO_PATH = "video_path"
        const val COLUMN_AUDIO_PATH = "audio_path"
        const val COLUMN_SYMPTOMS = "symptoms"
        const val COLUMN_STARTED_AT = "started_at"
        const val COLUMN_SAVED_AT = "saved_at"
    }
}