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
        Log.w(TAG, intent.getExtras().get(TelephonyManager.EXTRA_INCOMING_NUMBER).toString());
        
    }
}
