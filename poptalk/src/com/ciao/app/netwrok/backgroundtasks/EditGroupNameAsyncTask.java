package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.EditgroupInfoActivity;
//This Asyntask is used to update the group name on server in background
public class EditGroupNameAsyncTask  extends AsyncTask<Void, Void, Void>{
	private Context context;
	private JSONObject jsonRequest;
	private ProgressDialog dialog;
	public EditGroupNameAsyncTask(Context context,JSONObject jsonRequest) {
		this.context = context;
		this.jsonRequest = jsonRequest;
		dialog = new ProgressDialog(context);
		dialog.setMessage("Please wait...");
		dialog.setCancelable(false);
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog.show();
	}
	@Override
	protected Void doInBackground(Void... params) {
		String response = NetworkCall.getInstance(context).hitNetwork(AppNetworkConstants.EDIT_GROUP_INFO, jsonRequest);
		try{
			JSONObject jsonObject = new JSONObject(response);
			String errorCode = jsonObject.getString("error_code");	
			if(errorCode.equalsIgnoreCase("0")){
				if(context instanceof EditgroupInfoActivity){
					((EditgroupInfoActivity)context).groupNameChanged();
				}
				
			}
		}catch(JSONException e){

		}



		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dialog.hide();
	}

}