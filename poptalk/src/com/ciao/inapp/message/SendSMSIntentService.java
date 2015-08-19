package com.ciao.inapp.message;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.utils.AppUtils;
import com.poptalk.app.R;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

public class SendSMSIntentService extends IntentService{

	public static final int STATUS_RUNNING = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STATUS_ERROR = 2;
	private static final String TAG = "SendSMSIntentService";

	public SendSMSIntentService() {
		super(SendSMSIntentService.class.getName());

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e(TAG, "Service Started!");
		final ResultReceiver receiver = intent.getParcelableExtra("receiver");
		Bundle bundle = new Bundle();
			/* Update UI: Download Service is Running */
		receiver.send(STATUS_RUNNING, Bundle.EMPTY);
		try {
			if(NetworkStatus.isConected(this)){
				JSONObject jsonObject= new JSONObject();
				jsonObject.put("user_security", this.getString(R.string.user_security_key));
				jsonObject.put("user_id", AppSharedPreference.getInstance(this).getUserID());
				jsonObject.put("to", intent.getStringExtra("to"));
				jsonObject.put("from", intent.getStringExtra("from"));
				jsonObject.put("text", intent.getStringExtra("text"));
				String response = NetworkCall.getInstance(this).hitNetwork(AppNetworkConstants.SEND_MESSAGE, jsonObject);
				if(response!=null){
					JSONObject messageResponseJson = new JSONObject(response);
					JSONArray messageArray = messageResponseJson.getJSONArray("messages");
					String messageCountString = messageResponseJson.getString("message-count");
					JSONObject messageObject = messageArray.getJSONObject(0);
					String messageStatus = messageObject.getString("status");
					if(messageStatus.equalsIgnoreCase("0")){
						// Message sent successfully
						bundle.putString("result", messageCountString);
						receiver.send(STATUS_FINISHED, bundle);
					}else{
						// Error while sending  Message
						bundle.putString("result", "Message Failed");
						receiver.send(STATUS_ERROR, bundle);
					}
				}
			}else{
				AppUtils.showTost(this, "Please check your internet connection");
			}
		} catch (Exception e) {

				/* Sending error message back to activity */
			bundle.putString(Intent.EXTRA_TEXT, e.toString());
			receiver.send(STATUS_ERROR, bundle);
		}
		Log.d(TAG, "Service Stopping!");

	}

}
