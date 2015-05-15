package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WifiDirectManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;


public class LoadActivity extends Activity {

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //Initialize a LoadViewTask object and call the execute() method
        new LoadAppTask().execute();

    }

    //To use the AsyncTask, it must be subclassed
    private class LoadAppTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            ringLoader();
        }

        public void ringLoader() {
            progressDialog = ProgressDialog.show(LoadActivity.this,"", "Loading...", false, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Load local workspaces and subscriptions
                WorkspaceManager.getInstance().loadLocalWorkspaces();
                UserManager.getInstance().loadSubscriptions();
                WifiDirectManager.getInstance().turnOnWifiDirect();

                //Get the current thread's token
                synchronized (this)
                {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;

                    for (int i = 0; i < 4; i++){
                        this.wait(850);
                        publishProgress(i);
                    }
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result)
        {
            progressDialog.dismiss();

            Intent intent = new Intent(getApplicationContext(), AirDeskActivity.class);
            startActivity(intent);
        }
    }
}
