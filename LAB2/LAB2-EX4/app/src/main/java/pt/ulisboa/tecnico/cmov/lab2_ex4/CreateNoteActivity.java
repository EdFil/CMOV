package pt.ulisboa.tecnico.cmov.lab2_ex4;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
        Log.d(TAG, "onClickOk");
    }

    public void onClickCancel(View view){
        Log.d(TAG, "onClickCancel");
        super.onBackPressed();
    }


}
