package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.os.Messenger;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WifiDirectManager;

/**
 * Created by edgar on 13-04-2015.
 */
public class GlobalState extends Application implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    public static final String TAG = GlobalState.class.getSimpleName();
    private SimWifiP2pManager mManager = null;
    private ServiceConnection mConnection;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;

    @Override
    public void onCreate() {
        super.onCreate();

        WorkspaceManager.getInstance().initWorkspaceManager(this);
        UserManager.getInstance().initUserManager(this);
        WifiDirectManager.getInstance().initWifiDirectManager(this);
//        Log.d(TAG, "Starting");
//        mConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName className, IBinder service) {
//                Log.d(TAG, "onServiceConnected");
//                mService = new Messenger(service);
//                mManager = new SimWifiP2pManager(mService);
//                mChannel = mManager.initialize(GlobalState.this,   getMainLooper(), null);
//                mBound = true;
//            }
//            @Override
//            public void onServiceDisconnected(ComponentName arg0) {
//                Log.d(TAG, "onServiceDisconnected");
//                mService = null;
//                mManager = null;
//                mChannel = null;
//                mBound = false;
//            }
//        };
//
//        SimWifiP2pSocketManager.Init(this);
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
//        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
//        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
//        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
//        SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(this);
//        registerReceiver(receiver, filter);
//
//        Intent intent = new Intent(this, SimWifiP2pService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    public void reloadManagers() {
        WorkspaceManager.getInstance().refreshWorkspaceLists();
//        UserManager.getInstance().refreshUserList();
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        // compile list of network members
        StringBuilder peersStr = new StringBuilder();
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null) ? "??" : device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
        }

        // display list of network members
        new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Network")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
        }

        // display list of devices in range
        new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

}
