package com.aa.mynotes.ui.about

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aa.mynotes.ui.theme.MyNotesTheme
import org.junit.Rule
import org.junit.Test

class AboutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysVersionName() {
        setContent()

        composeTestRule.onNodeWithText("Version 1.5.1").assertExists()
    }

    @Test
    fun navigateUpClick_invokesCallback() {
        var navigatedUp = false
        setContent(onNavigateUp = { navigatedUp = true })

        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()

        assert(navigatedUp)
    }

    @Test
    fun githubButtonClick_invokesCallback() {
        var clicked = false
        setContent(onViewOnGithub = { clicked = true })

        composeTestRule.onNodeWithText("VIEW APP ON GITHUB").performClick()

        assert(clicked)
    }

    @Test
    fun shareFabClick_invokesCallback() {
        var clicked = false
        setContent(onShareApp = { clicked = true })

        composeTestRule.onNodeWithContentDescription("Share the app with a friend").performClick()

        assert(clicked)
    }

    private fun setContent(
        onNavigateUp: () -> Unit = {},
        onViewOnGithub: () -> Unit = {},
        onShareApp: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            MyNotesTheme {
                AboutScreen(
                    versionName = "1.5.1",
                    onNavigateUp = onNavigateUp,
                    onViewOnGithub = onViewOnGithub,
                    onShareApp = onShareApp,
                )
            }
        }
    }
}
