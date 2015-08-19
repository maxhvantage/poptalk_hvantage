package com.ciao.app.views.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.broadcastreceiver.ContactSyncBroadCastReceiver;
import com.ciao.app.broadcastreceiver.ContactSyncBroadCastReceiver.ContactSyncListener;
import com.ciao.app.broadcastreceiver.GetSMSBroadCastReceiver;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.service.ContactSyncService;
import com.ciao.app.service.GCMIntentService;
import com.ciao.app.service.GetSMSServices;
import com.ciao.app.service.GetTotalCredit;
import com.ciao.app.service.SyncUserData;
import com.ciao.app.utils.AnalyticsUtils;
import com.ciao.app.utils.AnimationUtils;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.ciao.app.views.fragments.CallFragment;
import com.ciao.app.views.fragments.CiaoUserContactListFragment;
import com.ciao.app.views.fragments.CreditsFragment;
import com.ciao.app.views.fragments.InterstialContactListFragment;
import com.ciao.app.views.fragments.MessageFragment;
import com.ciao.app.views.fragments.SettingsFragment;
import com.csipsimple.ui.SipHome;
import com.poptalk.app.R;

import org.jivesoftware.smack.XMPPConnection;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by rajat on 25/1/15.
 * This is the main screen of app with four tabs in bottom
 * This Activity have four Fragments,CallFrament,MessageFragment,ContactFragment,CreditFragment and SettingFragment
 */
public class CallActivity extends SipHome implements OnClickListener,AnimationListener,ContactSyncListener{
	private TextView callTV, messageTV,contactsTV,creditsTV, settingsTV,ciaoContactTV,ciaoOutContactTV,totalCreditsCallTV,totalCreditsTV,totalCreditsSettingTV;
	private LinearLayout callLL, messageLL,contactsLL, creditsLL, settingsLL,ciaoContactsLL,ciaoOutContactsLL;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private RelativeLayout keypadHeaderRL, contactHeaderRL, creditHeaderRL, messageHeaderRL, chatViewHeaderRL, accountSettingHeaderRl;
	private LinearLayout appHeaderLL;
	private ImageView backFromChat, addNewChat, backFromAddNewChatUserScreen;
	private RelativeLayout addNewChatUser /*contactNormalHeaderRL, contactSearchHeaderRL*/;
	private CallFragment callFragment;
	private static ProgressDialog progressDialog;
	private MediaPlayer player;
	private InterstialContactListFragment mIterstialContactsList;	
	private ImageView createGroupIV;
	private ImageView mSyncIV;
	private Animation animation;
	private ContactSyncBroadCastReceiver mReceiver;
	private int currentFragment = AppConstants.CALL_FRAGMENT_ID;
	private static boolean screenVisible = false;
	private Fragment mFragment;
	private boolean isSmsFragment = false;
	private TextView missedCallCountTV,unreadMSGTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		getSMSAlarmManager();
		List<String> ciaoNumberListTemp = ApplicationDAO.getInstance(this).getMyCiaoNumber();
		for(String ciaoNumber:ciaoNumberListTemp){
			ciaoNumber = AppUtils.parseFormattedNumber(ciaoNumber);
			ciaoNumber = ciaoNumber.replace("+", "");
			ciaoNumberList.add(ciaoNumber);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callscreen);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		intViews();
		intListener();
		intVariables();
		Intent intent = getIntent();
		if(intent!=null){
			Bundle bundle = intent.getExtras();
			if(bundle!=null){
				currentFragment = bundle.getInt("current_fragment");
			}
		}
		switchFragment(currentFragment);
		if (!AppSharedPreference.getInstance(this).getAppContactSynced()) {
			if(!AppSharedPreference.getInstance(this).getAppContactSyncing()){
				DialogUtils.showContactSyncDialog(this);
			}

		}
		startService(new Intent(this, XMPPChatService.class));
		mReceiver = new ContactSyncBroadCastReceiver();
		mReceiver.setContactSyncListener(this);

	}

