package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.util.Log;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;
import com.poptalk.app.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajat on 9/2/15.
 * This class is used to process the verify the request
 */
public class AccountVerificationLoader extends AsyncTaskLoader<String> implements BaseLoader {
	private RequestBean mRequestBean;
	private NetworkCall mNetworkCall;
	private JSONObject jsonObject;
	private String mRespronse;
	private String verificationUrl;
	private List<NameValuePair> checkListParams;


	public AccountVerificationLoader(RequestBean mRequestBean,String verificationCode) {
		super(mRequestBean.getContext());
		this.mRequestBean = mRequestBean;
		this.mNetworkCall = NetworkCall.getInstance(mRequestBean.getContext());
		this.checkListParams = new ArrayList<NameValuePair>();
		this.checkListParams.add(new BasicNameValuePair("code",verificationCode));
		this.verificationUrl = AppNetworkConstants.CODE_VERIFICATION_URL;
	}

	@Override
	protected void onStartLoading() {
		if(mRespronse!=null){
			deliverResult(mRespronse);
		}
		if(mRespronse == null || takeContentChanged()){
			showLoaderDialog();
			forceLoad();
		}
	}
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		cancelLoad();
		hideLoaderDialog();
	}
	@Override
	public String loadInBackground() {
		mRespronse = NetworkCall.getInstance(mRequestBean.getActivity()).hitNetwork(checkListParams,verificationUrl);
		try {
			JSONObject serverResponseJsonObject = new JSONObject(mRespronse);
			String errorCode = serverResponseJsonObject.getString("status");
			if (errorCode.equalsIgnoreCase("0")) {
				jsonObject = new JSONObject();
				jsonObject.put("user_security", mRequestBean.getActivity().getString(R.string.user_security_key));
				jsonObject.put("user_device_token", AppSharedPreference.getInstance(mRequestBean.getActivity()).getDeivceToken());
				jsonObject.put("user_id", AppSharedPreference.getInstance(mRequestBean.getActivity()).getUserID());
				mRespronse =NetworkCall.getInstance(mRequestBean.getActivity()).hitNetwork(AppNetworkConstants.VERIFY_ACCOUNT,jsonObject);
			}else{
				return mRespronse;
			}
		} catch (JSONException e) {
		} 

		Log.e("Acct Verifiy response -", mRespronse);
		return mRespronse;

	}

	@Override
	public void showLoaderDialog() {
		if (mRequestBean.isLoader()) {
			mNetworkCall.showProgressDialog(mRequestBean.getActivity(), mRequestBean.getLoaderMessage(), false);
		}
	}

	@Override
	public void hideLoaderDialog() {
		if (mRequestBean.isLoader()) {
			mNetworkCall.dismissDialog();
		}
	}
}
