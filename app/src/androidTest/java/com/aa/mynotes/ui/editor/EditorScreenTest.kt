package com.aa.mynotes.ui.editor

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.aa.mynotes.ui.theme.MyNotesTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun newNote_hidesShareAndDeleteActions() {
        setContent(lastEditedDateText = null)

        composeTestRule.onNodeWithContentDescription("Share note").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Delete note").assertDoesNotExist()
    }

    @Test
    fun newNote_showsHintWhenEmpty() {
        setContent(noteText = "", lastEditedDateText = null)

        composeTestRule.onNodeWithText("Enter your note here").assertExists()
    }

    @Test
    fun existingNote_showsShareDeleteActionsAndLastEditedDate() {
        setContent(noteText = "Hello", lastEditedDateText = "Fri, 7 Aug 2026 03:24 AM")

        composeTestRule.onNodeWithContentDescription("Share note").assertExists()
        composeTestRule.onNodeWithContentDescription("Delete note").assertExists()
        composeTestRule.onNodeWithText("Fri, 7 Aug 2026 03:24 AM").assertExists()
    }

    @Test
    fun typingText_invokesOnNoteTextChange() {
        var latestText = ""
        setContent(noteText = "", onNoteTextChange = { latestText = it }, lastEditedDateText = null)

        composeTestRule.onNodeWithTag(NOTE_TEXT_FIELD_TAG).performTextInput("Hi")

        assertEquals("Hi", latestText)
    }

    @Test
    fun shareClick_invokesCallback() {
        var clicked = false
        setContent(noteText = "Hello", onShareNote = { clicked = true }, lastEditedDateText = "date")

        composeTestRule.onNodeWithContentDescription("Share note").performClick()

        assert(clicked)
    }

    @Test
    fun deleteClick_invokesCallback() {
        var clicked = false
        setContent(noteText = "Hello", onDeleteNote = { clicked = true }, lastEditedDateText = "date")

        composeTestRule.onNodeWithContentDescription("Delete note").performClick()

        assert(clicked)
    }

    @Test
    fun navigateUpClick_invokesCallback() {
        var navigatedUp = false
        setContent(onNavigateUp = { navigatedUp = true }, lastEditedDateText = null)

        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()

        assert(navigatedUp)
    }

    private fun setContent(
        title: String = "Note editor",
        noteText: String = "",
        onNoteTextChange: (String) -> Unit = {},
        lastEditedDateText: String?,
        onNavigateUp: () -> Unit = {},
        onShareNote: () -> Unit = {},
        onDeleteNote: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            MyNotesTheme {
                EditorScreen(
                    title = title,
                    noteText = noteText,
                    onNoteTextChange = onNoteTextChange,
                    lastEditedDateText = lastEditedDateText,
                    onNavigateUp = onNavigateUp,
                    onShareNote = onShareNote,
                    onDeleteNote = onDeleteNote,
                )
            }
        }
    }
}
