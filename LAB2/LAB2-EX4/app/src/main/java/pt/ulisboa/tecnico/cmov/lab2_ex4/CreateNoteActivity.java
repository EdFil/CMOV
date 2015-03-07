package pt.ulisboa.tecnico.cmov.lab2_ex4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.lab2_ex4.note.Note;

/**
 * Created by edgar on 02-03-2015.
 */
public class CreateNoteActivity extends Activity {

    private final static String TAG = CreateNoteActivity.class.getSimpleName();

    TextView mTitle;
    TextView mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mTitle = (TextView) findViewById(R.id.noteTitle);
        mDescription = (TextView) findViewById(R.id.noteDescription);
    }

    public void onClickOk(View view){
        Intent intent = getIntent();
        intent.putExtra(Note.NOTE_MESSAGE, new Note(mTitle.getText().toString(), mDescription.getText().toString()));
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickCancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }


}
