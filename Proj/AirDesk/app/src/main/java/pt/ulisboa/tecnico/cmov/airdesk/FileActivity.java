package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;


public class FileActivity extends ActionBarActivity {

    public static final String LOG_TAG = FileActivity.class.getSimpleName();
    private static final String LINE_SEP = System.getProperty("line.separator");

    private TextView textToView;
    private ImageView edit;

    private EditText textToEdit;
    private ImageView save;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        textToView = (TextView) findViewById(R.id.textFile);
        edit = (ImageView) findViewById(R.id.editTextFile);

        textToEdit = (EditText) findViewById(R.id.editedTextFile);
        save = (ImageView) findViewById(R.id.saveTextFile);

        Intent intent = getIntent();
        file = (File) intent.getSerializableExtra("textFile");

        // Action bar back button e name
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(file.getName());

        Toast.makeText(getApplicationContext(), "Filename : " + file.getName(), Toast.LENGTH_SHORT).show();

        String fileText = read();
        textToView.setText(fileText);

    }


    public void onClickEdit(View view) {

        String fileText = read();
        textToEdit.setText(fileText);

        textToView.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);

        textToEdit.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
    }

    public void onClickSave(View view) {

        write();
        String fileText = read();
        textToView.setText(fileText);

        textToView.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);

        textToEdit.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void write() {

        FileOutputStream fos = null;
        try {
            // note that there are many modes you can use
            fos = new FileOutputStream(file);   
            fos.write(textToEdit.getText().toString().getBytes());

        Toast.makeText(getApplicationContext(), "File written", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IO problem", e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.d("FileExplorer", "Close error.");
            }
        }
    }

    private String read() {
        FileInputStream fis = null;
        Scanner scanner = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = new FileInputStream(file);
            // scanner does mean one more object, but it's easier to work with
            scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine() + LINE_SEP);
            }
            Toast.makeText(getApplicationContext(), "File read", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.d("LOG_TAG", "Close error.");
                }
            }
            if (scanner != null) {
                scanner.close();
            }
        }

        return sb.toString();
    }

}
