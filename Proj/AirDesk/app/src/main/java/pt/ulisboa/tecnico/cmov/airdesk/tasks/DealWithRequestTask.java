package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.AirDeskService;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

/**
 * Created by edgar on 06-05-2015.
 */
public class DealWithRequestTask extends AsyncTask<SimWifiP2pSocket, String, Void> {

    public static final String TAG = DealWithRequestTask.class.getSimpleName();
    private Thread mTimeLimitThread;

    @Override
    protected void onPreExecute() {
        mTimeLimitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Constants.TIME_TO_DEAL_WITH_REQUESTS);
                    DealWithRequestTask.this.cancel(true);
                } catch (InterruptedException e) {
                    Log.i(TAG + "[THREAD]", "Interrupted");
                }
            }
        });
    }

    @Override
    protected Void doInBackground(SimWifiP2pSocket... params) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(params[0].getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(params[0].getOutputStream()));

            // Get the service info
            JSONObject serviceDto = new JSONObject(reader.readLine());
            Log.i(TAG, "Received: " + serviceDto.toString());
            String serviceName = serviceDto.getString(Constants.SERVICE_NAME);
            JSONArray arguments = serviceDto.getJSONArray(Constants.SERVICE_ARGUMENTS);


            //Instantiate the service class
            Class serviceClassName = Class.forName(serviceName);
            AirDeskService serviceClass = (AirDeskService) serviceClassName.newInstance();

            JSONObject response = serviceClass.execute(arguments);

            StringBuilder debugString = new StringBuilder();
            debugString.append(serviceClassName.getSimpleName());
            debugString.append("(");
            for(int i = 0; i < arguments.length(); i++) {
                debugString.append(arguments.get(i));
                if ((i + 1) != arguments.length())
                    debugString.append(",");
            }
            debugString.append(")");

            publishProgress(debugString.toString());

            writer.write(response.toString());
            Log.i(TAG, "Wrote: " + response.toString());
            writer.newLine();
            writer.flush();

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(WorkspaceManager.getInstance().getContext(), values[0], Toast.LENGTH_SHORT).show();
    }

}
