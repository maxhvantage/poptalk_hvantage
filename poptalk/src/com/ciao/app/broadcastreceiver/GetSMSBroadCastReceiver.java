package com.ciao.app.broadcastreceiver;

import com.ciao.app.service.GetSMSServices;
/*
 * This class is used to restart the service that fetch the ciao out sms after reboot of device.
 * 
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GetSMSBroadCastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent syncIntent = new Intent();
        syncIntent.setClass(context, GetSMSServices.class);
        startWakefulService(context, syncIntent);
    }
}