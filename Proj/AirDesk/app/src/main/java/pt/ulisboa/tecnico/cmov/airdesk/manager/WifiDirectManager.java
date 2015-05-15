package pt.ulisboa.tecnico.cmov.airdesk.manager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.subscription.Subscription;
import pt.ulisboa.tecnico.cmov.airdesk.receiver.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.airdesk.service.GetWorkspacesToMount;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.AsyncResponse;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.BroadcastTask;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.DiscoverTask;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.ServerTask;

/**
 * Created by edgar on 05-05-2015.
 */
public class WifiDirectManager implements PeerListListener, GroupInfoListener{

    // ------------------------
    //     Singleton Stuff
    // ------------------------

    public static final String TAG = WifiDirectManager.class.getSimpleName();
    private static WifiDirectManager mInstance;

    public static synchronized WifiDirectManager getInstance() {
        return mInstance;
    }

    public static synchronized void initWifiDirectManager(Context context) {
        mInstance = new WifiDirectManager(context);
    }

    // ------------------------
    //      Manager Stuff
    // ------------------------

    public interface NetworkUpdate {
        void onForeignWorkspaceUpdate();
    }

    private NetworkUpdate mCallBack;

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private Context mContext = null;
    private ServiceConnection mConnection;
    private ServerTask mServer;

    private List<SimWifiP2pDevice> mPeerList;

    protected WifiDirectManager(Context context){
        mContext = context;
        mPeerList = new ArrayList<>();
        mConnection = new ServiceConnection() {
            // callbacks for service binding, passed to bindService()

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                mService = new Messenger(service);
                mManager = new SimWifiP2pManager(mService);
                mChannel = mManager.initialize(mContext, Looper.getMainLooper(), null);
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mService = null;
                mManager = null;
                mChannel = null;
                mBound = false;
            }
        };

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(mContext);

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(mContext);
        mContext.registerReceiver(receiver, filter);

        int port = Integer.parseInt(mContext.getString(R.string.port));
        mServer = new ServerTask(port);
    }

    public Context getContext() { return mContext; }
    public boolean isWifiDirectOn() { return mBound; }

    public void turnOnWifiDirect() {
        if(!mBound) {
            Intent intent = new Intent(mContext, SimWifiP2pService.class);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
            mServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void turnOffWifiDirect() {
        if (mBound) {
            mContext.unbindService(mConnection);
            mServer.cancel(true);
            mBound = false;
        }
    }

    public void refreshPeerList() {
        if(mBound)
            mManager.requestPeers(mChannel, (PeerListListener) this);
    }

    public void refreshGroupInfo() {
        if(mBound)
            mManager.requestGroupInfo(mChannel, (GroupInfoListener) this);
    }

        /*
	 * Listeners associated to WDSim
	 */

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        mPeerList.clear();
        mPeerList.addAll(peers.getDeviceList());
        for(SimWifiP2pDevice device : peers.getDeviceList())
        Log.i(TAG, mPeerList.toString());
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        List<SimWifiP2pDevice> deviceList = new ArrayList<>(devices.getDeviceList());

        SimWifiP2pDevice myDevice = devices.getByName(groupInfo.getDeviceName());
        UserManager.getInstance().getOwner().setDevice(myDevice);
        deviceList.remove(myDevice);

        UserManager.getInstance().clearUserList();

        for (SimWifiP2pDevice device : deviceList) {
            DiscoverTask task = new DiscoverTask(device, Integer.parseInt(getContext().getString(R.string.port)));
            // Execute the task
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    public List<SimWifiP2pDevice> getPeerList() {
        return mPeerList;
    }

    public void setOnForeignWorkspaceUpdate(NetworkUpdate networkUpdate) {
        mCallBack = networkUpdate;
    }

    public void updateForeignWorkspaceList() {
        // Load foreign workspaces of subscriptions
        List<Subscription> subscriptionList =  UserManager.getInstance().getSubscriptionList();

        List<String> arguments = new ArrayList<>();
        arguments.add(UserManager.getInstance().getOwner().getEmail());

        for(Subscription subscription : subscriptionList)
            arguments.addAll(Arrays.asList(subscription.getTags()));

        // Get and convert to String[] all tags, to send as arguments of service (String...)
        String[] tags = new String[arguments.size()];
        arguments.toArray(tags);

        // Create the broadcast task to send the service to all connected peers
        BroadcastTask task = new BroadcastTask(
                Integer.parseInt(WifiDirectManager.getInstance().getContext().getString(R.string.port)),
                GetWorkspacesToMount.class,
                tags
        );

        // Override the callback so we can process the result from the task
        task.mDelegate = new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                // deals with the broadcast request for the workspaces with the respective tags
                WorkspaceManager.getInstance().mountForeignWorkspacesFromJSON(output);
                if(mCallBack != null)
                    mCallBack.onForeignWorkspaceUpdate();
//                mForeignWorkspaceListAdapter.notifyDataSetChanged();
            }
        };
        // Execute the broadcast task to request the workspaces with tags
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
