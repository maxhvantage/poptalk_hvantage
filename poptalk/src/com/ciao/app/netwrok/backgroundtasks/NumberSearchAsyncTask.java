package com.ciao.app.netwrok.backgroundtasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ciao.app.datamodel.NumberResponseBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.views.activities.BuyNumberActivity;
import com.ciao.app.views.activities.BuyNumberCountryWise;
import com.ciao.app.views.activities.SearchAndSelectNumber;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/*
 * This asyncTask is used to search nexmo numbers in background and update the number list from onPostexecute.
 */
public class NumberSearchAsyncTask  extends AsyncTask<Void, Void, NumberResponseBean>{

	private ProgressDialog mDialog;
	private Context context;
	String url;
	List<NameValuePair> params;
	public NumberSearchAsyncTask(Context context, String url)
	{
		this.context=context;
		this.url=url;
	}

	public NumberSearchAsyncTask(Context context, List<NameValuePair> params, String url) {
		this.context=context;
		this.params=params;
		this.url=url;
	}

	@Override
	protected void onPostExecute(NumberResponseBean responseBean)
	{
		super.onPostExecute(responseBean);
		if(mDialog != null && mDialog.isShowing())
		{
			mDialog.dismiss();
		}

		if(responseBean.getmErrorCode()==0){
			if (context instanceof SearchAndSelectNumber) {
				((SearchAndSelectNumber)context).success(responseBean);	
			}else if (context instanceof BuyNumberCountryWise) {
				((BuyNumberCountryWise)context).success(responseBean);	
			}


		}else{
			if (context instanceof SearchAndSelectNumber) {
				((SearchAndSelectNumber)context).error(responseBean.getmExceptionName());	
			}
			else if (context instanceof BuyNumberCountryWise) {
				((BuyNumberCountryWise)context).error(responseBean.getmExceptionName());	
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
	protected NumberResponseBean doInBackground(Void... params)
	{
		NumberResponseBean baseResponse = NetworkCall.getInstance(context).networkHitSearchNumber(this.params,url);
		return baseResponse;
	}

}
