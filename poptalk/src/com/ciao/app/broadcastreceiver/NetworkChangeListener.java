package com.ciao.app.broadcastreceiver;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.csipsimple.service.SipService;
import com.poptalk.app.R;
/*
 * This class is responsible for monitoring the network status for the app.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

public class NetworkChangeListener extends BroadcastReceiver {
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	public static final int NETWORK_STATUS_NOT_CONNECTED = 0,
			NETWORK_STAUS_WIFI = 1, NETWORK_STATUS_MOBILE = 2;

	@Override
	public void onReceive(Context context, Intent intent) {
		int conn = getConnectivityStatus(context);
		int status = 0;
		if (conn == TYPE_WIFI) {
			status = NETWORK_STAUS_WIFI;
		} else if (conn == TYPE_MOBILE) {
			status = NETWORK_STATUS_MOBILE;
		} else if (conn == TYPE_NOT_CONNECTED) {
			status = NETWORK_STATUS_NOT_CONNECTED;
		}

		Log.e("Sulod sa network reciever", "Sulod sa network reciever");
		if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			if (status == NETWORK_STATUS_NOT_CONNECTED) {
				context.stopService(new Intent(context, SipService.class));
			} else {
				context.startService(new Intent(context, SipService.class));

			}

		}

	}

/*
 * This method is used get the current network status 
 * @param - context
 * @return -int 
 */
	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}

}
