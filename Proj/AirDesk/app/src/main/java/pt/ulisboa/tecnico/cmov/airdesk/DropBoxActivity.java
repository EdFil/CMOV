package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pt.ulisboa.tecnico.cmov.airdesk.tasks.ReadFileTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;


public class DropBoxActivity extends ActionBarActivity {

    final static private String APP_KEY = "7fsxh18ga2xz85g";
    final static private String APP_SECRET = "4qz9af5jk0et3hj";

    private static final String TAG = DropBoxActivity.class.getSimpleName();

    private TextView fileName;
    private TextView fileContent;

    private SharedPreferences mSharedPreferences;
    public static DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox);

        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF_FILE, MODE_PRIVATE);

        fileName = (TextView) findViewById(R.id.fileName);
        fileContent = (TextView) findViewById(R.id.fileContent);

        Toast.makeText(this, "dropCreate", Toast.LENGTH_LONG).show();

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        //AndroidAuthSession session = new AndroidAuthSession(appKeys);
        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        if(!mSharedPreferences.contains(APP_KEY)) {
            mDBApi.getSession().startOAuth2Authentication(this);
        }

    }

    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mDBApi.getSession();

        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);

                Intent intent = new Intent(getApplicationContext(), LoadActivity.class);
                startActivity(intent);

            } catch (IllegalStateException e) {
                Toast.makeText(this, "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    private void loadAuth(AndroidAuthSession session) {
        String key = mSharedPreferences.getString(APP_KEY, null);
        String secret = mSharedPreferences.getString(APP_SECRET, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        session.setOAuth2AccessToken(secret);
    }

    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putString(APP_KEY, "oauth2:");
            edit.putString(APP_SECRET, oauth2AccessToken);
            edit.commit();
            return;
        }

    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);

        loadAuth(session);
        return session;
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

    public void copy(View view){
        File file = new File("working-draft.txt");
        new PutFileTask().execute(file.getName());

        Intent intent = new Intent(getApplicationContext(), LoadActivity.class);
        startActivity(intent);
    }

    public void download(View view){
        File file = new File("copy");
        new GetFileTask().execute(file.getName());
    }

    private class PutFileTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String path = params[0];
            FileInputStream fis = null;
            try {
                fis = openFileInput(path);
                DropboxAPI.Entry response = mDBApi.putFile("test.txt", fis,
                        fis.getChannel().size(), null, null);
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
    }

    private class GetFileTask extends AsyncTask<String, String, Void> {

        ReadFileTask task = new ReadFileTask();

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            FileOutputStream fos = null;
            try {
                File file = new File(name);
                fos = openFileOutput(name, Context.MODE_PRIVATE);
                DropboxAPI.DropboxFileInfo info = mDBApi.getFile("test.txt", null, fos, null);
                Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);

                //ReadFileTask task = new ReadFileTask();
                task.execute(file);
                String content = task.get();

                Log.d("FILE", content);
                fileName.setText(file.getName());
           //     fileContent.setText(info.getCharset());
            } catch (Exception e) {
                System.out.println("Download went wrong: " + e);
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {e.printStackTrace();}
                }
            }

//            DropboxAPI.Entry existingEntry = mDBApi.metadata("/magnum-opus.txt", 1, null, false, null);
//            Log.i("DbExampleLog", "The file's rev is now: " + existingEntry.rev);
            return null;
        }
    }

}
