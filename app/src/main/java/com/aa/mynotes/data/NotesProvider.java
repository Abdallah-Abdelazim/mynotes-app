package com.aa.mynotes.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class NotesProvider extends ContentProvider {

    /**
     * Globally unique string that identifies the content provider to the Android Framework.
     * Only one app on any particular device can use any particular authority.
     * If you tried to install this app on a device that already has an app that has
     * registered this authority, the installation will fail.
     * To make sure that it's globally unique you typically start with the app package
     * and finish with the name of the provider class but make it all lower-case.
     */
    private static final String AUTHORITY = "com.aa.mynotes.notesprovider";
    /**
     * This represents the entire data set. This database has only one table so the name of
     * the path is the name of that table.
     */
    private static final String BASE_PATH = "notes";
    /**
     * A Uniform Resource Identifier that identifies the content provider.
     * It includes the authority and the base path.
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested query operation
    private static final int NOTES = 1;
    private static final int NOTE_ID = 2;

    /**
     * the purpose of the UriMatcher is to parse a URI and then tell you which operation has been requested.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";
    
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTE_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper DBHelper = new DBOpenHelper(getContext());
        database = DBHelper.getWritableDatabase();
        return true;
    }

    /**
     *
     * @param uri
     * @param projection
     * @param selection The where clause to filter he data (specify one or set it to null to get all data in a table)
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        if (uriMatcher.match(uri) == NOTE_ID) {
            // uri references specific record
            // edit the selection to return the note referenced by the give URI
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_NOTES,
                DBOpenHelper.ALL_COLUMNS,
                selection,
                null,
                null,
                null,
                DBOpenHelper.NOTE_LAST_CHANGED + " DESC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     *
     * @param uri
     * @param values key-value pair
     *               (similar to Bundle class but the Bundle class tends to be used to
     *               manage the user interface while the ContentValues class is used data
     *               around on the backend)
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_NOTES, null, values);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    /**
     *
     * @param uri
     * @param selection (whereClause)
     * @param selectionArgs (whereArgs)
     * @return number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    /**
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return number of rows updated
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_NOTES, values, selection, selectionArgs);
    }

}