//	private void getSMSAlarmManager(){
//		AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//		Intent intent = new Intent(this, GetSMSBroadCastReceiver.class);
//		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		manager.setInexactRepeating(AlarmManager.RTC, 0, 2000, pendingIntent);
//	}

	@Override
	protected void onResume() {
		super.onResume();
		startService(new Intent(this, SyncUserData.class));
		startService(new Intent(this, GetTotalCredit.class));
		startService(new Intent(this, GetSMSServices.class));
		screenVisible = true;
		totalCreditsCallTV.setText(AppSharedPreference.getInstance(this).getTotalCredit()+"");
		totalCreditsSettingTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
		if(AppSharedPreference.getInstance(this).getAppContactSyncing()){
			startAnimation();
		}
		updateMissedCallCounter();
	}

	//Called this function from chiled fragment.
	public void UpdateCredits()
	{
		onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		screenVisible = false;
	}
	private void intVariables() {

	}

	@Override
	protected String sipUser() {
		return AppSharedPreference.getInstance(this).getCommUser();
	}

	@Override
	protected String sipAuth() {
		return AppSharedPreference.getInstance(this).getCommSecurity();
	}

	private void intListener() {
		callTV.setOnClickListener(this);
		messageTV.setOnClickListener(this);
		contactsTV.setOnClickListener(this);
		creditsTV.setOnClickListener(this);
		settingsTV.setOnClickListener(this);
		backFromChat.setOnClickListener(this);
		addNewChat.setOnClickListener(this);
		addNewChatUser.setOnClickListener(this);
		backFromAddNewChatUserScreen.setOnClickListener(this);
		ciaoContactsLL.setOnClickListener(this);
		ciaoOutContactsLL.setOnClickListener(this);
	}

	private void intViews() {
		callLL = (LinearLayout)findViewById(R.id.ll_menu_call);
		messageLL = (LinearLayout)findViewById(R.id.ll_menu_message);
		creditsLL= (LinearLayout)findViewById(R.id.ll_menu_credits);
		settingsLL= (LinearLayout)findViewById(R.id.ll_menu_settings);
		callTV = (TextView) findViewById(R.id.tv_Call);
		messageTV = (TextView) findViewById(R.id.tv_message);
		contactsTV = (TextView) findViewById(R.id.tv_contacts);
		creditsTV = (TextView) findViewById(R.id.tv_credits);
		settingsTV = (TextView) findViewById(R.id.tv_settings);
		keypadHeaderRL = (RelativeLayout) findViewById(R.id.rl_header_keybaord);
		contactHeaderRL = (RelativeLayout) findViewById(R.id.rl_header_contact_fragment);
		creditHeaderRL = (RelativeLayout) findViewById(R.id.rl_header_credit);
		messageHeaderRL = (RelativeLayout) findViewById(R.id.rl_header_message);
		chatViewHeaderRL = (RelativeLayout) findViewById(R.id.rl_header_chat);
		accountSettingHeaderRl = (RelativeLayout) findViewById(R.id.rl_header_account_settings);
		appHeaderLL = (LinearLayout) findViewById(R.id.ll_app_header);
		backFromChat = (ImageView) findViewById(R.id.iv_back_from_chat);
		addNewChatUser = (RelativeLayout) findViewById(R.id.ll_add_chat);
		addNewChat = (ImageView) findViewById(R.id.iv_add_new_chat);
		backFromAddNewChatUserScreen = (ImageView) findViewById(R.id.iv_back_from_add_new_chat_user);
		ciaoContactsLL= (LinearLayout)findViewById(R.id.ll_ciao_contact);
		ciaoOutContactsLL= (LinearLayout)findViewById(R.id.ll_ciao_out_contacts);
		ciaoContactTV = (TextView)findViewById(R.id.tv_ciao_contacts);
		ciaoOutContactTV= (TextView)findViewById(R.id.tv_ciao_out_contacts);
		totalCreditsCallTV = (TextView)findViewById(R.id.tv_total_credits_call);
		totalCreditsTV = (TextView)findViewById(R.id.tv_total_credits);
		totalCreditsSettingTV = (TextView)findViewById(R.id.tv_total_credits_setting);
		createGroupIV = (ImageView)findViewById(R.id.iv_create_group_chat);
		mSyncIV = (ImageView)findViewById(R.id.iv_sync_contacts);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Please wait...");
		progressDialog.setCancelable(false);
		missedCallCountTV = (TextView)findViewById(R.id.tv_missed_call_count);
		unreadMSGTV = (TextView)findViewById(R.id.tv_unread_sms_count);


	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_Call:
			switchFragment(AppConstants.CALL_FRAGMENT_ID);

			break;
		case R.id.tv_message:
			AnalyticsUtils.sendEvent("app_messages", new AnalyticsUtils.Param("view", "message_list"));
			switchFragment(AppConstants.MESSAGE_FRAGMENT_ID);
			break;
		case R.id.tv_contacts:
			switchFragment(AppConstants.CONTACTS_FRAGMENT_ID);
			break;
		case R.id.tv_credits:
			AnalyticsUtils.sendEvent("app_credits", new AnalyticsUtils.Param("view", "credits"));
			switchFragment(AppConstants.CREDITS_FRAGMENT_ID);
			break;
		case R.id.tv_settings:
			AnalyticsUtils.sendEvent("app_credits", new AnalyticsUtils.Param("view", "settings"));
			switchFragment(AppConstants.SETTINGS_FRAGMENT_ID);
			break;
		case R.id.iv_back_from_chat:
			addNewChatUser.setVisibility(View.GONE);
			switchFragment(AppConstants.MESSAGE_FRAGMENT_ID);
			break;
		case R.id.iv_add_new_chat:
			AnalyticsUtils.sendEvent("app_messages", new AnalyticsUtils.Param("add_chat", "open"));
			addNewChatUser.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_back_from_add_new_chat_user:
			addNewChatUser.setVisibility(View.GONE);
			break;
		case R.id.ll_add_chat:
			addNewChatUser.setVisibility(View.GONE);
			break;
		case R.id.iv_search_contact_in_contact_fragment:
			break;
		case R.id.bnt_clear_search_et:
			break;
		case R.id.ll_ciao_contact:
			ciaoContactsLL.setSelected(true);
			ciaoOutContactsLL.setSelected(false);
			ciaoContactTV.setTextColor(getResources().getColor(R.color.color_txt));
			ciaoOutContactTV.setTextColor(Color.WHITE);
			if(mIterstialContactsList!=null){
				mIterstialContactsList.switchContactsFragment(AppConstants.CIAO_CONTACT_FRAGMENT_ID);
			}

			break;
		case R.id.ll_ciao_out_contacts:
			ciaoContactsLL.setSelected(false);
			ciaoOutContactsLL.setSelected(true);
			ciaoContactTV.setTextColor(Color.WHITE);
			ciaoOutContactTV.setTextColor(getResources().getColor(R.color.color_txt));
			if(mIterstialContactsList!=null){
				mIterstialContactsList.switchContactsFragment(AppConstants.CIAO_OUT_CONTACT_FRAGMENT_ID);
			}
			break; 	
		default:
			switchFragment(AppConstants.CALL_FRAGMENT_ID);
			break;
		}
	}

	// switch fragment to navigate to differents screens
	public void switchFragment(int fragmentId) {
		startService(new Intent(this, GetTotalCredit.class));
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		hideKeyBoard();
		switch (fragmentId) {
		case AppConstants.CALL_FRAGMENT_ID:

			mFragment = new CallFragment();
			fragmentTransaction.replace(R.id.ll_fragment_container, mFragment);
			((View) findViewById(R.id.v_call)).setVisibility(View.VISIBLE);
			totalCreditsCallTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
			((View) findViewById(R.id.v_message)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_credits)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_settings)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_contacts)).setVisibility(View.INVISIBLE);
			currentFragment = AppConstants.CALL_FRAGMENT_ID;
			break;
		case AppConstants.MESSAGE_FRAGMENT_ID:
			mFragment=new MessageFragment();
			fragmentTransaction.replace(R.id.ll_fragment_container, mFragment);
			((View) findViewById(R.id.v_call)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_message)).setVisibility(View.VISIBLE);
			((View) findViewById(R.id.v_credits)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_settings)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_contacts)).setVisibility(View.INVISIBLE);
			changeHeader(AppConstants.MESSAGE_FRAGMENT_ID);
			currentFragment = AppConstants.MESSAGE_FRAGMENT_ID;
			break;
		case AppConstants.CONTACTS_FRAGMENT_ID:
			changeHeader(AppConstants.CONTACTS_FRAGMENT_ID);
			mFragment = new InterstialContactListFragment();
			fragmentTransaction.replace(R.id.ll_fragment_container,mFragment);
			((View) findViewById(R.id.v_call)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_message)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_credits)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_settings)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_contacts)).setVisibility(View.VISIBLE);
			currentFragment = AppConstants.CONTACTS_FRAGMENT_ID;
			break;

			/*case AppConstants.CHAT_FRAGMENT_ID:
			mFragment=new ChatViewFragment();
			fragmentTransaction.replace(R.id.ll_fragment_container, mFragment);
			((View) findViewById(R.id.v_call)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_message)).setVisibility(View.VISIBLE);
			((View) findViewById(R.id.v_credits)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_settings)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_contacts)).setVisibility(View.INVISIBLE);
			currentFragment = AppConstants.CHAT_FRAGMENT_ID;
			break;*/
		case AppConstants.CREDITS_FRAGMENT_ID:

			if (!AppSharedPreference.getInstance(CallActivity.this).getIsConnectedToInternet()){
				DialogUtils.showInternetAlertDialog(CallActivity.this);
			}
			mFragment=new CreditsFragment();
			fragmentTransaction.replace(R.id.ll_fragment_container, mFragment);
			((View) findViewById(R.id.v_call)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_message)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_credits)).setVisibility(View.VISIBLE);
			totalCreditsTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
			((View) findViewById(R.id.v_settings)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_contacts)).setVisibility(View.INVISIBLE);
			changeHeader(AppConstants.CREDITS_FRAGMENT_ID);
			currentFragment = AppConstants.CREDITS_FRAGMENT_ID;

			break;
		case AppConstants.SETTINGS_FRAGMENT_ID:
			mFragment=new SettingsFragment();
			fragmentTransaction.replace(R.id.ll_fragment_container, mFragment);
			((View) findViewById(R.id.v_call)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_message)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_credits)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_settings)).setVisibility(View.VISIBLE);
			totalCreditsSettingTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
			((View) findViewById(R.id.v_contacts)).setVisibility(View.INVISIBLE);
			currentFragment = AppConstants.SETTINGS_FRAGMENT_ID;
			break;
		default:
			mFragment = new CallFragment();
			fragmentTransaction.replace(R.id.ll_fragment_container, mFragment);
			((View) findViewById(R.id.v_call)).setVisibility(View.VISIBLE);
			((View) findViewById(R.id.v_message)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_credits)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_settings)).setVisibility(View.INVISIBLE);
			((View) findViewById(R.id.v_contacts)).setVisibility(View.INVISIBLE);
			currentFragment = AppConstants.CALL_FRAGMENT_ID;
			break;
		}

		fragmentTransaction.commit();
	}

