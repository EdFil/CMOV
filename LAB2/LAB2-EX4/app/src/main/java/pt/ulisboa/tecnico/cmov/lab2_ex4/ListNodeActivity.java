package pt.ulisboa.tecnico.cmov.lab2_ex4;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
    private static int CREATE_NOTE_CODE = 1;

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
        mNoteList.add(new Note("Note title 4", "Note description 4", BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher)));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_NOTE_CODE) {
            if(resultCode == RESULT_OK){
                Note note = data.getParcelableExtra(Note.NOTE_MESSAGE);
                mNoteArrayAdapter.add(note);
            }
            if (resultCode == RESULT_CANCELED) {
                //Empty
            }
        }
    }

    public void newNote(View view){
        startActivityForResult(new Intent(this, CreateNoteActivity.class), CREATE_NOTE_CODE);
        //startActivity();
    }
}
