package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;

import com.dropbox.client2.exception.DropboxException;

import pt.ulisboa.tecnico.cmov.airdesk.DropBoxActivity;

public class DeleteFolderTask extends AsyncTask<String, String, Void> {

    public static final String LOG_TAG = DeleteFolderTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... file) {

        String path = file[0];

        String [] tokens = path.split("/");
        int size = tokens.length;
        String wsName = file[1];
        String userName = tokens[size-1];

        String dbPath = userName + "/" + wsName;

        try {

            DropBoxActivity.mDBApi.delete(dbPath);

        } catch (DropboxException e) {
            System.out.println("Delete folder went wrong: " + e);
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
