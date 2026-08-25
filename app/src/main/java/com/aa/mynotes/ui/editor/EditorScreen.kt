package com.aa.mynotes.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aa.mynotes.R
import com.aa.mynotes.ui.theme.ColorPrimary
import com.aa.mynotes.ui.theme.MyNotesTheme

const val NOTE_TEXT_FIELD_TAG = "noteTextField"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    title: String,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    lastEditedDateText: String?,
    onNavigateUp: () -> Unit,
    onShareNote: () -> Unit,
    onDeleteNote: () -> Unit,
) {
    val isExistingNote = lastEditedDateText != null
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_navigate_up),
                            tint = Color.White,
                        )
                    }
                },
                actions = {
                    if (isExistingNote) {
                        IconButton(onClick = onShareNote) {
                            Icon(
                                painter = painterResource(R.drawable.ic_share_2),
                                contentDescription = stringResource(R.string.action_share_note),
                                tint = Color.White,
                            )
                        }
                        IconButton(onClick = onDeleteNote) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = stringResource(R.string.action_delete_note),
                                tint = Color.White,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
        ) {
            NoteTextField(
                noteText = noteText,
                onNoteTextChange = onNoteTextChange,
                modifier = Modifier.weight(1f),
            )
            if (lastEditedDateText != null) {
                BottomBar(lastEditedDateText)
            }
        }
    }
}

@Composable
private fun NoteTextField(noteText: String, onNoteTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Box(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        if (noteText.isEmpty()) {
            Text(
                text = stringResource(R.string.note_edit_text_hint),
                fontSize = 22.sp,
                color = Color.Gray,
            )
        }
        BasicTextField(
            value = noteText,
            onValueChange = onNoteTextChange,
            modifier = Modifier.fillMaxSize().testTag(NOTE_TEXT_FIELD_TAG),
            textStyle = TextStyle(fontSize = 22.sp, color = textColor),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            cursorBrush = SolidColor(textColor),
        )
    }
}

@Composable
private fun BottomBar(lastEditedDateText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(ColorPrimary),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(R.string.last_edited_text_view), color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = lastEditedDateText, color = Color.White, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
private fun EditorScreenNewNotePreview() {
    MyNotesTheme {
        EditorScreen(
            title = "New note",
            noteText = "",
            onNoteTextChange = {},
            lastEditedDateText = null,
            onNavigateUp = {},
            onShareNote = {},
            onDeleteNote = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditorScreenExistingNotePreview() {
    MyNotesTheme {
        EditorScreen(
            title = "Note editor",
            noteText = "Baseline test note",
            onNoteTextChange = {},
            lastEditedDateText = "Fri, 7 Aug 2026 03:24 AM",
            onNavigateUp = {},
            onShareNote = {},
            onDeleteNote = {},
        )
    }
}
