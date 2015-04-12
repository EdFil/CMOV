package pt.ulisboa.tecnico.cmov.lab2_ex4.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.lab2_ex4.R;
import pt.ulisboa.tecnico.cmov.lab2_ex4.note.Note;

/**
 * Created by edgar on 02-03-2015.
 */
public class NoteArrayAdapter extends ArrayAdapter<Note> {

    public NoteArrayAdapter(Context context, List<Note> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Note note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_note, parent, false);
        }
        // Lookup view for data population
        TextView noteTitle = (TextView) convertView.findViewById(R.id.noteTitle);
        // Populate the data into the template view using the data object
        noteTitle.setText(note.getTitle());
        // Return the completed view to render on screen
        return convertView;
    }
}
