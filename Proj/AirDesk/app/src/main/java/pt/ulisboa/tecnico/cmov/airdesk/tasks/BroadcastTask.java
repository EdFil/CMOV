package pt.ulisboa.tecnico.cmov.airdesk.tasks;

import android.os.AsyncTask;

import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.AirDeskService;

public class BroadcastTask extends AsyncTask<Void, String, Void> {

    public static final String TAG = BroadcastTask.class.getSimpleName();
    private int mPort;
    private Class<AirDeskService> mServiceClass;
    private String[] mArguments;
    public AsyncResponse mDelegate = null;

    public BroadcastTask(int port, Class serviceClass, String... arguments) {
        mPort = port;
        mServiceClass = serviceClass;
        mArguments = arguments;
    }


    @Override
    protected Void doInBackground(Void... params) {
        // Get all peers in network
        List<User> usersInNetwork = UserManager.getInstance().getUsers();

        // Iterate all the devices
        for(int i = 0; i < usersInNetwork.size(); i++) {
            SimWifiP2pDevice device = usersInNetwork.get(i).getDevice();
            // Create a request task for every device
            if(device == null)
                continue;
            RequestTask task = new RequestTask(device.getVirtIp(), mPort, mServiceClass, mArguments);
            // Override the callback so we can process the result from the task
            task.mDelegate = new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    onProgressUpdate(output);
                }
            };
            // Execute the task
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mDelegate.processFinish(values[0]);
    }
}
