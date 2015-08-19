package com.ciao.app.views.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.adapters.AutocompleteContactAdapter;
import com.ciao.app.adapters.GroupUserContactsAdapter;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.interfaces.CustomAutoCompleteTextChangedListener;
import com.ciao.app.netwrok.backgroundtasks.GroupCreaterAsyncTask;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.customviews.CustomAutoCompleteView;

public class CreateGroupChatAutoCompleterView extends Activity{
	private ListView groupUserLV;
	private CustomAutoCompleteView mAutoCompleter;
	private List<CiaoContactBean> matchedContactList;
	private ArrayList<CiaoContactBean> selectedContactList;
	private AutocompleteContactAdapter mAdapter;
	private GroupUserContactsAdapter mGroupAdapter;
	private String groupName,groupIcon;
	private CustomAutoCompleteTextChangedListener mTextChangeListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group_autocompleter);
		groupUserLV = (ListView)findViewById(R.id.lv_group_user);
		mAutoCompleter =  (CustomAutoCompleteView)findViewById(R.id.atc_contact);
		matchedContactList = new ArrayList<CiaoContactBean>();
		selectedContactList = new ArrayList<CiaoContactBean>();
		mTextChangeListener = new CustomAutoCompleteTextChangedListener(this,selectedContactList);
		mAutoCompleter.addTextChangedListener(mTextChangeListener);
		mAutoCompleter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				TextView textView = (TextView)view.findViewById(android.R.id.text1);
				CiaoContactBean ciaoContactBean = (CiaoContactBean)textView.getTag();
				mAutoCompleter.setText(ciaoContactBean.getName());
				selectedContactList.add(ciaoContactBean);
				mGroupAdapter.notifyDataSetChanged();
				mAutoCompleter.setText("");
				mTextChangeListener.updateSelectedContactList(selectedContactList);
				
				
			}
		});
		mGroupAdapter = new GroupUserContactsAdapter(this, R.layout.contacts_dropdown_item, selectedContactList);
		mAdapter = new AutocompleteContactAdapter(this, R.layout.contacts_dropdown_item, android.R.id.text1, matchedContactList);
		mAutoCompleter.setAdapter(mAdapter);
		groupUserLV.setAdapter(mGroupAdapter);
		
		Intent intent = getIntent();
		if(intent!=null){
			Bundle bundle =  intent.getExtras();
			if(bundle!=null){
				groupName = bundle.getString("group_name");
				groupIcon = bundle.getString("group_icon");
			}
			
		}


	}
	
	public void updateAutoCompleter(AutocompleteContactAdapter mAdapter){
		this.mAdapter = mAdapter;
		mAutoCompleter.setAdapter(this.mAdapter);
	}
	
	public void goToPreviousScreen(View view){
		finish();
	}
	// Create group on server
	public void createGroupChat(View view){
		if (!AppSharedPreference.getInstance(CreateGroupChatAutoCompleterView.this).getIsConnectedToInternet()){
			DialogUtils.showInternetAlertDialog(CreateGroupChatAutoCompleterView.this);
			return;
		}

		if(selectedContactList.size()>0){
			new GroupCreaterAsyncTask(this,selectedContactList,groupName,groupIcon).execute();
		}else{
			AppUtils.showTost(this, "Please select atleast one contact");
		}
		
	}
	// Go to screen where user can select multiple contact at once using multiselect list view .
	public void goToSelectMultipleContactsAtOnce(View view){
		Intent intent = new Intent(this,CreateGroupUsingListActivity.class); 
		intent.putExtra("group_name", groupName);
		intent.putExtra("group_icon", groupIcon);
		startActivityForResult(intent, 1);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){
			if(resultCode == RESULT_OK){
				setResult(RESULT_OK);
				finish();
			}
		}
	}

}
