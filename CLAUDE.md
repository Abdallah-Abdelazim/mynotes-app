# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

My Notes is a small, single-module offline Android note-taking app (package `com.aa.mynotes`). Published on Google Play and F-Droid. Licensed under GPLv3.

## Build & Test Commands

```bash
./gradlew build              # full build (what CI runs on push/PR to master)
./gradlew assembleDebug       # build debug APK only
./gradlew test                # run JVM unit tests (app/src/test)
./gradlew connectedAndroidTest # run instrumented tests on a device/emulator (app/src/androidTest)
./gradlew test --tests "com.aa.mynotes.ExampleUnitTest"  # run a single unit test class
```

CI (`.github/workflows/android.yml`) runs `./gradlew build` on JDK 17 for every push/PR to `master`. There is currently no unit test suite of substance (`ExampleUnitTest` is the unmodified template stub), but every screen has a Compose UI test under `app/src/androidTest/java/com/aa/mynotes/ui/` — run with `./gradlew connectedDebugAndroidTest` (needs an emulator/device).

## Architecture

The UI is 100% Jetpack Compose (Material3); there is no XML layout left in the app. Three screens, one shared data layer:

- **`activities/MainActivity`** (`ui/notes/NotesScreen.kt` + `ui/notes/NotesViewModel.kt`) — note list. `NotesViewModel` (an `AndroidViewModel`) exposes a `StateFlow<List<Note>>` built from a `callbackFlow` that registers a `ContentObserver` on `NotesProvider.CONTENT_URI` and re-queries on every change; the Activity just does `viewModel.notes.collectAsStateWithLifecycle()`. This means **any** insert/update/delete anywhere in the app refreshes the list automatically — there is no manual refresh call anywhere, and no `ActivityResult` plumbing between `MainActivity` and `EditorActivity`.
- **`activities/EditorActivity`** (`ui/editor/EditorScreen.kt`) — single screen for both creating and editing a note, driven by an `isNewNote` boolean set in `onCreate` based on whether a note `Uri` was passed in. Saving happens implicitly on exit (back press, up navigation, or process death via the composable's hoisted `mutableStateOf` text field state) via `finishEditing()`, not an explicit save button — an empty note is discarded/deleted rather than saved. Back navigation goes through `OnBackPressedDispatcher`/`OnBackPressedCallback`, not an `onBackPressed()` override — required since predictive back gestures (default from Android 16+/API 36+) bypass `onBackPressed()` entirely.
- **`activities/AboutActivity`** (`ui/about/AboutScreen.kt`) — static about screen; reads `BuildConfig.VERSION_NAME` and shares an app-description string via `ACTION_SEND`.
- **`data/NotesProvider`** — a `ContentProvider` (authority `com.aa.mynotes.notesprovider`, not exported) fronting the SQLite database. All CRUD goes through `getContentResolver()` + `NotesProvider.CONTENT_URI`, never directly through `DBOpenHelper`. Insert/update/delete call `getContentResolver().notifyChange(CONTENT_URI, null)` — required for `NotesViewModel`'s `ContentObserver` to fire; don't remove these when touching this file.
- **`data/DBOpenHelper`** — `SQLiteOpenHelper` defining the single `notes` table (`_id`, `noteText`, `noteLastChanged`). Column name constants here (`NOTE_ID`, `NOTE_TEXT`, `NOTE_LAST_CHANGED`, `ALL_COLUMNS`) are the source of truth used throughout.
- **`data/Note`** — the Kotlin data class (`id`, `text`, `lastChanged`) that `NotesViewModel` maps `Cursor` rows into; this is the only model type, used purely at the UI boundary.
- **`ui/theme/`** — a minimal hand-built Material3 theme (`Color.kt`, `Theme.kt`) matching the app's original green/red XML palette (`ColorPrimary`/`ColorAccent`/`ColorPrimaryText`). Not a full design system — extend it before inventing new colors ad hoc in a screen.

Every screen composable is stateless (takes data + callbacks, no direct `ContentResolver`/`ViewModel` access), which is what makes each one independently testable — see the `*ScreenTest.kt` files for the pattern (`createComposeRule()` from `androidx.compose.ui.test.junit4.v2`, not the deprecated v1 import).

## Notable Constraints

- **AndroidX, not the legacy Support Library**: the app was migrated off `com.android.support:*` to `androidx.appcompat`, `com.google.android.material`, `androidx.constraintlayout`, and Compose (`androidx.compose.*`).
- **AGP 9's built-in Kotlin support is used, with no `kotlin-android` plugin applied** — that plugin is incompatible with AGP 9's new DSL. All new UI code is Kotlin/Compose now; the only remaining Java is the data layer (`NotesProvider`, `DBOpenHelper`).
- **No dependency injection, no repository abstraction** — `NotesViewModel` talks to the `ContentProvider` directly via `AndroidViewModel`'s `Application` context. Keep new code consistent with this direct style rather than introducing new architectural layers unless asked.
- `minSdk 23`, `compileSdk`/`targetSdk 37`, Java 21 toolchain — tracked in `app/build.gradle`; `versionCode`/`versionName` there are bumped on release.
- **`adb shell input text` triggers false "click" events in Compose screens** — synthetic key-up events from adb's text injection can activate whatever `clickable`/`IconButton` currently holds keyboard focus (a real Compose keyboard-accessibility feature, not a bug), which can look like unexplained navigation when scripting UI checks over adb. Prefer the Compose testing framework (`performTextInput`/`performClick` in a `*ScreenTest.kt`) or manual testing over raw `adb shell input text` for anything beyond a quick visual check.
- Renovate (`renovate.json`) manages dependency update PRs against `master`.