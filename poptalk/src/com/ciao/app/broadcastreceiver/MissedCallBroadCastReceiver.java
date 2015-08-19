package com.ciao.app.broadcastreceiver;

import com.ciao.app.apppreference.AppSharedPreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/*
 * This class is used to get the notification about miss call in the app.
 */
public class MissedCallBroadCastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		AppSharedPreference.getInstance(context).updateMissedCallCount();
		
	}

}
