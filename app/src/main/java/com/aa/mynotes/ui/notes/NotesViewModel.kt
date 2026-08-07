package com.aa.mynotes.ui.notes

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aa.mynotes.data.DBOpenHelper
import com.aa.mynotes.data.Note
import com.aa.mynotes.data.NotesProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    // NotesProvider notifies this URI's observers on every insert/update/delete, so this Flow
    // stays in sync with the database without any caller needing to trigger a manual refresh.
    val notes: StateFlow<List<Note>> = callbackFlow {
        val contentResolver = getApplication<Application>().contentResolver
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(queryNotes(contentResolver))
            }
        }
        contentResolver.registerContentObserver(NotesProvider.CONTENT_URI, true, observer)
        trySend(queryNotes(contentResolver))
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteAllNotes() {
        getApplication<Application>().contentResolver.delete(NotesProvider.CONTENT_URI, null, null)
    }

    private fun queryNotes(contentResolver: ContentResolver): List<Note> {
        val cursor = contentResolver.query(NotesProvider.CONTENT_URI, DBOpenHelper.ALL_COLUMNS, null, null, null)
            ?: return emptyList()
        return cursor.use {
            buildList {
                while (it.moveToNext()) {
                    add(
                        Note(
                            id = it.getLong(it.getColumnIndexOrThrow(DBOpenHelper.NOTE_ID)),
                            text = it.getString(it.getColumnIndexOrThrow(DBOpenHelper.NOTE_TEXT)),
                            lastChanged = it.getString(it.getColumnIndexOrThrow(DBOpenHelper.NOTE_LAST_CHANGED)),
                        ),
                    )
                }
            }
        }
    }
}
