package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONException;
import org.json.JSONObject;

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.datamodel.PopTalkRatesBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.views.activities.ContactDetailsActivity;
import com.ciao.app.views.activities.NewSmsActivity;
import com.ciao.app.views.fragments.KeypadFragment;
import com.poptalk.app.R;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
/*
 * This AsyntaskLoader is used to get call and sms rates  in background
 */
public class GetCallLimitAndCharge extends AsyncTask<Void, Void, Void>{
	private Context mContext;
	private String prefix;
	private Fragment mFragment;
	private String phoneNumber;
	private Activity activity;

	public GetCallLimitAndCharge(Context mContext, String prefix,Fragment mFragment,String phoneNumber) {
		super();
		this.mContext = mContext;
		this.prefix = prefix;
		this.mFragment = mFragment;
		this.phoneNumber = phoneNumber;
	}
	
	public GetCallLimitAndCharge(Context mContext, String prefix,Activity activity,String phoneNumber) {
		super();
		this.mContext = mContext;
		this.prefix = prefix;
		this.activity = activity;
		this.phoneNumber = phoneNumber;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("user_security", mContext.getString(R.string.user_security_key));
			jsonObject.put("prefix", prefix);
			jsonObject.put("phone_number", prefix+phoneNumber);
			String response = NetworkCall.getInstance(mContext).hitNetwork(AppNetworkConstants.GET_CALL_RATES_FOR_DAILED_NUMBER, jsonObject);
			JSONObject responseJsonObject = new JSONObject(response);
			String errorCode = responseJsonObject.getString("error_code");
			if(errorCode.equalsIgnoreCase("0")){
				JSONObject resultJsonObject = responseJsonObject.getJSONObject("result");
				String callRates =resultJsonObject.getString("price");
				String smsRates  = resultJsonObject.getString("sms_price");
				if(callRates!=null && callRates.length()>0){
					if(mFragment!=null){
						if(mFragment instanceof KeypadFragment){
							((KeypadFragment)mFragment).setCallLimitForDialedNumber(callRates);
						}	
					}else if (activity!=null) {
						if(activity instanceof ContactDetailsActivity){
							((ContactDetailsActivity)activity).setCallLimitForDialedNumber(callRates);	
						}
						if(activity instanceof NewSmsActivity){
							((NewSmsActivity)activity).setCallLimitForDialedNumber(callRates,smsRates);	
						}
					}
						
				}else if (smsRates!=null && smsRates.length()>0) {
					callRates = "0.0";
					if(mFragment!=null){
						if(mFragment instanceof KeypadFragment){
							((KeypadFragment)mFragment).setCallLimitForDialedNumber(callRates);
						}	
					}else if (activity!=null) {
						if(activity instanceof ContactDetailsActivity){
							((ContactDetailsActivity)activity).setCallLimitForDialedNumber(callRates);	
						}
						if(activity instanceof NewSmsActivity){
							((NewSmsActivity)activity).setCallLimitForDialedNumber(callRates,smsRates);	
						}
					}
				}
				
			}
		} catch (JSONException e) {
		}
		return null;
	}

}
