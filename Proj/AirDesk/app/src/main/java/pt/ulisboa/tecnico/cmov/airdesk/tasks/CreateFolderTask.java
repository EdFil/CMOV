package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.FileInputStream;
import java.io.IOException;

import pt.ulisboa.tecnico.cmov.airdesk.DropBoxActivity;

public class CreateFolderTask extends AsyncTask<String, String, Void> {

    public static final String LOG_TAG = CreateFolderTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... file) {

        String wsName = file[1];
        String userName = file[0];

        String dbPath = userName + "/" + wsName;

        try {
            DropboxAPI.Entry response = DropBoxActivity.mDBApi.createFolder(dbPath);
            Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
        } catch (DropboxException e) {
            System.out.println("Create folder went wrong: " + e);
            e.printStackTrace();
        }catch (Exception e) {
            System.out.println("Something went wrong: " + e);
            e.printStackTrace();
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
