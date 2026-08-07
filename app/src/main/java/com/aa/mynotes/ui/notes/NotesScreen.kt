package com.aa.mynotes.ui.notes

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aa.mynotes.R
import com.aa.mynotes.data.Note
import com.aa.mynotes.ui.theme.ColorPrimary
import com.aa.mynotes.ui.theme.MyNotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onAddNote: () -> Unit,
    onDeleteAllNotes: () -> Unit,
    onAboutClick: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_description_more_options),
                            tint = Color.White,
                        )
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_delete_all_notes)) },
                            onClick = {
                                menuExpanded = false
                                if (notes.isEmpty()) {
                                    Toast.makeText(context, R.string.no_notes_to_delete_message, Toast.LENGTH_SHORT).show()
                                } else {
                                    showDeleteAllDialog = true
                                }
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_about)) },
                            onClick = {
                                menuExpanded = false
                                onAboutClick()
                            },
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                shape = CircleShape,
                containerColor = ColorPrimary,
                contentColor = Color.White,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_new_note),
                    contentDescription = stringResource(R.string.content_description_add_note),
                )
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
        ) {
            if (notes.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_notes_msg),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    items(notes, key = { it.id }) { note ->
                        NoteRow(note = note, onClick = { onNoteClick(note) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text(stringResource(R.string.are_you_sure)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAllDialog = false
                    onDeleteAllNotes()
                    Toast.makeText(context, R.string.all_notes_deleted_message, Toast.LENGTH_SHORT).show()
                }) {
                    Text(stringResource(R.string.dialog_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text(stringResource(R.string.dialog_no))
                }
            },
        )
    }
}

@Composable
private fun NoteRow(note: Note, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Image (not Icon) preserves lead_pencil's native multi-color artwork instead of
        // tinting it to a single color.
        Image(
            painter = painterResource(R.drawable.lead_pencil),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = note.text,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesScreenEmptyPreview() {
    MyNotesTheme {
        NotesScreen(notes = emptyList(), onNoteClick = {}, onAddNote = {}, onDeleteAllNotes = {}, onAboutClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesScreenWithNotesPreview() {
    MyNotesTheme {
        NotesScreen(
            notes = listOf(
                Note(1, "Buy milk", "2026-08-07 03:24:00"),
                Note(2, "Finish the Compose migration", "2026-08-07 04:00:00"),
            ),
            onNoteClick = {},
            onAddNote = {},
            onDeleteAllNotes = {},
            onAboutClick = {},
        )
    }
}
