package com.idank_elishevaa.myruns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;


public class IgnoreReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // An incoming call is ringing, ignore it
                muteCall(context);
            }
        }
        if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
            muteCall(context);
        }
    }

    private void muteCall(Context context) {
        // Perform any desired actions when ignoring the call
        Log.d("CallReceiver", "Ignoring incoming call");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int adJustMute = AudioManager.ADJUST_MUTE;
            audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, adJustMute, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, adJustMute, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, adJustMute, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, adJustMute, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, adJustMute, 0);
            Log.d("IncomingCallReceiver", "Call muted");
        } else {
            Log.e("IncomingCallReceiver", "AudioManager is null");
        }
    }
}
