package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

/**
 * Created by edgar on 06-05-2015.
 */
public class OutgoingCommTask extends AsyncTask<String, Void, String> {

    public static final String TAG = OutgoingCommTask.class.getSimpleName();
    private SimWifiP2pSocket mOutgoingSocket;
    private int mPort;

    public OutgoingCommTask(int port) {
        mPort = port;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            mOutgoingSocket = new SimWifiP2pSocket(params[0], mPort);
        } catch (UnknownHostException e) {
            return "Unknown Host:" + e.getMessage();
        } catch (IOException e) {
            return "IO error:" + e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.e(TAG, result);
        }
        else {
            try {
                mOutgoingSocket.getOutputStream().write("Hello World".getBytes());
                mOutgoingSocket.getOutputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
