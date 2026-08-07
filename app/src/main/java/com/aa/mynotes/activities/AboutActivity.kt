package com.aa.mynotes.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aa.mynotes.BuildConfig
import com.aa.mynotes.R
import com.aa.mynotes.ui.about.AboutScreen
import com.aa.mynotes.ui.theme.MyNotesTheme

class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyNotesTheme {
                AboutScreen(
                    versionName = BuildConfig.VERSION_NAME,
                    onNavigateUp = { finish() },
                    onViewOnGithub = { openWebPage(getString(R.string.github_repo_url)) },
                    onShareApp = { shareApp() },
                )
            }
        }
    }

    private fun openWebPage(url: String) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    /** Shares a small description of the app with a link to install it. */
    private fun shareApp() {
        val appName = getString(R.string.app_name)
        val appDescription = getString(R.string.app_description)
        val playstoreUrl = getString(R.string.playstore_url)

        val sharedSubject = getString(R.string.share_app_subject, appName)
        val sharedText = getString(R.string.share_app_text, appName, appDescription, playstoreUrl)

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, sharedSubject)
            putExtra(Intent.EXTRA_TEXT, sharedText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_app_chooser_dialog_title)))
    }
}
