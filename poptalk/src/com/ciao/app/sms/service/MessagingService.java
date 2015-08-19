package com.ciao.app.sms.service;

/**
 * Created by rajat on 12/3/15.
 NOT IN USE
 */

import android.app.IntentService;
import android.content.Intent;

import com.ciao.app.sms.receiver.MessagingReceiver;

/**
 * This service is triggered internally only and is used to process incoming SMS and MMS messages
 * that the {@link com.example.android.smssample.receiver.MessagingReceiver} passes over. It's
 * preferable to handle these in a service in case there is significant work to do which may exceed
 * the time allowed in a receiver.
 */
public class MessagingService extends IntentService {
    private static final String TAG = "MessagingService";

    // These actions are for this app only and are used by MessagingReceiver to start this service
    public static final String ACTION_MY_RECEIVE_SMS = "com.example.android.smssample.RECEIVE_SMS";
    public static final String ACTION_MY_RECEIVE_MMS = "com.example.android.smssample.RECEIVE_MMS";

    public MessagingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String intentAction = intent.getAction();
            if (ACTION_MY_RECEIVE_SMS.equals(intentAction)) {
                // TODO: Handle incoming SMS

                // Ensure wakelock is released that was created by the WakefulBroadcastReceiver
                MessagingReceiver.completeWakefulIntent(intent);
            } else if (ACTION_MY_RECEIVE_MMS.equals(intentAction)) {
                // TODO: Handle incoming MMS

                // Ensure wakelock is released that was created by the WakefulBroadcastReceiver
                MessagingReceiver.completeWakefulIntent(intent);
            }
        }
    }
}
