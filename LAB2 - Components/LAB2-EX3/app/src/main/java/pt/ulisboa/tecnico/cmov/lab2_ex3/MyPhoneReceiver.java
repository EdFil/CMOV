package pt.ulisboa.tecnico.cmov.lab2_ex3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by edgar on 02-03-2015.
 */
public class MyPhoneReceiver extends BroadcastReceiver {

    private static String TAG = MyPhoneReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Object extra = intent.getExtras().get(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if(extra != null)
            Log.d(TAG, extra.toString());
    }
}
