package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.GetUserService;

/**
 * Created by edgar on 06-05-2015.
 */
public class OutgoingCommTask extends AsyncTask<String, String, String> {

    public static final String TAG = OutgoingCommTask.class.getSimpleName();
    private SimWifiP2pSocket mCommunicationSocket;
    private String mIp;
    private int mPort;
    String fromServer;

    public OutgoingCommTask(String ip, int port) {
        mIp = ip;
        mPort = port;
    }


    @Override
    protected String doInBackground(String... params) {
        try {
            mCommunicationSocket = new SimWifiP2pSocket("192.168.0.2", mPort);
            BufferedReader reader = new BufferedReader(new InputStreamReader(mCommunicationSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(mCommunicationSocket.getOutputStream()));

            writer.write(GetUserService.class.getName());
            writer.newLine();
            writer.flush();

            String response = reader.readLine();

            publishProgress(response);

            writer.close();
            reader.close();

            mCommunicationSocket.close();
        } catch (UnknownHostException e) {
            return "Unknown Host:" + e.getMessage();
        } catch (IOException e) {
            return "IO error:" + e.getMessage();
        }
        return "END";
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(WorkspaceManager.getInstance().getContext(), values[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "Ended");
        Toast.makeText(WorkspaceManager.getInstance().getContext(), result, Toast.LENGTH_SHORT).show();
    }
}
