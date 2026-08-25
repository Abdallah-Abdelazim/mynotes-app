package com.aa.mynotes.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NOTES = "notes"
        const val NOTE_ID = "_id"
        const val NOTE_TEXT = "noteText"
        const val NOTE_LAST_CHANGED = "noteLastChanged"

        @JvmField
        val ALL_COLUMNS = arrayOf(NOTE_ID, NOTE_TEXT, NOTE_LAST_CHANGED)

        private const val TABLE_CREATE =
            "CREATE TABLE $TABLE_NOTES (" +
                    "$NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$NOTE_TEXT TEXT, " +
                    "$NOTE_LAST_CHANGED TEXT default (datetime(CURRENT_TIMESTAMP, 'localtime'))" +
                    ")"
    }
}
