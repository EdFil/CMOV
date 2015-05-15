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
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.GetUserService;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

/**
 * Created by edgar on 06-05-2015.
 */
public class DiscoverTask extends AsyncTask<Void, String, JSONObject> {

    public static final String TAG = DiscoverTask.class.getSimpleName();
    private String mIp;
    private SimWifiP2pDevice mDevice;
    private int mPort;
    private JSONObject mReponse;
    private String mServiceClass;
    private String[] mArguments;
    public AsyncResponse mDelegate = null;

    public DiscoverTask(SimWifiP2pDevice device, int port) {
        mDevice = device;
        mPort = port;
        mServiceClass = GetUserService.class.getName();
        mArguments = new String[0];
    }


    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            // Open sockets and readers
            SimWifiP2pSocket socket = new SimWifiP2pSocket(mDevice.getVirtIp(), mPort);
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
            Thread.sleep(new Random().nextInt(1000));

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
        User user = null;
        try {
            user = UserManager.getInstance().createUser(result);
            user.setDevice(mDevice);
            StringBuilder string = new StringBuilder();
            string.append("Users after discover:");
            for (User onlineUser : UserManager.getInstance().getUsers())
                string.append(onlineUser.toString());
            Log.i(TAG, string.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
