package com.ciao.app.netwrok.backgroundtasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.ciao.app.views.activities.CheckinActivity;

import org.json.JSONObject;

import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.fragment.FragmentCheckin;
import in.lockerapplication.networkcall.NetworkCall;

public class ChekinAsyncTask extends AsyncTask<Void, Void, CreditResponseBean>{

	//	private ProgressDialog mDialog;
	private Context context;
	private JSONObject jsonObject;
	private Activity activity;

	public ChekinAsyncTask(Context context, Activity activity, JSONObject jobs)
	{
		this.context=context;
		this.jsonObject=jobs;
		this.activity=activity;
	}

	@Override
	protected void onPostExecute(CreditResponseBean responseBean)
	{
		super.onPostExecute(responseBean);
		//		if(mDialog != null && mDialog.isShowing())
		//		{
		//			mDialog.dismiss();
		//		}
		if(activity!=null)
		{
			if(activity instanceof CheckinActivity)
			{
				if (responseBean!=null && responseBean.getmErrorCode()==0 && responseBean.getmExceptionName() == null)
				{
					((CheckinActivity)activity).onSuccess(responseBean);
				}
				else
				{
					((CheckinActivity)activity).onError();
				}
			}
		}

	}

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
	}

	@Override
	protected CreditResponseBean doInBackground(Void... params)
	{
		CreditResponseBean baseResponse = NetworkCall.getNetworkCallInstance(context).chekin(jsonObject);
		return baseResponse;
	}

}
