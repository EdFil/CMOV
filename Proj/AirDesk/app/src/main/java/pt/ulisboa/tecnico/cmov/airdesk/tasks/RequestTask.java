package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.Random;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

/**
 * Created by edgar on 06-05-2015.
 */
public class RequestTask extends AsyncTask<Void, String, JSONObject> {

    public static final String TAG = RequestTask.class.getSimpleName();
    private String mIp;
    private SimWifiP2pDevice mDevice;
    private int mPort;
    private JSONObject mReponse;
    private String mServiceClass;
    private String[] mArguments;
    public AsyncResponse mDelegate = null;
    private Thread mTimeLimitThread;

    public RequestTask(String ip, int port, Class serviceClass, String... arguments) {
        mIp = ip;
        mPort = port;
        mServiceClass = serviceClass.getName();
        mArguments = arguments;
    }

    @Override
    protected void onPreExecute() {
        mTimeLimitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Constants.TIME_TO_DEAL_WITH_REQUESTS);
                    Log.i(TAG + "[THREAD]", "Canceling");
                    RequestTask.this.cancel(true);
                } catch (InterruptedException e) {
                    Log.i(TAG + "[THREAD]", "Interrupted");
                }
            }
        });
        //mTimeLimitThread.run();
    }


    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            // Open sockets and readers
            SimWifiP2pSocket socket = new SimWifiP2pSocket(mIp, mPort);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Create dto to send the service info
            JSONObject dto = new JSONObject();
            dto.put(Constants.SERVICE_NAME, mServiceClass);
            JSONArray arguments = new JSONArray();
            for(String string : mArguments)
                arguments.put(string);
            dto.put(Constants.SERVICE_ARGUMENTS, arguments);

            // Send dto to server
            writer.write(dto.toString());
            Log.i(TAG, "Wrote: " + dto.toString());
            writer.newLine();
            writer.flush();

            // Wait for the server's response
            JSONObject serverResponse = new JSONObject(reader.readLine());
            Log.i(TAG, "Received: " + serverResponse.toString());

            // Close everything
            writer.close();
            reader.close();
            socket.close();

            // To simulate delays in the network
            Thread.sleep(new Random().nextInt(Constants.REQUEST_DELAY));

            // Return the request response
            return serverResponse;
        } catch (UnknownHostException e) {
            return makeJsonErrorMessage("Unknown Host:" + e.getMessage());
        } catch (IOException e) {
            return makeJsonErrorMessage("IO error:" + e.getMessage());
        } catch (JSONException e) {
            return makeJsonErrorMessage("JSON error:" + e.getMessage());
        } catch (InterruptedException e) {
            return makeJsonErrorMessage("Interrupeted:" + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if(mDelegate != null)
            mDelegate.processFinish(result.toString());
    }

    private JSONObject makeJsonErrorMessage(String errorMessage) {
        try {
            return new JSONObject().put("error", errorMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
