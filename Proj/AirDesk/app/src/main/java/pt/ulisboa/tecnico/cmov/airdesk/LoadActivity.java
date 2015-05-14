package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;


public class LoadActivity extends Activity {

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //Initialize a LoadViewTask object and call the execute() method
        new LoadViewTask().execute();

    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            stagesLoader();
//            ringLoader();
        }

        private void stagesLoader() {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(LoadActivity.this);
            //Set the progress dialog to display a horizontal progress bar
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Loading...");
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(false);
            //This dialog isn't indeterminate
            progressDialog.setIndeterminate(false);
            //The maximum number of items is 3
            progressDialog.setMax(3);
            //Set the current progress to zero
            progressDialog.setProgress(0);
            //Display the progress dialog
            progressDialog.show();
        }

        public void ringLoader() {
            progressDialog = ProgressDialog.show(LoadActivity.this,"", "Loading...", false, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {

                // Load local workspaces
                WorkspaceManager.getInstance().loadLocalWorkspaces();

                // Load foreign workspaces of subscriptions
                UserManager.getInstance().loadForeignWorkspaces();

                //Get the current thread's token
                synchronized (this)
                {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;
                    //While the counter is smaller than four
                    while(counter <= 3)
                    {
                        //Wait 850 milliseconds
                        this.wait(1000);

                        // Set progress
                        counter++;

                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter);
                    }
                }
            }
            catch (InterruptedException e)
            {
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
