package com.aa.mynotes.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aa.mynotes.data.NotesProvider
import com.aa.mynotes.ui.notes.NotesScreen
import com.aa.mynotes.ui.notes.NotesViewModel
import com.aa.mynotes.ui.theme.MyNotesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyNotesTheme {
                val viewModel: NotesViewModel = viewModel()
                val notes by viewModel.notes.collectAsStateWithLifecycle()

                NotesScreen(
                    notes = notes,
                    onNoteClick = { note -> openEditor(note.id) },
                    onAddNote = { openEditor(noteId = null) },
                    onDeleteAllNotes = { viewModel.deleteAllNotes() },
                    onAboutClick = { startActivity(Intent(this, AboutActivity::class.java)) },
                )
            }
        }
    }

    private fun openEditor(noteId: Long?) {
        val intent = Intent(this, EditorActivity::class.java)
        if (noteId != null) {
            val noteUri = Uri.parse("${NotesProvider.CONTENT_URI}/$noteId")
            intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, noteUri)
        }
        startActivity(intent)
    }
}
