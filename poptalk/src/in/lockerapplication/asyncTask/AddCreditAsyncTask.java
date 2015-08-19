package in.lockerapplication.asyncTask;

import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.fragment.FragmentClickAds;
import in.lockerapplication.fragment.FragmentInstallApp;
import in.lockerapplication.fragment.FragmentMain;
import in.lockerapplication.networkcall.NetworkCall;

import org.json.JSONObject;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.service.GetTotalCredit;
import com.ciao.app.views.activities.ShareToEarnCreditActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class AddCreditAsyncTask  extends AsyncTask<Void, Void, CreditResponseBean>{

	//	private ProgressDialog mDialog;
	private Context context;
	private JSONObject jsonObject;

	public AddCreditAsyncTask(Context context, JSONObject jobs)
	{
		this.context=context;
		this.jsonObject=jobs;

	}


	@Override
	protected void onPostExecute(CreditResponseBean responseBean)
	{
		super.onPostExecute(responseBean);

		if (AppSharedPreference.getInstance(context).getIsConnectedToInternet()) {
			context.startService(new Intent(context, GetTotalCredit.class));
		}
	}

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
	}

	@Override
	protected CreditResponseBean doInBackground(Void... params)
	{
		CreditResponseBean baseResponse = NetworkCall.getNetworkCallInstance(context).socialShare(jsonObject);
		return baseResponse;
	}

}