// Change header of screen as per selected screen ui
	public void changeHeader(int fragmentId) {
		switch (fragmentId) {
		case AppConstants.KEYPAD_FRAGMENT_ID:
			keypadHeaderRL.setVisibility(View.VISIBLE);
			contactHeaderRL.setVisibility(View.GONE);
			creditHeaderRL.setVisibility(View.GONE);
			messageHeaderRL.setVisibility(View.GONE);
			chatViewHeaderRL.setVisibility(View.GONE);
			accountSettingHeaderRl.setVisibility(View.GONE);
			appHeaderLL.setVisibility(View.VISIBLE);
			break;
		case AppConstants.CONTACTS_FRAGMENT_ID:
			keypadHeaderRL.setVisibility(View.GONE);
			creditHeaderRL.setVisibility(View.GONE);
			contactHeaderRL.setVisibility(View.VISIBLE);
			messageHeaderRL.setVisibility(View.GONE);
			chatViewHeaderRL.setVisibility(View.GONE);
			accountSettingHeaderRl.setVisibility(View.GONE);
			appHeaderLL.setVisibility(View.VISIBLE);
			ciaoContactsLL.setSelected(true);
			ciaoOutContactsLL.setSelected(false);
			ciaoContactTV.setTextColor(getResources().getColor(R.color.color_txt));
			ciaoOutContactTV.setTextColor(Color.WHITE);
			break;
		case AppConstants.CREDITS_FRAGMENT_ID:
			keypadHeaderRL.setVisibility(View.GONE);
			creditHeaderRL.setVisibility(View.VISIBLE);
			contactHeaderRL.setVisibility(View.GONE);
			messageHeaderRL.setVisibility(View.GONE);
			chatViewHeaderRL.setVisibility(View.GONE);
			accountSettingHeaderRl.setVisibility(View.GONE);
			appHeaderLL.setVisibility(View.VISIBLE);

			break;
		case AppConstants.MESSAGE_FRAGMENT_ID:
			keypadHeaderRL.setVisibility(View.GONE);
			creditHeaderRL.setVisibility(View.GONE);
			contactHeaderRL.setVisibility(View.GONE);
			messageHeaderRL.setVisibility(View.VISIBLE);
			chatViewHeaderRL.setVisibility(View.GONE);
			accountSettingHeaderRl.setVisibility(View.GONE);
			appHeaderLL.setVisibility(View.VISIBLE);

			break;
		case AppConstants.CHAT_FRAGMENT_ID:
			keypadHeaderRL.setVisibility(View.GONE);
			creditHeaderRL.setVisibility(View.GONE);
			contactHeaderRL.setVisibility(View.GONE);
			messageHeaderRL.setVisibility(View.GONE);
			chatViewHeaderRL.setVisibility(View.VISIBLE);
			accountSettingHeaderRl.setVisibility(View.GONE);
			appHeaderLL.setVisibility(View.VISIBLE);

			break;
		case AppConstants.SETTINGS_FRAGMENT_ID:
			keypadHeaderRL.setVisibility(View.GONE);
			creditHeaderRL.setVisibility(View.GONE);
			contactHeaderRL.setVisibility(View.GONE);
			messageHeaderRL.setVisibility(View.GONE);
			accountSettingHeaderRl.setVisibility(View.VISIBLE);
			chatViewHeaderRL.setVisibility(View.GONE);
			appHeaderLL.setVisibility(View.VISIBLE);
			break;
		}

	}


	public void hideKeyBoard() {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
	}


	public void gotToCreditScreen(View view) {
		switchFragment(AppConstants.CREDITS_FRAGMENT_ID);
	}

	public void syncContacts() {
		if(NetworkStatus.isConected(this)){
			AppSharedPreference.getInstance(this).setAppContactSynced(false);
			AppSharedPreference.getInstance(this).setAppContactSyncing(false);
			startService(new Intent(this, ContactSyncService.class));
		}else{
			AppUtils.showTost(this, "Please check your internet connection");
		}

	}
	public void syncContacts(View view) {
		DialogUtils.showContactSyncDialog(this);
	}

	public void findContactsForChat(View view){
		if(isSmsFragment){
			Intent intent = new Intent(this,NewSmsActivity.class);
			startActivity(intent);
		}else{
			Intent intent = new Intent(this,InterstialContactListActivity.class);
			intent.putExtra("operation",AppConstants.CONTACT_MESSAGE);
			startActivityForResult(intent, 1001);
		}
	}

	public void animateCoin(View view){
		AnimationUtils.rotateCreditCoin((ImageView)view);
		playBeep();
	}
	public void playBeep() {
		try {
			AssetFileDescriptor afd = getAssets().openFd("coins.mp3");
			if(player==null){
				player = new MediaPlayer();
			}
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player.prepare();
			player.start();
			player = null;
		} catch (Exception e) {
			e.printStackTrace();
			player = null;
		}
	}

   // Go to screen where user can create new group
	public void selectContactForGroup(View view){
		XMPPConnection xmppConnection = XMPPChatService.getConnection();
		if(xmppConnection!=null){
			if(xmppConnection.isConnected()){
				if(xmppConnection.getUser()!=null){
					startActivity(new Intent(this,CreatNewGroupActivity.class));	
				}else{
					AppUtils.showTost(this, "Please check your internet connection");	
				}
			}else{
				AppUtils.showTost(this, "Please check your internet connection");
			}
		}else{
			AppUtils.showTost(this, "Please check your internet connection");
		}

	}

	// hide and show the create group icon.
	public void toggleCreateGroupIcon(boolean visible){
		if(visible){
//			createGroupIV.setVisibility(View.VISIBLE);
			isSmsFragment = false;
		}else{
//			createGroupIV.setVisibility(View.INVISIBLE);
			isSmsFragment = true;
		}

	}

	@Override
	public void onAnimationStart(Animation animation) {

	}
	@Override
	public void onAnimationEnd(Animation animation) {
		if(AppSharedPreference.getInstance(this).getAppContactSyncing()){
			animation.reset();
			animation.start();	
		}else{
			stopAnimation();
		}

	}
	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	public void startAnimation(){
		if(mSyncIV!=null){
			animation =  AnimationUtils.syncInProgressAnimation();
			animation.setAnimationListener(this);
			mSyncIV.startAnimation(animation);
		}
	}

	public void stopAnimation(){
		if(mSyncIV!=null && animation!=null){
			mSyncIV.clearAnimation();
			animation.setAnimationListener(null);	
		}

	}

	@Override
	public void loading() {
		// TODO Auto-generated method stub
		startAnimation();

		Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.ll_fragment_container);
		if(currentFragment instanceof InterstialContactListFragment){
			((InterstialContactListFragment)mFragment).CheckBack(true);
		}
	}

	@Override
	public void loaded(){
		// TODO Auto-generated method stub
		stopAnimation();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				Fragment currentfragment = getSupportFragmentManager().findFragmentById(R.id.ll_fragment_container);
				if(currentfragment instanceof CiaoUserContactListFragment){
					((InterstialContactListFragment)mFragment).CheckBack(false);
				}

				if(currentFragment  == AppConstants.CONTACTS_FRAGMENT_ID){
					if(screenVisible){
						//mIterstialContactsList.updateList();
						switchFragment(AppConstants.CONTACTS_FRAGMENT_ID);	
					}
				}
			}
		}, 2000);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}


	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		AppUtils.showTost(this, "Hi");
	}*/

	public void updateCredit(int totalCredit) {
		totalCreditsCallTV.setText(totalCredit+"");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1001){
			if(resultCode==RESULT_OK){
				if(data!=null){
					String userId=data.getStringExtra("_id");
					String userName =data.getStringExtra("_name");
					String userPic=data.getStringExtra("_pic");
					Intent intent = new Intent(this,NewSmsActivity.class);
					userId = ApplicationDAO.getInstance(this).getUserCiaoRegisteredPhoneNumber(userId);
					intent.putExtra("_id",userId);
					intent.putExtra("_name",userName);
					intent.putExtra("_pic",userPic);
					startActivity(intent);
					
				}
			}
		}
	}

    
	public void updateMissedCallCounter(){
		if(AppSharedPreference.getInstance(this).getMissedCallCount()>0){
			missedCallCountTV.setText(""+String.valueOf(AppSharedPreference.getInstance(this).getMissedCallCount()));
		}else{
			missedCallCountTV.setVisibility(View.GONE);	
		}
		int msgCount =  ApplicationDAO.getInstance(this).getContactsCountWithUnreadMessages();
		if(msgCount>0){
			unreadMSGTV.setText(Integer.toString(msgCount));
			unreadMSGTV.setVisibility(View.VISIBLE);	
		}else{
			unreadMSGTV.setVisibility(View.GONE);	
		}
	}
}
