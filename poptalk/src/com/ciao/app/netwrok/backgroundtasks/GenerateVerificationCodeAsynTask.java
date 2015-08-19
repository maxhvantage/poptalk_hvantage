package com.ciao.app.netwrok.backgroundtasks;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.views.activities.RegistrationNextActivity;
import com.ciao.app.views.activities.VerifyNumberActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// not in use as per new flow of verification
public class GenerateVerificationCodeAsynTask extends AsyncTask<String, Void, String>{
	private Context context;
	private JSONObject jsonObject;
	private String verificationURL = null;
	private String response;

	public GenerateVerificationCodeAsynTask(Context context,RequestBean mRequestBean){
		this.context = context;
		this.verificationURL = AppNetworkConstants.SMS_NUMBER_VALIDATION;
		this.jsonObject = mRequestBean.getJsonObject();
	}

	@Override
	protected String doInBackground(String... params) {
		response = NetworkCall.getInstance(context).hitNetwork(verificationURL, jsonObject);
		String status = null;
		try {
			JSONObject jsonObject = new JSONObject(response);
			status = jsonObject.getString("error_code");
		} catch (JSONException e) {
			Log.e("response-sms-verification", e.getMessage());
		}
		return status;
	}

}
