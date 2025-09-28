package com.example.contextmonitoring.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class RecordingDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseContract.DATABASE_NAME,
    null,
    DatabaseContract.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        // Create draft_recordings table
        val createDraftTable = """
            CREATE TABLE ${DatabaseContract.DraftRecordingsTable.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.DraftRecordingsTable.COLUMN_VIDEO_PATH} TEXT,
                ${DatabaseContract.DraftRecordingsTable.COLUMN_AUDIO_PATH} TEXT,
                ${DatabaseContract.DraftRecordingsTable.COLUMN_SYMPTOMS} TEXT,
                ${DatabaseContract.DraftRecordingsTable.COLUMN_STARTED_AT} INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createDraftTable)

        // Create history table
        val createHistoryTable = """
            CREATE TABLE ${DatabaseContract.HistoryTable.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.HistoryTable.COLUMN_VIDEO_PATH} TEXT,
                ${DatabaseContract.HistoryTable.COLUMN_AUDIO_PATH} TEXT,
                ${DatabaseContract.HistoryTable.COLUMN_SYMPTOMS} TEXT,
                ${DatabaseContract.HistoryTable.COLUMN_STARTED_AT} INTEGER NOT NULL,
                ${DatabaseContract.HistoryTable.COLUMN_SAVED_AT} INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createHistoryTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.DraftRecordingsTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.HistoryTable.TABLE_NAME}")
        onCreate(db)
    }
}