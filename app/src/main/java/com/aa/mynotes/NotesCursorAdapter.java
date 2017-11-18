package com.aa.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Used instead of the SimpleCursorAdapter to add more functionality and customization.
 */
public class NotesCursorAdapter extends CursorAdapter {

    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
    }

    /**
     *
     * @param view
     * @param context
     * @param cursor points to the particular row that it's supposed to be displayed.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String noteText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));

        TextView noteTextView = view.findViewById(R.id.noteTextView);
        noteTextView.setText(noteText);
    }

}
