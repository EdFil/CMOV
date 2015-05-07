package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.AirDeskService;

/**
 * Created by edgar on 06-05-2015.
 */
public class DealWithRequestTask extends AsyncTask<SimWifiP2pSocket, String, Void> {

    public static final String TAG = DealWithRequestTask.class.getSimpleName();

    @Override
    protected Void doInBackground(SimWifiP2pSocket... params) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(params[0].getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(params[0].getOutputStream()));

            String serviceName = reader.readLine();

            Class serviceClassName = Class.forName(serviceName);
            AirDeskService serviceClass = (AirDeskService) serviceClassName.newInstance();
            String response = serviceClass.execute();

            publishProgress("Executed: " + serviceClassName.getSimpleName());

            writer.write(response);
            Log.d(TAG, response);
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
        }
        Log.d(TAG, "Ended");
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(WorkspaceManager.getInstance().getContext(), values[0], Toast.LENGTH_SHORT).show();
    }

}
