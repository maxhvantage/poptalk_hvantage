package com.ciao.app.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

//This class used to get the total credit of the user logged in app
public class GetTotalCredit extends IntentService {
	private RequestBean mRequestBean = new RequestBean();
	private JSONObject jsonRequest,jsonResponse;
	
	public GetTotalCredit(){
		super("service");
	}

	public GetTotalCredit(String totalCredit) {
		super(totalCredit);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		try {
			 if (AppSharedPreference.getInstance(this).getIsConnectedToInternet()){
				jsonRequest = new JSONObject();
				jsonRequest.put("user_security", getString(R.string.user_security_key));
				jsonRequest.put("user_id", AppSharedPreference.getInstance(mRequestBean.getActivity()).getUserID());
				String response = NetworkCall.getInstance(getApplicationContext()).hitNetwork(AppNetworkConstants.GET_TOTAL_CREDIT, jsonRequest);
				jsonResponse = new JSONObject(response);
				String errorCode = jsonResponse.getString("error_code");
				if ("0".equals(errorCode)) {
					AppSharedPreference.getInstance(mRequestBean.getActivity()).setTotalCredit(jsonResponse.getJSONObject("result").getString("total_credit"));
				}
			}

		} catch (JSONException e) {
			Log.e("call_api","error on check credits");
		}

	}

}
