package com.ciao.app.netwrok.backgroundtasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.datamodel.BaseResponseBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.views.activities.BuyNumberActivity;
import com.ciao.app.views.activities.PurchasePhoneNumberActivity;
import com.poptalk.app.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/*
 * This AsyncTask is used to process the number buy request
 */
public class BuyNumberAsyncTask  extends AsyncTask<Void, Void, BaseResponseBean>{

	private ProgressDialog mDialog;
	private Context context;
	String url,number;
	private String isoCode="US";
	public BuyNumberAsyncTask(Context context,String number, String url)
	{
		this.context=context;
		this.url=url;
		this.number=number;
	}
	
	public BuyNumberAsyncTask(Context context,String number,String isoCode, String url)
	{
		this.context=context;
		this.url=url;
		this.number=number;
		this.isoCode = isoCode;
	}

	@Override
	protected void onPostExecute(BaseResponseBean responseBean)
	{
		super.onPostExecute(responseBean);
		if(mDialog != null && mDialog.isShowing())
		{
			mDialog.dismiss();
		}

		if(responseBean.getmErrorCode()==0){
			if(context instanceof BuyNumberActivity){
				((BuyNumberActivity)context).onSuccess(responseBean);
			}else if (context instanceof PurchasePhoneNumberActivity) {
				((PurchasePhoneNumberActivity)context).onSuccess(responseBean);
			}
			
		}else{
			if(context instanceof BuyNumberActivity){
				((BuyNumberActivity)context).error(responseBean.getmExceptionName());
			}else if (context instanceof PurchasePhoneNumberActivity) {
				((PurchasePhoneNumberActivity)context).error(responseBean.getmExceptionName());
			}
			
		}
	}

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		mDialog=new ProgressDialog(context);
		mDialog.setMessage("Please wait ...");
		mDialog.setCancelable(false);
		mDialog.show();
	}

	@Override
	protected BaseResponseBean doInBackground(Void... params)
	{
		BaseResponseBean baseResponse= NetworkCall.getInstance(context).numberBuy(url,number,isoCode,AppSharedPreference.getInstance(context).getUserID());

		Log.e("RegisterNumber", "Call has been made with baseResponse "+baseResponse.getmErrorCode());
		if(baseResponse.getmErrorCode() == 0){
			String registerOnAstrick  = NetworkCall.getInstance(context).registerNumberOnAteriskServer(number);
			Log.e("Register on Asterisk", registerOnAstrick);
			Log.e("RegisterNumber", "registered on Asterisk--"+registerOnAstrick);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("user_security", context.getString(R.string.user_security_key));
				jsonObject.put("user_id", AppSharedPreference.getInstance(context).getUserID());
				jsonObject.put("ciao_number", number);
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
			String response = NetworkCall.getInstance(context).hitNetwork(AppNetworkConstants.SAVE_CIAO_NUMBER_ON_SIGNUP, jsonObject);

			try {
				JSONObject serverResponseJsonObject = new JSONObject(response);
				String errorCode = serverResponseJsonObject.getString("error_code");
				if (errorCode.equalsIgnoreCase("0")) {
					AppSharedPreference.getInstance(context).setUserCiaoNumber(number);
					List<NameValuePair>  numberUpdateParam =new ArrayList<NameValuePair>();
					numberUpdateParam.add(new BasicNameValuePair("country", isoCode));
					numberUpdateParam.add(new BasicNameValuePair("msisdn", number));


					String numberUpdateResponse = NetworkCall.getInstance(context).hitNetwork(numberUpdateParam, AppNetworkConstants.UPDATE_NEXMO_NUMBER_URL);
					JSONObject numberUpdateResponseJsonObject = new JSONObject(numberUpdateResponse);
					Log.e("RegisterNumber", "Number update -"+numberUpdateResponse);
					String errCode = numberUpdateResponseJsonObject.getString("error-code");
					if(errCode.equalsIgnoreCase("200")){
						Log.e("Number detail updated - ", "----------");
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
		return baseResponse;
	}

}
