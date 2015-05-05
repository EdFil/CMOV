package pt.ulisboa.tecnico.cmov.airdesk.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;

/**
 * Created by edgar on 05-05-2015.
 */
public class WifiDirectManager {

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

    protected WifiDirectManager(Context context){
        mContext = context;
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
    }

    public Context getContext() { return mContext; }
    public boolean isWifiDirectOn() { return mBound; }

    public void turnOnWifiDirect() {
        if(!mBound) {
            Intent intent = new Intent(mContext, SimWifiP2pService.class);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }
    }

    public void turnOffWifiDirect() {
        if (mBound) {
            mContext.unbindService(mConnection);
            mBound = false;
        }
    }

}
