package pt.ulisboa.tecnico.cmov.lab2_ex4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.lab2_ex4.adapter.NoteArrayAdapter;
import pt.ulisboa.tecnico.cmov.lab2_ex4.note.Note;


public class ListNodeActivity extends Activity {

    private static String TAG = ListNodeActivity.class.getSimpleName();

    ArrayList<Note> mNoteList;
    NoteArrayAdapter mNoteArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNoteList = new ArrayList<Note>();
        mNoteList.add(new Note("Note title 1", "Note description 1"));
        mNoteList.add(new Note("Note title 2", "Note description 2"));
        mNoteList.add(new Note("Note title 3", "Note description 3"));
        mNoteList.add(new Note("Note title 4", "Note description 4"));

        mNoteArrayAdapter = new NoteArrayAdapter(this, mNoteList);

        ListView listView = (ListView) findViewById(R.id.listNotes);
        listView.setAdapter(mNoteArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ReadNodeActivity.class);
                intent.putExtra(Note.NOTE_MESSAGE, (Note)parent.getItemAtPosition(position));
                startActivity(intent);
            }
        });
    }

    public void newNote(View view){
        startActivity(new Intent(this, CreateNoteActivity.class));

    }
}
