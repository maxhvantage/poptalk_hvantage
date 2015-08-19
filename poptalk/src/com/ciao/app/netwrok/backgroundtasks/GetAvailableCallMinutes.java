package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.datamodel.PopTalkRatesBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.utils.DialogUtils;
import com.ciao.app.views.fragments.CreditsFragment;
import com.poptalk.app.R;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
/*
 * This AsyntaskLoader is used to get the available call min for land line ,mobile and number of sms for selected country
 */
public class GetAvailableCallMinutes extends AsyncTask<Void, Void, Void>{
	private Context mContext;
	private String prefix;
	private Fragment mFragment;
	private PopTalkRatesBean popTalkRatesBean = null;


	public GetAvailableCallMinutes(Context mContext,Fragment mFragment,String prefix) {
		this.mContext = mContext;
		this.prefix = prefix;
        this.mFragment = mFragment;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("user_security", mContext.getString(R.string.user_security_key));
			jsonObject.put("prefix", prefix);
			String response = NetworkCall.getInstance(mContext).hitNetwork(AppNetworkConstants.GET_MINUTES, jsonObject);
			JSONObject responseJsonObject = new JSONObject(response);
			String errorCode = responseJsonObject.getString("error_code");
			if(errorCode.equalsIgnoreCase("0")){
				JSONObject resultJsonObject = responseJsonObject.getJSONObject("result");
				String smsRate = resultJsonObject.getString("sms_price");
				String mobileRate =resultJsonObject.getString("call_price");
				String landlineRate = resultJsonObject.getString("landline_price");
				popTalkRatesBean = new PopTalkRatesBean(smsRate, mobileRate, landlineRate);
			}
		} catch (JSONException e) {
		}

		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(mFragment instanceof CreditsFragment && popTalkRatesBean != null){
		  	((CreditsFragment)mFragment).setAvailableMinutes(popTalkRatesBean);
		}
	}

}
