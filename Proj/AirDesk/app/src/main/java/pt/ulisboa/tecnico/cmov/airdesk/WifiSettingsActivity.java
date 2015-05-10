package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.PeerListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WifiDirectManager;

public class WifiSettingsActivity extends Activity  {

    public static final String TAG = WifiSettingsActivity.class.getSimpleName();
    PeerListAdapter mPeerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the UI
        setContentView(R.layout.activity_wifi_settings);
        guiSetButtonListeners();

        guiUpdateInitState();
        if(WifiDirectManager.getInstance().isWifiDirectOn())
            guiUpdateDisconnectedState();

        mPeerList = new PeerListAdapter(this, WifiDirectManager.getInstance().getPeerList());
        ((ListView)findViewById(R.id.peerList)).setAdapter(mPeerList);
    }

	/*
     * Listeners associated to buttons
	 */

    private OnClickListener listenerWifiOnButton = new OnClickListener() {
        public void onClick(View v) {
            WifiDirectManager.getInstance().turnOnWifiDirect();
            guiUpdateDisconnectedState();
        }
    };

    private OnClickListener listenerWifiOffButton = new OnClickListener() {
        public void onClick(View v) {
            WifiDirectManager.getInstance().turnOffWifiDirect();
            guiUpdateInitState();
        }
    };

    private OnClickListener listenerInRangeButton = new OnClickListener() {
        public void onClick(View v) {
            WifiDirectManager.getInstance().refreshPeerList();
            mPeerList.notifyDataSetChanged();
        }
    };

    private OnClickListener listenerInGroupButton = new OnClickListener() {
        public void onClick(View v) {
            WifiDirectManager.getInstance().refreshGroupInfo();
        }
    };

    private OnClickListener listenerConnectButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //findViewById(R.id.idConnectButton).setEnabled(false);
//            String ip = "192.168.0.2";
//            int port = Integer.parseInt(getString(R.string.port));
//            final JSONObject response = null;
//            RequestTask task = new RequestTask(ip, port, response, GetUserService.class);
//            task.mDelegate = new AsyncResponse() {
//                @Override
//                public void processFinish(String output) {
//                    mPeerList
//                }
//            };
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private OnClickListener listenerDisconnectButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
//            findViewById(R.id.idDisconnectButton).setEnabled(false);
//            if (mCliSocket != null) {
//                try {
//                    mCliSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            mCliSocket = null;
//            guiUpdateDisconnectedState();
        }
    };

    private OnClickListener listenerSendButton = new OnClickListener() {
        @Override
        public void onClick(View v) {
//            findViewById(R.id.idSendButton).setEnabled(false);
//            try {
//                mCliSocket.getOutputStream().write( (mTextInput.getText().toString()+"\n").getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mTextInput.setText("");
//            findViewById(R.id.idSendButton).setEnabled(true);
//            findViewById(R.id.idDisconnectButton).setEnabled(true);
        }
    };

	/*
	 * Helper methods for updating the interface
	 */

    private void guiSetButtonListeners() {

        findViewById(R.id.idConnectButton).setOnClickListener(listenerConnectButton);
        findViewById(R.id.idDisconnectButton).setOnClickListener(listenerDisconnectButton);
        findViewById(R.id.idSendButton).setOnClickListener(listenerSendButton);
        findViewById(R.id.idWifiOnButton).setOnClickListener(listenerWifiOnButton);
        findViewById(R.id.idWifiOffButton).setOnClickListener(listenerWifiOffButton);
        findViewById(R.id.idInRangeButton).setOnClickListener(listenerInRangeButton);
        findViewById(R.id.idInGroupButton).setOnClickListener(listenerInGroupButton);
    }

    private void guiUpdateInitState() {
        findViewById(R.id.idConnectButton).setEnabled(false);
        findViewById(R.id.idDisconnectButton).setEnabled(false);
        findViewById(R.id.idSendButton).setEnabled(false);
        findViewById(R.id.idWifiOnButton).setEnabled(true);
        findViewById(R.id.idWifiOffButton).setEnabled(false);
        findViewById(R.id.idInRangeButton).setEnabled(false);
        findViewById(R.id.idInGroupButton).setEnabled(false);
    }

    private void guiUpdateDisconnectedState() {
        findViewById(R.id.idSendButton).setEnabled(false);
        findViewById(R.id.idConnectButton).setEnabled(true);
        findViewById(R.id.idDisconnectButton).setEnabled(false);
        findViewById(R.id.idWifiOnButton).setEnabled(false);
        findViewById(R.id.idWifiOffButton).setEnabled(true);
        findViewById(R.id.idInRangeButton).setEnabled(true);
        findViewById(R.id.idInGroupButton).setEnabled(true);
    }
}
