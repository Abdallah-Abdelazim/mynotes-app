package com.aa.mynotes.activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.IntentCompat
import com.aa.mynotes.R
import com.aa.mynotes.data.DBOpenHelper
import com.aa.mynotes.data.NotesProvider
import com.aa.mynotes.ui.editor.EditorScreen
import com.aa.mynotes.ui.theme.MyNotesTheme
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditorActivity : ComponentActivity() {

    private var isNewNote = true
    private var noteFilter: String? = null
    private var oldText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = IntentCompat.getParcelableExtra(intent, NotesProvider.CONTENT_ITEM_TYPE, Uri::class.java)
        isNewNote = uri == null

        var lastEditedDateText: String? = null
        if (uri != null) {
            noteFilter = "${DBOpenHelper.NOTE_ID}=${uri.lastPathSegment}"
            contentResolver.query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    oldText = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.NOTE_TEXT))
                    val lastChanged = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.NOTE_LAST_CHANGED))
                    lastEditedDateText = formatLastChanged(lastChanged)
                }
            }
        }

        val title = getString(if (isNewNote) R.string.editor_title_insert_new_note else R.string.editor_title_edit_note)
        val noteTextState = mutableStateOf(oldText.orEmpty())

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishEditing(noteTextState.value)
                }
            },
        )

        setContent {
            MyNotesTheme {
                EditorScreen(
                    title = title,
                    noteText = noteTextState.value,
                    onNoteTextChange = { noteTextState.value = it },
                    lastEditedDateText = lastEditedDateText,
                    onNavigateUp = { finishEditing(noteTextState.value) },
                    onShareNote = { shareNote() },
                    onDeleteNote = { deleteNote() },
                )
            }
        }
    }

    /** Formats the note's "yyyy-MM-dd HH:mm:ss" DB timestamp for display, honoring the device's 12/24h setting. */
    private fun formatLastChanged(lastChanged: String): String? {
        val dateParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val pattern = if (DateFormat.is24HourFormat(this)) "EEE, d MMM yyyy HH:mm" else "EEE, d MMM yyyy hh:mm a"
        val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            dateFormatter.format(dateParser.parse(lastChanged)!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    private fun shareNote() {
        val shareNoteIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, oldText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareNoteIntent, getString(R.string.share_note_chooser_dialog_title)))
    }

    private fun finishEditing(currentText: String) {
        val noteText = currentText.trim()

        if (isNewNote) {
            if (noteText.isEmpty()) {
                setResult(RESULT_CANCELED)
            } else {
                insertNote(noteText)
                setResult(RESULT_OK)
            }
        } else {
            when {
                noteText.isEmpty() -> deleteNote()
                noteText == oldText -> setResult(RESULT_CANCELED)
                else -> {
                    updateNote(noteText)
                    setResult(RESULT_OK)
                }
            }
        }
        finish()
    }

    private fun updateNote(newNoteText: String) {
        val values = ContentValues().apply {
            put(DBOpenHelper.NOTE_TEXT, newNoteText)
            put(DBOpenHelper.NOTE_LAST_CHANGED, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date()))
        }
        contentResolver.update(NotesProvider.CONTENT_URI, values, noteFilter, null)
        Toast.makeText(this, R.string.note_updated_message, Toast.LENGTH_SHORT).show()
    }

    private fun insertNote(noteText: String) {
        val values = ContentValues().apply { put(DBOpenHelper.NOTE_TEXT, noteText) }
        contentResolver.insert(NotesProvider.CONTENT_URI, values)
    }

    private fun deleteNote() {
        contentResolver.delete(NotesProvider.CONTENT_URI, noteFilter, null)
        Toast.makeText(this, R.string.note_deleted_message, Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
}
