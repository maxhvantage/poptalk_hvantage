package com.ciao.app.netwrok.backgroundtasks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.views.activities.AddParticipantToGroupActivity;
import com.poptalk.app.R;
import android.app.Activity;
import android.os.AsyncTask;

/*
 * This class is used to process the add new member to group request
 */
public class AddNewMemberToGroup {
	private Activity mActivity;
	private String groupJid;
	private String memberJid;
	private boolean memberAddedToServer = false;
	
	private AsyncTask<Void, Void, Void> addMemberTask = new AsyncTask<Void, Void, Void>() {
		protected void onPreExecute() {
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("user_security", mActivity.getString(R.string.user_security_key));
				jsonObject.put("user_device_token", AppSharedPreference.getInstance(mActivity).getDeivceToken());
				jsonObject.put("group_jid", groupJid.split("@")[0]);
				JSONArray memberJsonArray = new JSONArray();
				memberJsonArray.put(memberJid);
				jsonObject.put("member_list", memberJsonArray);
				String response = NetworkCall.getInstance(mActivity).hitNetwork(AppNetworkConstants.ADD_MEMBER_TO_GROUP, jsonObject);
			    JSONObject responseJsonObject = new JSONObject(response);
			    String errorCode = responseJsonObject.getString("error_code");
			    if(errorCode.equalsIgnoreCase("0")){
			    	memberAddedToServer = true;
			    }
			} catch (JSONException e) {
				
			}
			return null;
		}
		
		protected void onPostExecute(Void result) {
			if(memberAddedToServer){
				if(mActivity instanceof AddParticipantToGroupActivity){
					((AddParticipantToGroupActivity)mActivity).sendInvitaionToNewMember();
					List<String> contactJabberID = new ArrayList<String>();
					contactJabberID.add(memberJid);
					ApplicationDAO.getInstance(mActivity).saveUsersOfGroupInDb(groupJid.split("@")[0], contactJabberID);
				}
			}else{
				
			}
		}
	};
	public AddNewMemberToGroup(Activity mActivity,String groupJid,String memberJid){
		this.mActivity = mActivity;
		this.groupJid = groupJid;
		this.memberJid = memberJid;
	}
	
   public void execute(){
	   addMemberTask.execute();
   }
}
