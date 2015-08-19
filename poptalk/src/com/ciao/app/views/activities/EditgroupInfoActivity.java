package com.ciao.app.views.activities;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.netwrok.backgroundtasks.EditGroupNameAsyncTask;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;


/*
 * This class is used to Edit the group name
 */
public class EditgroupInfoActivity extends Activity {

	private String groupName,groupJID;
	private EditText groupNameEt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group_info);
		groupNameEt = ((EditText)findViewById(R.id.tv_group_name));
		Intent intent = getIntent();
		if(intent!=null){
			Bundle bundle = intent.getExtras();
			if (bundle!=null) {
				groupName =	bundle.getString("group_name");
				groupJID=bundle.getString("group_jid");
				groupNameEt.setText(groupName);
			}
		}

	}

	public void cancel(View view)
	{
		finish();
	}

	public void ok(View view)
	{
		if(((EditText)findViewById(R.id.tv_group_name)).getText().length()>0){
			groupName = groupNameEt.getText().toString();

			if (!AppSharedPreference.getInstance(EditgroupInfoActivity.this).getIsConnectedToInternet()){
				DialogUtils.showInternetAlertDialog(EditgroupInfoActivity.this);
				return;
			}

			try {

				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("user_security", getString(R.string.user_security_key));
				jsonRequest.put("user_id",AppSharedPreference.getInstance(this).getUserCountryCode()+AppSharedPreference.getInstance(this).getUserPhoneNumber());
				jsonRequest.put("group_jid",groupJID);
				jsonRequest.put("group_name",groupName);
				
                new EditGroupNameAsyncTask(this,jsonRequest).execute();
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}


	}

	public void goToPreviousScreen(View view) {
		finish();
	}
	
	public void groupNameChanged(){
		Intent backIntent = new Intent();
		backIntent.putExtra("group_name", groupName);
		setResult(RESULT_OK, backIntent);
		finish();
			
	}


}
