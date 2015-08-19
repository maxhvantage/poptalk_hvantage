package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONException;
import org.json.JSONObject;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;

import android.content.Context;
import android.os.AsyncTask;
/*
 * This asyncTask is used to update the device token in background.
 */
public class UpdateDeviceTokenAsyncTask extends AsyncTask<Void, Void, Void>{

	private Context context;
	
	public UpdateDeviceTokenAsyncTask(Context context)
	{
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		JSONObject jsonRequest = new JSONObject();
		try {
			jsonRequest.put("user_security", context.getString(R.string.user_security_key));
			jsonRequest.put("user_device_token",AppSharedPreference.getInstance(context).getDeivceToken());
			jsonRequest.put("user_id",AppSharedPreference.getInstance(context).getUserID());
             
			String response = NetworkCall.getInstance(context).hitNetwork(AppNetworkConstants.UPDATE_DEVICE_TOKEN, jsonRequest);
			JSONObject jsonResponse = new JSONObject(response);
            String errorCode = jsonResponse.getString("error_code ");
            if(errorCode.equalsIgnoreCase("0"))
            {
            	
            }else{
            	
            }
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
