package com.idank_elishevaa.myruns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class IgnoreReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Check the action of the received intent
        String action = intent.getAction();
        if (action != null) {

            // stopping both calls and sms
            switch (action) {
                // if a call came in
                case TelephonyManager.ACTION_PHONE_STATE_CHANGED:
                    String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                    // Incoming call is ringing, reject the call
                    if (phoneState != null && phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        abortBroadcast();
                    }
                    break;

                // got an sms
                case "android.provider.Telephony.SMS_RECEIVED":
                    abortBroadcast();
                    break;
            }
        }

    }

}
