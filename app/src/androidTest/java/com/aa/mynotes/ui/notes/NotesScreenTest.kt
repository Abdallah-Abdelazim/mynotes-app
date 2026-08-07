package com.aa.mynotes.ui.notes

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aa.mynotes.data.Note
import com.aa.mynotes.ui.theme.MyNotesTheme
import org.junit.Rule
import org.junit.Test

class NotesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleNotes = listOf(
        Note(1, "Buy milk", "2026-08-07 03:24:00"),
        Note(2, "Finish the Compose migration", "2026-08-07 04:00:00"),
    )

    @Test
    fun emptyNotes_showsNoNotesMessage() {
        setContent(notes = emptyList())

        composeTestRule.onNodeWithText("There're no notes to display.\n Start adding some by pressing '+' button below.")
            .assertExists()
    }

    @Test
    fun withNotes_showsEachNoteText() {
        setContent(notes = sampleNotes)

        composeTestRule.onNodeWithText("Buy milk").assertExists()
        composeTestRule.onNodeWithText("Finish the Compose migration").assertExists()
    }

    @Test
    fun noteClick_invokesCallbackWithThatNote() {
        var clicked: Note? = null
        setContent(notes = sampleNotes, onNoteClick = { clicked = it })

        composeTestRule.onNodeWithText("Buy milk").performClick()

        assert(clicked == sampleNotes[0])
    }

    @Test
    fun addNoteClick_invokesCallback() {
        var clicked = false
        setContent(notes = emptyList(), onAddNote = { clicked = true })

        composeTestRule.onNodeWithContentDescription("Add note").performClick()

        assert(clicked)
    }

    @Test
    fun aboutMenuItemClick_invokesCallback() {
        var clicked = false
        setContent(notes = emptyList(), onAboutClick = { clicked = true })

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("About").performClick()

        assert(clicked)
    }

    @Test
    fun deleteAll_onEmptyList_doesNotShowConfirmationDialog() {
        setContent(notes = emptyList())

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete all notes").performClick()

        composeTestRule.onNodeWithText("Are you sure?").assertDoesNotExist()
    }

    @Test
    fun deleteAll_withNotes_confirmInvokesCallback() {
        var deleted = false
        setContent(notes = sampleNotes, onDeleteAllNotes = { deleted = true })

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete all notes").performClick()
        composeTestRule.onNodeWithText("Are you sure?").assertExists()
        composeTestRule.onNodeWithText("Yes").performClick()

        assert(deleted)
    }

    @Test
    fun deleteAll_withNotes_dismissDoesNotInvokeCallback() {
        var deleted = false
        setContent(notes = sampleNotes, onDeleteAllNotes = { deleted = true })

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete all notes").performClick()
        composeTestRule.onNodeWithText("No").performClick()

        assert(!deleted)
        composeTestRule.onNodeWithText("Are you sure?").assertDoesNotExist()
    }

    private fun setContent(
        notes: List<Note>,
        onNoteClick: (Note) -> Unit = {},
        onAddNote: () -> Unit = {},
        onDeleteAllNotes: () -> Unit = {},
        onAboutClick: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            MyNotesTheme {
                NotesScreen(
                    notes = notes,
                    onNoteClick = onNoteClick,
                    onAddNote = onAddNote,
                    onDeleteAllNotes = onDeleteAllNotes,
                    onAboutClick = onAboutClick,
                )
            }
        }
    }
}
