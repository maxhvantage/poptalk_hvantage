package com.ciao.app.netwrok.backgroundtasks;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Window;
import android.widget.TextView;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.views.activities.GroupChatActivity;
/*
 * This AsyntaskLoader is used to process group creation request in background
 */

public class GroupCreaterAsyncTask {

	private AsyncTask<Void, Void, Void> groupCreaterAsyntask = new AsyncTask<Void, Void, Void>() {
		String groupId;
		protected void onPreExecute() {
			mProgressDialog.show();
		}
		@Override
		protected Void doInBackground(Void... params) {
			groupId = groupName.replace(" ","_")+"_"+System.currentTimeMillis();
			List<String> contactJabberID = ApplicationDAO.getInstance(mContext).getJabberIdToCreateGroup(selectedContactList);
			contactJabberID.add(AppSharedPreference.getInstance(mContext).getUserCountryCode()+AppSharedPreference.getInstance(mContext).getUserPhoneNumber());
			XMPPChatService.createGroupChatRoom(groupName,groupId,contactJabberID, "Please join my gruop",(AppSharedPreference.getInstance(mContext).getUserCountryCode()+AppSharedPreference.getInstance(mContext).getUserPhoneNumber()));
			ApplicationDAO.getInstance(mContext).createGroupAndStoreInDb(groupName,groupId,groupIcon);
			ApplicationDAO.getInstance(mContext).saveUsersOfGroupInDb(groupId,contactJabberID);
			ApplicationDAO.getInstance(mContext).createNewGroupChat(groupId, Long.toString(System.currentTimeMillis()), "A new Group created", false, groupName);
			NetworkCall.getInstance(mContext).hitCreateGroupService(mContext,groupId,groupName,groupIcon,contactJabberID);
			return null;
		}

		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			Intent intent = new Intent(mContext,GroupChatActivity.class);
			intent.putExtra("jid",groupId);
			intent.putExtra("user_name",groupName);
			intent.putExtra("user_pic",groupIcon);
			mContext.startActivity(intent);
			mContext.setResult(mContext.RESULT_OK);
			mContext.finish();
		}
	};
	private Dialog mProgressDialog;
	private ArrayList<CiaoContactBean> selectedContactList;
	private Activity mContext;
	private String groupName,groupIcon;


	public GroupCreaterAsyncTask(Activity mContext,ArrayList<CiaoContactBean> selectedContactList,String groupName,String groupIcon) {
		mProgressDialog = new Dialog(mContext);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog.setContentView(R.layout.dialog_progress_indicator);
		((TextView) mProgressDialog.findViewById(R.id.tv_text)).setText("Creating group...");
		mProgressDialog.setCancelable(false);
		this.selectedContactList = selectedContactList;
		this.mContext = mContext;
		this.groupName = groupName;
		this.groupIcon = groupIcon;
	}

	public void execute(){
		groupCreaterAsyntask.execute();
	}
}
