package com.aa.mynotes.ui.about

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aa.mynotes.R
import com.aa.mynotes.ui.theme.ColorAccent
import com.aa.mynotes.ui.theme.ColorPrimaryText
import com.aa.mynotes.ui.theme.MyNotesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    versionName: String,
    onNavigateUp: () -> Unit,
    onViewOnGithub: () -> Unit,
    onShareApp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_about)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_navigate_up),
                            tint = Color.White,
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
                onClick = onShareApp,
                shape = CircleShape,
                containerColor = ColorAccent,
                contentColor = Color.White,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share_1),
                    contentDescription = stringResource(R.string.share_app_fab_content_description),
                )
            }
        },
    ) { contentPadding ->
        AboutContent(
            versionName = versionName,
            onViewOnGithub = onViewOnGithub,
            modifier = Modifier.padding(contentPadding),
        )
    }
}

@Composable
private fun AboutContent(versionName: String, onViewOnGithub: () -> Unit, modifier: Modifier = Modifier) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Box(modifier = modifier.fillMaxSize()) {
        if (isLandscape) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppLogo()
                    Spacer(modifier = Modifier.size(32.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AppDescription()
                        Spacer(modifier = Modifier.size(16.dp))
                        VersionText(versionName)
                    }
                }
                Spacer(modifier = Modifier.size(24.dp))
                GithubButton(onClick = onViewOnGithub)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(2f))
                AppLogo()
                Spacer(modifier = Modifier.weight(3f))
                AppDescription()
                Spacer(modifier = Modifier.size(16.dp))
                VersionText(versionName)
                Spacer(modifier = Modifier.weight(2f))
                GithubButton(onClick = onViewOnGithub)
                Spacer(modifier = Modifier.size(96.dp))
            }
        }
    }
}

@Composable
private fun AppLogo() {
    // app_logo.png is a raw 100x100 bitmap in the density-less drawable/ bucket. The
    // View system auto-scales that to a consistent 100dp; Compose's Image does not, so
    // it's sized explicitly here to match.
    Image(
        painter = painterResource(R.drawable.app_logo),
        contentDescription = null,
        modifier = Modifier.size(100.dp),
    )
}

@Composable
private fun AppDescription() {
    Text(
        text = stringResource(R.string.app_description),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun VersionText(versionName: String) {
    Text(text = stringResource(R.string.app_version_name, versionName))
}

@Composable
private fun GithubButton(onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color(0xFFE0E0E0),
            contentColor = ColorPrimaryText,
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_github_circle),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(stringResource(R.string.view_on_github_button_text).uppercase())
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    MyNotesTheme {
        AboutScreen(
            versionName = "1.5.1",
            onNavigateUp = {},
            onViewOnGithub = {},
            onShareApp = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 640, heightDp = 360)
@Composable
private fun AboutScreenLandscapePreview() {
    MyNotesTheme {
        AboutScreen(
            versionName = "1.5.1",
            onNavigateUp = {},
            onViewOnGithub = {},
            onShareApp = {},
        )
    }
}
