package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pt.ulisboa.tecnico.cmov.airdesk.DropBoxActivity;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

public class UploadFileTask extends AsyncTask<String, String, Void> {

    public static final String LOG_TAG = UploadFileTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... file) {

        String path = file[0];

        String [] tokens = path.split("/");
        int size = tokens.length;
        String fileName = tokens[size-1];
        String wsName = tokens[size-2];
        String userName = tokens[size-3];

        String dbPath = userName + "/" + wsName + "/" + fileName;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            DropboxAPI.Entry response = DropBoxActivity.mDBApi.putFileOverwrite(dbPath, fis,
                    fis.getChannel().size(), null);
            Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
        } catch (DropboxException e) {
            System.out.println("Upload went wrong: " + e);
            e.printStackTrace();
        }catch (Exception e) {
            System.out.println("Something went wrong: " + e);
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {e.printStackTrace();}
            }
        }
//            DropboxAPI.Entry existingEntry = mDBApi.metadata("/magnum-opus.txt", 1, null, false, null);
//            Log.i("DbExampleLog", "The file's rev is now: " + existingEntry.rev);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

       // Toast.makeText(mTextToEdit.getContext(), "File uploaded", Toast.LENGTH_SHORT).show();
    }
}
