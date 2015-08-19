package com.ciao.app.netwrok.backgroundtasks;

import java.io.IOException;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.os.AsyncTask;
/*
 * This AsyntaskLoader is used to get the device token for push notification in background
 */
public class GetDeviceTokenInBackGroud extends AsyncTask<Void, Void, Void>{
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private GoogleCloudMessaging gcm;
	private String gcmRegId;
	private Context context;
	
	public GetDeviceTokenInBackGroud(Context context){
		this.context = context;
	}
	@Override
	protected Void doInBackground(Void... params) {
		String msg = "";
		gcm = GoogleCloudMessaging.getInstance(context);
		gcmRegId = AppSharedPreference.getInstance(context).getDeivceToken();
		if(gcmRegId.isEmpty()||(gcmRegId.equalsIgnoreCase("12345"))){
			while(gcmRegId.isEmpty()||(gcmRegId.equalsIgnoreCase("12345"))){
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					gcmRegId = gcm.register(AppConstants.GCM_SENDER_ID);
					AppSharedPreference.getInstance(context).setDeviceToken(gcmRegId);
					msg = "Device registered, registration ID=" + gcmRegId;
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
			}
		}
		
		return null;

	}
}
