package com.aa.mynotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// The XML theme (res/values/styles.xml AppTheme) only defines a light palette, so the
// dark scheme here just carries the same brand colors over Material3's default dark surfaces.
private val LightColors = lightColorScheme(
    primary = ColorPrimary,
    onPrimary = Color.White,
    secondary = ColorAccent,
    onSecondary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = ColorPrimaryText,
    onSurface = ColorPrimaryText,
)

private val DarkColors = darkColorScheme(
    primary = ColorPrimary,
    onPrimary = Color.White,
    secondary = ColorAccent,
    onSecondary = Color.White,
)

@Composable
fun MyNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colorScheme, content = content)
}
