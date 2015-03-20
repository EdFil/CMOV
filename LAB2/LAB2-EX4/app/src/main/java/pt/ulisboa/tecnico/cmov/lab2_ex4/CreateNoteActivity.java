package pt.ulisboa.tecnico.cmov.lab2_ex4;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import pt.ulisboa.tecnico.cmov.lab2_ex4.note.Note;

/**
 * Created by edgar on 02-03-2015.
 */
public class CreateNoteActivity extends Activity {

    private final static String TAG = CreateNoteActivity.class.getSimpleName();
    private final static String SAVED_NOTE_TEXT = "We have saved a note for you";

    TextView mTitle;
    TextView mDescription;
    ImageView mImageView;
    Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mTitle = (TextView) findViewById(R.id.noteTitle);
        mDescription = (TextView) findViewById(R.id.noteDescription);
        mImageView = (ImageView) findViewById(R.id.imageView);

        if(savedInstanceState != null){
            Note note = savedInstanceState.getParcelable(Note.NOTE_MESSAGE);
            if(note != null){
                Toast.makeText(getApplicationContext(), SAVED_NOTE_TEXT, Toast.LENGTH_SHORT);
                mTitle.setText(note.getTitle());
                mDescription.setText(note.getDescription());
            }
        }
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            Note note = savedInstanceState.getParcelable(Note.NOTE_MESSAGE);
            if(note != null){
                Log.d(TAG, "Restored a note");
                mTitle.setText(note.getTitle());
                mDescription.setText(note.getDescription());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(Note.NOTE_MESSAGE, new Note(mTitle.getText().toString(), mDescription.getText().toString(), mBitmap));
        Log.d(TAG, "Saved a note");
    }


    public void onClickOk(View view){
        Intent intent = getIntent();
        intent.putExtra(Note.NOTE_MESSAGE, new Note(mTitle.getText().toString(), mDescription.getText().toString(), mBitmap));
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickCancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onLoadImage(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            try {
                Uri imageUri = data.getData();
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                mImageView.setImageBitmap(mBitmap);
            }catch(IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }



}
