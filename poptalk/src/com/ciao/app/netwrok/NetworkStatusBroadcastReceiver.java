package com.ciao.app.netwrok;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.netwrok.backgroundtasks.GetDeviceTokenInBackGroud;
import com.ciao.app.service.GetTotalCredit;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.SplashActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.widget.Toast;

public class NetworkStatusBroadcastReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {

		ConnectivityManager coManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

		if((coManager.getNetworkInfo(coManager.TYPE_WIFI) != null && coManager.getNetworkInfo(coManager.TYPE_WIFI).isConnected())
				||(coManager.getNetworkInfo(coManager.TYPE_MOBILE) != null && coManager.getNetworkInfo(coManager.TYPE_MOBILE).isConnected()))
		{
			if (AppSharedPreference.getInstance(context).getUserID() != null) {
				context.startService(new Intent(context, XMPPChatService.class));
				context.startService(new Intent(context, GetTotalCredit.class));
			}
			registerGCMInBackground(context);
			/*AppUtils.smsCallPrices(context);*/
			AppSharedPreference.getInstance(context).setIsConnectedToInternet(true);
			
		}else{
			AppSharedPreference.getInstance(context).setIsConnectedToInternet(false);

		}
	}
	
	private void registerGCMInBackground(final Context context){
		new GetDeviceTokenInBackGroud(context).execute();
	}

}
