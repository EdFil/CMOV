package pt.ulisboa.tecnico.cmov.airdesk.manager;

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
import pt.ulisboa.tecnico.cmov.airdesk.receiver.SimWifiP2pBroadcastReceiver;
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

        turnOnWifiDirect();
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
}
