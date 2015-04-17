package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import pt.ulisboa.tecnico.cmov.airdesk.R;

public class ReadFileTask extends AsyncTask<File, Integer, String> {

    public static final String LOG_TAG = ReadFileTask.class.getSimpleName();
    private static final String LINE_SEP = System.getProperty("line.separator");

    TextView mTextToView;

    public ReadFileTask(TextView textToView) {
        mTextToView = textToView;
    }

    @Override
    protected String doInBackground(File... file) {
        FileInputStream fis = null;
        Scanner scanner = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = new FileInputStream(file[0]);
            // scanner does mean one more object, but it's easier to work with
            scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine() + LINE_SEP);
            }
//            Toast.makeText(getApplicationContext(), "File read", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        mTextToView.setText(s);
    }
}
