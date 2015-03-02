package pt.ulisboa.tecnico.cmov.lab2_ex4;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.lab2_ex4.note.Note;

/**
 * Created by edgar on 02-03-2015.
 */
public class ReadNodeActivity extends Activity {

    private final static String TAG = ReadNodeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        Intent intent = getIntent();

        Note note = intent.getParcelableExtra(Note.NOTE_MESSAGE);

        TextView textView = (TextView) findViewById(R.id.noteTitle);
        TextView textView2 = (TextView) findViewById(R.id.noteDescription);

        textView.setText(note.getTitle());
        textView2.setText(note.getDescription());
    }

}
