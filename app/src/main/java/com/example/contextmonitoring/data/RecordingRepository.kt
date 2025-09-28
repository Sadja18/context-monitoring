package com.example.contextmonitoring.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import java.time.Instant

class RecordingRepository private constructor(context: Context) {
    private val databaseHelper = RecordingDatabaseHelper(context)
    private val database: SQLiteDatabase = databaseHelper.writableDatabase

    // Singleton pattern
    companion object {
        @Volatile
        private var INSTANCE: RecordingRepository? = null

        fun getInstance(context: Context): RecordingRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RecordingRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun updateDraftRecording(
        draftId: Long,
        videoPath: String? = null,
        audioPath: String? = null,
        symptoms: List<String>? = null
    ): Boolean {
        val contentValues = ContentValues()

        // Only update fields that are provided (not null)
        if (videoPath != null) {
            contentValues.put(DatabaseContract.DraftRecordingsTable.COLUMN_VIDEO_PATH, videoPath)
        }
        if (audioPath != null) {
            contentValues.put(DatabaseContract.DraftRecordingsTable.COLUMN_AUDIO_PATH, audioPath)
        }
        if (symptoms != null) {
            contentValues.put(DatabaseContract.DraftRecordingsTable.COLUMN_SYMPTOMS, symptoms.joinToString(","))
        }

        // Don't update started_at when updating existing draft

        val rowsAffected = database.update(
            DatabaseContract.DraftRecordingsTable.TABLE_NAME,
            contentValues,
            "${BaseColumns._ID} = ?",
            arrayOf(draftId.toString())
        )

        return rowsAffected > 0
    }

    // Save draft recording
    fun saveDraftRecording(
        videoPath: String?,
        audioPath: String?,
        symptoms: List<String>
    ): Long {
        val contentValues = ContentValues().apply {
            put(DatabaseContract.DraftRecordingsTable.COLUMN_VIDEO_PATH, videoPath)
            put(DatabaseContract.DraftRecordingsTable.COLUMN_AUDIO_PATH, audioPath)
            put(DatabaseContract.DraftRecordingsTable.COLUMN_SYMPTOMS, symptoms.joinToString(","))
            put(DatabaseContract.DraftRecordingsTable.COLUMN_STARTED_AT, Instant.now().toEpochMilli())
        }
        return database.insert(DatabaseContract.DraftRecordingsTable.TABLE_NAME, null, contentValues)
    }

    // Get current draft (most recent)
    fun getCurrentDraft(): DraftRecording? {
        val cursor = database.query(
            DatabaseContract.DraftRecordingsTable.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseContract.DraftRecordingsTable.COLUMN_STARTED_AT} DESC",
            "1"
        )

        return if (cursor.moveToFirst()) {
            DraftRecording(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                videoPath = cursor.getStringOrNull(cursor, DatabaseContract.DraftRecordingsTable.COLUMN_VIDEO_PATH),
                audioPath = cursor.getStringOrNull(cursor, DatabaseContract.DraftRecordingsTable.COLUMN_AUDIO_PATH),
                symptoms = cursor.getStringOrNull(cursor, DatabaseContract.DraftRecordingsTable.COLUMN_SYMPTOMS)?.split(",") ?: emptyList(),
                startedAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.DraftRecordingsTable.COLUMN_STARTED_AT))
            ).also { cursor.close() }
        } else {
            cursor.close()
            null
        }
    }

    // Move draft to history
    fun moveDraftToHistory(draftId: Long): Long {
        val draft = getDraftById(draftId) ?: return -1

        val contentValues = ContentValues().apply {
            put(DatabaseContract.HistoryTable.COLUMN_VIDEO_PATH, draft.videoPath)
            put(DatabaseContract.HistoryTable.COLUMN_AUDIO_PATH, draft.audioPath)
            put(DatabaseContract.HistoryTable.COLUMN_SYMPTOMS, draft.symptoms.joinToString(","))
            put(DatabaseContract.HistoryTable.COLUMN_STARTED_AT, draft.startedAt)
            put(DatabaseContract.HistoryTable.COLUMN_SAVED_AT, Instant.now().toEpochMilli())
        }

        val historyId = database.insert(DatabaseContract.HistoryTable.TABLE_NAME, null, contentValues)

        // Delete the draft after moving to history
        if (historyId != -1L) {
            database.delete(
                DatabaseContract.DraftRecordingsTable.TABLE_NAME,
                "${BaseColumns._ID} = ?",
                arrayOf(draftId.toString())
            )
        }

        return historyId
    }

    // Get all history records
    fun getAllHistory(): List<HistoryRecording> {
        val cursor = database.query(
            DatabaseContract.HistoryTable.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseContract.HistoryTable.COLUMN_SAVED_AT} DESC"
        )

        val historyList = mutableListOf<HistoryRecording>()
        while (cursor.moveToNext()) {
            historyList.add(
                HistoryRecording(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                    videoPath = cursor.getStringOrNull(cursor, DatabaseContract.HistoryTable.COLUMN_VIDEO_PATH),
                    audioPath = cursor.getStringOrNull(cursor, DatabaseContract.HistoryTable.COLUMN_AUDIO_PATH),
                    symptoms = cursor.getStringOrNull(cursor, DatabaseContract.HistoryTable.COLUMN_SYMPTOMS)?.split(",") ?: emptyList(),
                    startedAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.HistoryTable.COLUMN_STARTED_AT)),
                    savedAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.HistoryTable.COLUMN_SAVED_AT))
                )
            )
        }
        cursor.close()
        return historyList
    }

    // Delete all records (for "Delete all captured" button)
    fun deleteAllRecords() {
        database.delete(DatabaseContract.DraftRecordingsTable.TABLE_NAME, null, null)
        database.delete(DatabaseContract.HistoryTable.TABLE_NAME, null, null)
    }

    // Helper method to safely get string from cursor
    private fun android.database.Cursor.getStringOrNull(cursor: android.database.Cursor, columnName: String): String? {
        val columnIndex = cursor.getColumnIndexOrThrow(columnName)
        return if (cursor.isNull(columnIndex)) null else cursor.getString(columnIndex)
    }

    // Get specific draft by ID
    private fun getDraftById(id: Long): DraftRecording? {
        val cursor = database.query(
            DatabaseContract.DraftRecordingsTable.TABLE_NAME,
            null,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            DraftRecording(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                videoPath = cursor.getStringOrNull(cursor, DatabaseContract.DraftRecordingsTable.COLUMN_VIDEO_PATH),
                audioPath = cursor.getStringOrNull(cursor, DatabaseContract.DraftRecordingsTable.COLUMN_AUDIO_PATH),
                symptoms = cursor.getStringOrNull(cursor, DatabaseContract.DraftRecordingsTable.COLUMN_SYMPTOMS)?.split(",") ?: emptyList(),
                startedAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.DraftRecordingsTable.COLUMN_STARTED_AT))
            ).also { cursor.close() }
        } else {
            cursor.close()
            null
        }
    }
}

// Data classes for database records
data class DraftRecording(
    val id: Long,
    val videoPath: String?,
    val audioPath: String?,
    val symptoms: List<String>,
    val startedAt: Long
)

data class HistoryRecording(
    val id: Long,
    val videoPath: String?,
    val audioPath: String?,
    val symptoms: List<String>,
    val startedAt: Long,
    val savedAt: Long
)