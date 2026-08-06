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

CI (`.github/workflows/android.yml`) runs `./gradlew build` on JDK 17 for every push/PR to `master`. There is currently no linked test suite of substance — `ExampleUnitTest`/`ExampleInstrumentedTest` are the unmodified template stubs.

## Architecture

The app has three screens and one data layer, wired together with pre-Jetpack Android APIs:

- **`activities/MainActivity`** — note list. Uses the legacy `LoaderManager`/`CursorLoader` APIs (not `ViewModel`/`LiveData`/coroutines) to query notes asynchronously and feed a `CursorAdapter`. Deleting all notes and opening the editor both go through this loader-driven refresh cycle (`restartLoader()`), so any change to how notes are written must still trigger a loader restart (currently done via `startActivityForResult`/`onActivityResult` with `RESULT_OK`) or the list silently goes stale.
- **`activities/EditorActivity`** — single screen for both creating and editing a note, branching internally on `Intent.ACTION_INSERT` vs `Intent.ACTION_EDIT` (`action` field set in `onCreate` based on whether a note `Uri` was passed in). Saving happens implicitly on exit (back press or up navigation) via `finishEditing()`, not via an explicit save button — an empty note is discarded/deleted rather than saved.
- **`activities/AboutActivity`** — static about screen; reads `BuildConfig.VERSION_NAME` and shares an app-description string via `ACTION_SEND`.
- **`data/NotesProvider`** — a `ContentProvider` (authority `com.aa.mynotes.notesprovider`, not exported) fronting the SQLite database. All CRUD in the activities goes through `getContentResolver()` + `NotesProvider.CONTENT_URI`, never directly through `DBOpenHelper`.
- **`data/DBOpenHelper`** — `SQLiteOpenHelper` defining the single `notes` table (`_id`, `noteText`, `noteLastChanged`). Column name constants here (`NOTE_ID`, `NOTE_TEXT`, `NOTE_LAST_CHANGED`, `ALL_COLUMNS`) are the source of truth used throughout the activities and adapter — there is no separate model/entity class.
- **`adapters/NotesCursorAdapter`** — binds `Cursor` rows from the provider directly to `note_list_item` row views; no RecyclerView, no view model, no list transformation layer.

Data flow for any note mutation: Activity → `ContentResolver` → `NotesProvider` → `SQLiteDatabase` (raw `selection` strings built by string-concatenating `DBOpenHelper.NOTE_ID + "=" + id`, not parameterized `selectionArgs`) → `MainActivity`'s loader picks up the change on next restart.

## Notable Constraints

- **AndroidX, not the legacy Support Library**: the app was migrated off `com.android.support:*` to `androidx.appcompat`, `com.google.android.material`, and `androidx.constraintlayout`. `CursorAdapter` deliberately uses the plain framework `android.widget.CursorAdapter` (not an AndroidX wrapper — none exists; the framework class has been available since API 1).
- **AGP 9's built-in Kotlin support is used, with no `kotlin-android` plugin applied** — that plugin is incompatible with AGP 9's new DSL. There's no production Kotlin source yet; new files can be Kotlin without any extra plugin wiring.
- **No dependency injection, no view model layer, no repository abstraction** — activities talk to the `ContentProvider` directly. Keep new code consistent with this direct style rather than introducing new architectural layers unless asked.
- `minSdk 23`, `compileSdk`/`targetSdk 37`, Java 21 toolchain — tracked in `app/build.gradle`; `versionCode`/`versionName` there are bumped on release.
- `EditorActivity` intercepts back navigation via `OnBackPressedDispatcher`/`OnBackPressedCallback`, not an `onBackPressed()` override — required since predictive back gestures (default from Android 16+/API 36+) bypass `onBackPressed()` entirely.
- Renovate (`renovate.json`) manages dependency update PRs against `master`.