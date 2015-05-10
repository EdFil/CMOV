package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class DropBoxActivity extends ActionBarActivity {

    final static private String APP_KEY = "7fsxh18ga2xz85g";
    final static private String APP_SECRET = "4qz9af5jk0et3hj";

    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);

        Toast.makeText(this, "dropCreate", Toast.LENGTH_LONG).show();

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        mDBApi.getSession().startOAuth2Authentication(this);
    }

    protected void onResume() {
        super.onResume();
        FileOutputStream fos = null;
        FileInputStream fis = null;
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                Toast.makeText(this, "dropResume", Toast.LENGTH_SHORT).show();

                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();

                String text = "Text Test";
                fos = openFileOutput("working-draft.txt", Context.MODE_PRIVATE);
                fos.write(text.toString().getBytes());

                fis = openFileInput("working-draft.txt");

                new PutFileTask().execute(fis);

                Intent intent = new Intent(getApplicationContext(), AirDeskActivity.class);
                startActivity(intent);

            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                    fis.close();
                } catch (IOException e) {
                    Log.d("DROP BOX ACTIVIY", "Close error.");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dropbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PutFileTask extends AsyncTask<FileInputStream, FileInputStream, Void> {

        @Override
        protected Void doInBackground(FileInputStream... params) {
            FileInputStream fis = params[0];
            DropboxAPI.Entry response = null;
            try {
                response = mDBApi.putFile("/magnum-opus.txt", fis, fis.getChannel().size(), null, null);
                Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
            } catch (DropboxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


//            DropboxAPI.Entry existingEntry = mDBApi.metadata("/magnum-opus.txt", 1, null, false, null);
//            Log.i("DbExampleLog", "The file's rev is now: " + existingEntry.rev);
            return null;
        }
    }
}
