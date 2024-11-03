package com.aa.mynotes.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aa.mynotes.R;
import com.aa.mynotes.data.DBOpenHelper;
import com.aa.mynotes.data.NotesProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity {

    private static final String TAG = EditorActivity.class.getSimpleName();

    private String action; // to know which action this editor is doing (whether insert or update)
    private EditText editor;
    ViewGroup bottomBar;
    private String noteFilter;  // the where clause used in SQL statements to query a specific note
    private String oldText; // the existing text of a note before displaying it to the user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = findViewById(R.id.noteEditText);
        bottomBar = findViewById(R.id.bottomBar);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
        if (uri == null) {  // opened for new note

            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.editor_title_insert_new_note));

            editor.requestFocus();
            bottomBar.setVisibility(View.GONE);  // completely hide the bottomBar and all its contents

        } else {   // opened for edit note

            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.editor_title_edit_note));
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);  // disable keyboard popup automatically

            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            if (cursor.moveToFirst()) {
                // cursor is not empty
                oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
                String lastChanged = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_LAST_CHANGED));

                // display the note text
                editor.setText(oldText);

                // display last edited date in the bottom bar
                SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat dateFormatter;
                if (android.text.format.DateFormat.is24HourFormat(this)) {  // initialize the date formatter either in 12 or 24 hour time format
                    dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");    // 24 hour time format
                } else {
                    dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm a");  // AM/PM time format
                }
                TextView lastEditedDateTextView = findViewById(R.id.lastEditedDateTextView);
                try {
                    lastEditedDateTextView.setText(dateFormatter.format(dateParser.parse(lastChanged)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
            cursor.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { // back button pressed
            finishEditing();
            return true;
        } else if (id == R.id.action_delete_note) {
            deleteNote();
            return true;
        } else if (id == R.id.action_share_note) {
            shareNote();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void shareNote() {
        Intent shareNoteIntent = new Intent();
        shareNoteIntent.setAction(Intent.ACTION_SEND);
        shareNoteIntent.putExtra(Intent.EXTRA_TEXT, oldText);
        shareNoteIntent.setType("text/plain");
        String chooserDialogTitle = getString(R.string.share_note_chooser_dialog_title);
        startActivity(Intent.createChooser(shareNoteIntent, chooserDialogTitle));
    }

    private void finishEditing() {
        String noteText = editor.getText().toString().trim(); // trim here removes any leading or following white spaces.

        switch (action) {
            case Intent.ACTION_INSERT:
                if (noteText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(noteText);
                    setResult(RESULT_OK);
                }
                break;
            case Intent.ACTION_EDIT:
                if (noteText.length() == 0) {
                    deleteNote();
                } else if (noteText.equals(oldText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(noteText);
                    setResult(RESULT_OK);
                }
                break;
        }
        finish();
    }

    private void updateNote(String newNoteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newNoteText);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        Date date = new Date();
        values.put(DBOpenHelper.NOTE_LAST_CHANGED, dateFormat.format(date));
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, R.string.note_updated_message, Toast.LENGTH_SHORT).show();
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, R.string.note_deleted_message, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
