package com.ciao.app.broadcastreceiver;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.utils.AppUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/*
 * This class is used get notify about last call duration.
 */
public class CallTimeBroadCastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent!=null){
			Bundle bundle =	intent.getExtras();
			long timeInMilisec = bundle.getLong("time");
			AppSharedPreference.getInstance(context).setLastCallDuration(timeInMilisec);
			
		}
		

	}

}
