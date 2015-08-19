package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONException;
import org.json.JSONObject;

import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.GroupInfoActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

/*
 * This asyncTask is used to update the group image in background.
 */
public class UpdateGroupImageAsyncTask extends AsyncTask<Void, Void, Void>{
	private boolean isImageUpdated = false;
	private Context context;
	private String groupJID;
	private ProgressDialog dialog;
	public UpdateGroupImageAsyncTask(Context context,String groupJID){
		this.context = context;
		this.groupJID = groupJID;
		dialog = new ProgressDialog(context);
		dialog.setMessage("Uploading image...");
		dialog.setCancelable(false);
		
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog.show();
	}
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dialog.dismiss();
		if(isImageUpdated){
			AppUtils.showTost(context, "Group Image updated successfully");
		}else{
			AppUtils.showTost(context, "Unable to update group image,please try again later");
		}
	}
	@Override
	protected Void doInBackground(Void... params) {
		String respose = NetworkCall.getInstance(context).hitGroupImageUploaderService(AppConstants.APP_IMAGE_BASE_FOLDER+ "/temp_pic.png", groupJID, context);
		try {
			JSONObject jsonObject = new JSONObject(respose);
			String errorCode = jsonObject.getString("error_code");
			if (errorCode.equalsIgnoreCase("0")) {
				JSONObject resultJsonObject = jsonObject.getJSONObject("result");
				String groupImage = resultJsonObject.getString("group_image");
				XMPPChatService.updateGroupName(groupImage, groupJID);
				ApplicationDAO.getInstance(context).updateGroupImage(groupJID, groupImage);
				isImageUpdated = true;
			} else {
				isImageUpdated = false;  
			}
		} catch (JSONException e) {
			e.printStackTrace();
			isImageUpdated = false;
		}
		return null;
	}
}
