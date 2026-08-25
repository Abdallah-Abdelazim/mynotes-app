package com.aa.mynotes.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class NotesProvider : ContentProvider() {

    private lateinit var database: android.database.sqlite.SQLiteDatabase

    override fun onCreate(): Boolean {
        val dbHelper = DBOpenHelper(context!!)
        database = dbHelper.writableDatabase
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        val resolvedSelection = if (uriMatcher.match(uri) == NOTE_ID_MATCH) {
            "${DBOpenHelper.NOTE_ID}=${uri.lastPathSegment}"
        } else {
            selection
        }

        return database.query(
            DBOpenHelper.TABLE_NOTES,
            DBOpenHelper.ALL_COLUMNS,
            resolvedSelection,
            null,
            null,
            null,
            "${DBOpenHelper.NOTE_LAST_CHANGED} DESC",
        )
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = database.insert(DBOpenHelper.TABLE_NOTES, null, values)
        context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return Uri.parse("$BASE_PATH/$id")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val rowsDeleted = database.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs)
        context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return rowsDeleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val rowsUpdated = database.update(DBOpenHelper.TABLE_NOTES, values, selection, selectionArgs)
        context!!.contentResolver.notifyChange(CONTENT_URI, null)
        return rowsUpdated
    }

    companion object {
        private const val AUTHORITY = "com.aa.mynotes.notesprovider"
        private const val BASE_PATH = "notes"

        @JvmField
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$BASE_PATH")

        private const val NOTES = 1
        private const val NOTE_ID_MATCH = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, BASE_PATH, NOTES)
            addURI(AUTHORITY, "$BASE_PATH/#", NOTE_ID_MATCH)
        }

        const val CONTENT_ITEM_TYPE = "Note"
    }
}
