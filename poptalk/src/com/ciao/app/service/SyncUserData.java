package com.ciao.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;
import com.poptalk.app.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anderick on 7/20/15.
 */
public class SyncUserData extends IntentService {
    private JSONObject jsonRequest,jsonResponse;

    public SyncUserData(){
        super("service");
    }

    public SyncUserData(String totalCredit) {
        super(totalCredit);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            if (AppSharedPreference.getInstance(this).getIsConnectedToInternet()) {
                JSONObject jsonRequest, jsonResponse;
                jsonRequest = new JSONObject();
                jsonRequest.put("user_security", this.getString(R.string.user_security_key));
                jsonRequest.put("user_id", AppSharedPreference.getInstance(this).getUserID());
                jsonRequest.put("user_device_token", AppSharedPreference.getInstance(this).getDeivceToken());
                String response = NetworkCall.getInstance(this).hitNetwork(AppNetworkConstants.SYNC_DATA, jsonRequest);
                jsonResponse = new JSONObject(response);
                AppSharedPreference.getInstance(this).setCommUser(jsonResponse.getJSONObject("result").getString("comm_user"));
                AppSharedPreference.getInstance(this).setCommSecurity(jsonResponse.getJSONObject("result").getString("comm_security"));
            }
        } catch(Exception e) {
            Log.e("user_sync", "error to sync the user");
        }
    }

}
