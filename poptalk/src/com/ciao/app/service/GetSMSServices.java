package com.ciao.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


//This class is used for get all sms those are send to me and in pending state(SMS In - nexmo)
public class GetSMSServices extends IntentService{

	public GetSMSServices() {
		super("SMS");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(getSMSAsynTask!=null){
			if(getSMSAsynTask.getStatus()!=Status.RUNNING){
				getSMSAsynTask.execute();
			}
		}
	}

	private AsyncTask<Void, Void, Void> getSMSAsynTask = new AsyncTask<Void, Void, Void>() {

		@Override
		protected Void doInBackground(Void... params) {
			if (!"-1".equals(AppSharedPreference.getInstance(GetSMSServices.this).getUserID())) {
				try {
					String timeStamp = ApplicationDAO.getInstance(GetSMSServices.this).getLastSMSTime();
					if (timeStamp.length() > 10) {
						timeStamp = timeStamp.substring(0, timeStamp.length() - 4);
					}
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("user_security", "abchjds1256a12dasdsad67672");
					jsonObject.put("user_id", AppSharedPreference.getInstance(GetSMSServices.this).getUserID());
					jsonObject.put("last_timestamp", timeStamp);
					if(NetworkStatus.isConected(GetSMSServices.this)){
						String response = NetworkCall.getInstance(GetSMSServices.this).hitNetwork(AppNetworkConstants.GET_USER_SMS, jsonObject);
						JSONObject responseJsonObject = new JSONObject(response);
						String errorCode = responseJsonObject.getString("error_code");
						if(errorCode.equalsIgnoreCase("0")){
							JSONArray messageResultArray = responseJsonObject.getJSONArray("result");
							for(int i=0;i<messageResultArray.length();i++){
								JSONObject messageJsonObject = messageResultArray.getJSONObject(i);
								String message = messageJsonObject.getString("message");
								String senderId = messageJsonObject.getString("sender_number");
                                String messageTime = messageJsonObject.getString("message_timestamp");
                                long time = Long.parseLong(messageTime);
								ApplicationDAO.getInstance(GetSMSServices.this).saveSmsInDb(senderId, message, AppConstants.SMS_RECEIVED, AppConstants.SMS_RECEIVED, true,time);
								if(AppSharedPreference.getInstance(GetSMSServices.this).getSMSSceenVisbility()){
									AppUtils.playChatBubbleSound(GetSMSServices.this);
								}else{
									AppUtils.showSMSNotification(GetSMSServices.this, senderId, message, "", "");
								}
							}
						}
					}
				} catch (JSONException e) {
					// TODO: handle exception
				}

			}

			return null;
		}
	};

}
