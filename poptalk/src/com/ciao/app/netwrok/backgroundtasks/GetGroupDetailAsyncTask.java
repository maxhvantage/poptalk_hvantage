package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.GroupInfoBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.ParserClass;
import com.ciao.app.utils.AppUtils;
/*
 * This AsyntaskLoader is used to get group detail if user received invitation from a group in background
 */
public class GetGroupDetailAsyncTask extends AsyncTask<Void, Void, GroupInfoBean>{
	private Context mContext;
	private String groupJID;
	public GetGroupDetailAsyncTask(Context context,String groupJID) {
		this.mContext = context;
		this.groupJID = groupJID;
	}

	@Override
	protected GroupInfoBean doInBackground(Void... params) {
		JSONObject jsonObject  = new JSONObject();
		try{
			jsonObject.put("user_security", mContext.getString(R.string.user_security_key));
			jsonObject.put("user_id", AppSharedPreference.getInstance(mContext).getUserID());
			jsonObject.put("group_jid", groupJID.split("@")[0]);
		}catch(JSONException e){
			e.printStackTrace();
		}
		String response = NetworkCall.getInstance(mContext).hitNetwork(AppNetworkConstants.GET_GROUP_DETAIL,jsonObject);
		GroupInfoBean groupInfoBean = ParserClass.getInstance(mContext).parseGroupInfo(response);
		if(groupInfoBean!=null){
			if(groupInfoBean.getGroupJID()!=null && groupInfoBean.getGroupJID().length()>0){
				ApplicationDAO.getInstance(mContext).createGroupAndStoreInDb(groupInfoBean.getGroupName(),groupInfoBean.getGroupJID(),groupInfoBean.getGroupImage());
				ApplicationDAO.getInstance(mContext).saveUsersOfReceivedGroupInDb(groupInfoBean.getGroupJID(),groupInfoBean.getMemberList());
				ApplicationDAO.getInstance(mContext).createNewGroupChat(groupInfoBean.getGroupJID(), Long.toString(System.currentTimeMillis()), "A new Group created", false, groupInfoBean.getGroupName());
				AppUtils.showNotification(mContext, groupInfoBean.getGroupJID(), "", groupInfoBean.getGroupName(), "pic",true);	
			}
		}
		return null;
	}



}
