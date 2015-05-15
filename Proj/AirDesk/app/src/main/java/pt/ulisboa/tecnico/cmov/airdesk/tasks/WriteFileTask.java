package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteFileTask extends AsyncTask<File, Void, Void> {

    public static final String LOG_TAG = WriteFileTask.class.getSimpleName();

    String mContent;

    public WriteFileTask(String content) {
        mContent = content;
    }

    @Override
    protected Void doInBackground(File... file) {

        FileOutputStream fos = null;
        try {
            // note that there are many modes you can use
            fos = new FileOutputStream(file[0]);
            fos.write(mContent.getBytes());

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
        return null;
    }
}
