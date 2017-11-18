package com.aa.mynotes;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class implements the Loader interface which execute the data operations on a background thread
 * automatically. And they elegantly handles changes in configuration such as orientation.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_EDITOR = 111;

    private CursorAdapter cursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditorForNewNote();
            }
        });

//        String[] from = {DBOpenHelper.NOTE_TEXT};
//        int[] to = {R.id.noteTextView};
//        cursorAdapter = new SimpleCursorAdapter(this, R.layout.note_list_item, null, from, to, 0);
        cursorAdapter = new NotesCursorAdapter(this, null, 0);  // used to add more customization the list items
        ListView notesListView = findViewById(R.id.notesListView);
        notesListView.setAdapter(cursorAdapter);

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                // the id argument is the primary key id of the SQL record represented in the clicked list item.
                /*
                * here's how it's working in the background:
                * The Content Provider when used with an SQLite database with a primary key column
                * requires the primary key column to have a name of "_id".
                * The Content Provider gets the value from that named column and passes it back
                * to the list. So by the time we get this value back, we know exactly
                * which item we want.
                * */
                Uri noteUri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, noteUri);
                startActivityForResult(intent, RC_EDITOR);
            }
        });

        getLoaderManager().initLoader(0, null,this);
    }

    private void openEditorForNewNote() {
        startActivityForResult(new Intent(this, EditorActivity.class), RC_EDITOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete_all:
                if (cursorAdapter.isEmpty()) {
                    Toast.makeText(this, R.string.no_notes_to_delete_message, Toast.LENGTH_SHORT).show();
                } else {
                    deleteAllNotes();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_EDITOR) {
            if (resultCode == RESULT_OK) {
                restartLoader();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            // clear the database
                            getContentResolver().delete(NotesProvider.CONTENT_URI, null, null);
                            restartLoader();

                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_notes_deleted_message),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    /**
     * Each time you change the data in the database you need to tell your Loader object that
     * it needs to restart, that it needs to reread the data from the backend database.
     */
    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }


    ///////////////////////////////////////////////////////////////////////////////
    // These method are implemented for LoaderCallbacks interface.               //
    // They are called automatically for you and you never call them yourself.   //
    // They all are callback methods.                                            //
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * This method is called whenever data is needed from the content provider.
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI, null, null, null, null);
    }

    /**
     * When you create the CursorLoader object it executes the query method on a background thread
     * and when the data comes back this method is called for you.
     * You job is to take the data represented by the Cursor object named data and pass it
     * to the CursorAdapter.
     * @param loader
     * @param data data represented by the Cursor object
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);

        // View or hide the no notes msg
        ViewGroup noNotesLinearLayout = findViewById(R.id.noNotesMsgLinearLayout);
        if (cursorAdapter.isEmpty()) {  // there's no notes in the database to view
            noNotesLinearLayout.setVisibility(View.VISIBLE);
        }
        else {   // there's indeed notes in the database to view
            noNotesLinearLayout.setVisibility(View.GONE);
        }
    }

    /**
     * The onLoaderReset method is called whenever the data needs to be wiped out.
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    /*---------------- Used for testing only -----------------*/
//    private void insertNote(String noteText) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DBOpenHelper.NOTE_TEXT, noteText);
//        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI, contentValues);
////        Log.d(TAG, "Inserted note: " + noteUri.getLastPathSegment());
//    }
//
//    private void insertSampleData() {
//        insertNote("Simple note.");
//        insertNote("Multi-line\nnote.");
//        insertNote("Very long note with a lot of text that exceeds the width of the screen.");
//
//        restartLoader();
//    }

}
