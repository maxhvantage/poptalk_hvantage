package in.lockerapplication.asyncTask;

import in.lockerapplication.TabActivityPager;
import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.fragment.FragmentCheckin;
import in.lockerapplication.fragment.FragmentClickAds;
import in.lockerapplication.fragment.FragmentInstallApp;
import in.lockerapplication.fragment.FragmentMain;
import in.lockerapplication.networkcall.NetworkCall;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

public class ChekinAsyncTask  extends AsyncTask<Void, Void, CreditResponseBean>{

	//	private ProgressDialog mDialog;
	private Context context;
	private JSONObject jsonObject;
	private Fragment fragment;

	public ChekinAsyncTask(Context context,Fragment fragment, JSONObject jobs)
	{
		this.context=context;
		this.jsonObject=jobs;
		this.fragment=fragment;
	}

	@Override
	protected void onPostExecute(CreditResponseBean responseBean)
	{
		super.onPostExecute(responseBean);
		//		if(mDialog != null && mDialog.isShowing())
		//		{
		//			mDialog.dismiss();
		//		}
		if(fragment!=null)
		{
			if(fragment instanceof  FragmentCheckin)
			{
				if (responseBean!=null && responseBean.getmErrorCode()==0 && responseBean.getmExceptionName() == null)
				{
					((FragmentCheckin)fragment).onSuccess(responseBean);
				}
				else
				{
					((FragmentCheckin)fragment).onError();
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
