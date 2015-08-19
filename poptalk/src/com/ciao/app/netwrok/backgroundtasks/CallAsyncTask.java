package com.ciao.app.netwrok.backgroundtasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ciao.app.datamodel.BaseResponseBean;
import com.ciao.app.netwrok.NetworkCall;

public class CallAsyncTask  extends AsyncTask<Void, Void, BaseResponseBean>{

	private ProgressDialog mDialog;
	private Context context;
	String url;
	public CallAsyncTask(Context context, String url)
	{
		this.context=context;
		this.url=url;
	}

	@Override
	protected void onPostExecute(BaseResponseBean responseBean)
	{
		super.onPostExecute(responseBean);
		if(mDialog != null && mDialog.isShowing())
		{
			mDialog.dismiss();
		}

		/*if(responseBean.getmErrorCode()==0){
			((CallActivity)context).success(responseBean);
		}else{
			if(responseBean.getmErrorCode()==1){
				((CallActivity)context).error("Invalid credentials");
			}else if(responseBean.getmErrorCode()==2){
				((CallActivity)context).error("Missing params");
			}else if(responseBean.getmErrorCode()==4){
				((CallActivity)context).error("Invalid destination address");
			}else if(responseBean.getmErrorCode()==5){
				((CallActivity)context).error("Cannot route the call");
			}else if(responseBean.getmErrorCode()==6){
				((CallActivity)context).error("Number barred");
			}else if(responseBean.getmErrorCode()==7){
				((CallActivity)context).error("Partner quota exceeded");
			}else if(responseBean.getmErrorCode()==99){
				((CallActivity)context).error("Internal error: An error has occurred in the Nexmo platform whilst processing this request");
			}
		}*/
	}

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		mDialog=new ProgressDialog(context);
		mDialog.setMessage("Please wait ...");
		mDialog.setCancelable(false);
		//mDialog.show();
	}

	@Override
	protected BaseResponseBean doInBackground(Void... params)
	{
		BaseResponseBean baseResponse = NetworkCall.getInstance(context).networkHitCall(url);
		return baseResponse;
	}

}
