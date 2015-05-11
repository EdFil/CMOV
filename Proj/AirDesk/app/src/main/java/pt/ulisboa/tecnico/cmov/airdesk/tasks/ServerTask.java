package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by edgar on 06-05-2015.
 */
public class ServerTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {

    public static final String TAG = ServerTask.class.getSimpleName();

    private SimWifiP2pSocketServer mServerSocket;
    private int mPort;

    public ServerTask(int port) {
        mPort = port;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            mServerSocket = new SimWifiP2pSocketServer(mPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Created server \"" + this.hashCode() + "\", at port " + mPort);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "Running server \"" + this.hashCode() + "\", at port " + mPort);
        while (!Thread.currentThread().isInterrupted()) {
            final SimWifiP2pSocket sock;
            try {
                SimWifiP2pSocket incommingSocket = mServerSocket.accept();
                if(incommingSocket != null)
                    new DealWithRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, incommingSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(final SimWifiP2pSocket... values) {
        Log.d(TAG, values.toString());


    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG, "Deleted server \"" + this.hashCode() + "\", at port " + mPort);
    }
}
