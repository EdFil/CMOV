package pt.ulisboa.tecnico.cmov.airdesk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WifiDirectManager;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {

    private final static String TAG = SimWifiP2pBroadcastReceiver.class.getSimpleName();
    private Context mContext;

    public SimWifiP2pBroadcastReceiver(Context context) {
        super();
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive(" + action + ")");
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {


            // This action is triggered when the WDSim service changes state:
            // - creating the service generates the WIFI_P2P_STATE_ENABLED event
            // - destroying the service generates the WIFI_P2P_STATE_DISABLED event

            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(mContext, "WiFi Direct enabled", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(mContext, "WiFi Direct disabled",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Toast.makeText(mContext, "Peer list changed",Toast.LENGTH_SHORT).show();
            WifiDirectManager.getInstance().refreshPeerList();
        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {
            WifiDirectManager.getInstance().refreshGroupInfo();

            Toast.makeText(mContext, "Network membership changed",
                    Toast.LENGTH_SHORT).show();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

//            SimWifiP2pInfo info = (SimWifiP2pInfo) intent.getSerializableExtra(
//                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
//            info.print();
            Toast.makeText(mContext, "Group ownership changed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

