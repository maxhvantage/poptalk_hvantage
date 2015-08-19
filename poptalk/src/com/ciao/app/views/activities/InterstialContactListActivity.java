package com.ciao.app.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.broadcastreceiver.ContactSyncBroadCastReceiver;
import com.ciao.app.broadcastreceiver.ContactSyncBroadCastReceiver.ContactSyncListener;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.service.ContactSyncService;
import com.ciao.app.utils.AnimationUtils;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.ciao.app.views.fragments.CiaoOutContactListFragment;
import com.ciao.app.views.fragments.CiaoUserContactListFragment;
import com.ciao.app.views.fragments.InterstialContactListFragment;

public class InterstialContactListActivity extends FragmentActivity implements OnClickListener,AnimationListener,ContactSyncListener {
	//private LinearLayout ciaoContactsLL,ciaoOutContactsLL;
	private RelativeLayout ciaoPopTalkRL,ciaoContactsRL;
	//private TextView ciaoContactTV,ciaoOutContactTV;
	private View popTalkV,contactV;
	private int contactOperation;
	private CiaoUserContactListFragment mCiaoUserFragment; 
	private CiaoOutContactListFragment mCiaoOutUserFragment;
	private Bundle arguments;
	private ImageView mSyncIV;
	private int currentFragment = AppConstants.CIAO_CONTACT_FRAGMENT_ID;
	private Fragment mfragment;
	private Animation animation;
	private ContactSyncBroadCastReceiver mReceiver;
	private boolean isScreenVisible = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mReceiver = new ContactSyncBroadCastReceiver();
		Intent intent = getIntent();
		if(intent!=null){
			Bundle bundle =	intent.getExtras();
			if(bundle!=null){
				contactOperation = bundle.getInt("operation");
				arguments = new Bundle();
				arguments.putInt("operation", contactOperation);
			}
		}

		setContentView(R.layout.activity_contact_list);
		//ciaoContactsLL= (LinearLayout)findViewById(R.id.ll_ciao_contact);
		//ciaoOutContactsLL= (LinearLayout)findViewById(R.id.ll_ciao_out_contacts);

		ciaoPopTalkRL = (RelativeLayout)findViewById(R.id.rl_poptalk);
		ciaoContactsRL = (RelativeLayout)findViewById(R.id.rl_contact);
		popTalkV = (View)findViewById(R.id.v_poptalk);
		contactV = (View)findViewById(R.id.v_contact);

		//ciaoContactTV = (TextView)findViewById(R.id.tv_ciao_contacts);
		//ciaoOutContactTV= (TextView)findViewById(R.id.tv_ciao_out_contacts);
		mSyncIV = (ImageView)findViewById(R.id.iv_sync_contacts);
		//ciaoContactsLL.setOnClickListener(this);
		//ciaoOutContactsLL.setOnClickListener(this);

		ciaoPopTalkRL.setOnClickListener(this);
		ciaoContactsRL.setOnClickListener(this);
		ciaoPopTalkRL.setSelected(true);
		ciaoContactsRL.setSelected(false);

		//ciaoContactsLL.setSelected(true);
		//ciaoOutContactsLL.setSelected(false);

		//ciaoContactTV.setTextColor(getResources().getColor(R.color.color_txt));
		//ciaoOutContactTV.setTextColor(Color.WHITE);
		switchContactsFragment(AppConstants.CIAO_CONTACT_FRAGMENT_ID);
		mReceiver.setContactSyncListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isScreenVisible =true;
		if(AppSharedPreference.getInstance(this).getAppContactSyncing()){
			startAnimation();
		}else{
			
		}
		
	}
	public void switchContactsFragment(int fragmentId) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		switch (fragmentId) {
		case AppConstants.CIAO_CONTACT_FRAGMENT_ID:
			mfragment =  new CiaoUserContactListFragment();
			mfragment.setArguments(arguments);
			fragmentTransaction.replace(R.id.ll_contacts_fragment_container,mfragment);
			fragmentTransaction.commit();
			currentFragment = AppConstants.CIAO_CONTACT_FRAGMENT_ID;
			break;
		case AppConstants.CIAO_OUT_CONTACT_FRAGMENT_ID:
			mfragment = new CiaoOutContactListFragment();
			mfragment.setArguments(arguments);
			fragmentTransaction.replace(R.id.ll_contacts_fragment_container,mfragment);
			fragmentTransaction.commit();
			currentFragment = AppConstants.CIAO_OUT_CONTACT_FRAGMENT_ID;
			break;
		default:
			break;
		}

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_poptalk:
			ciaoPopTalkRL.setSelected(true);
			ciaoContactsRL.setSelected(false);
			popTalkV.setVisibility(View.VISIBLE);
			contactV.setVisibility(View.GONE);
			//ciaoContactTV.setTextColor(getResources().getColor(R.color.color_txt));
			//ciaoOutContactTV.setTextColor(Color.WHITE);
			if(!AppSharedPreference.getInstance(this).getAppContactSyncing()){
			switchContactsFragment(AppConstants.CIAO_CONTACT_FRAGMENT_ID);
			}
			break;
		case R.id.rl_contact:
			ciaoPopTalkRL.setSelected(false);
			ciaoContactsRL.setSelected(true);
			popTalkV.setVisibility(View.GONE);
			contactV.setVisibility(View.VISIBLE);
			//ciaoContactTV.setTextColor(Color.WHITE);
			//ciaoOutContactTV.setTextColor(getResources().getColor(R.color.color_txt));
			if(!AppSharedPreference.getInstance(this).getAppContactSyncing()){
			switchContactsFragment(AppConstants.CIAO_OUT_CONTACT_FRAGMENT_ID);
			}
			break; 	
		}

	}

	public void syncContacts(View view) {
		DialogUtils.showContactSyncDialog(this);
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
		Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.ll_contacts_fragment_container);
		if(currentFragment instanceof CiaoUserContactListFragment){
			((CiaoUserContactListFragment)mfragment).pleaseCheckBack(true);
		}else if(currentFragment instanceof CiaoOutContactListFragment){
			((CiaoOutContactListFragment)mfragment).pleaseCheckBack(true);
		}
	}

	@Override
	public void loaded() {
		// TODO Auto-generated method stub
		stopAnimation();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Fragment currentfragment = getSupportFragmentManager().findFragmentById(R.id.ll_contacts_fragment_container);
				if(currentfragment instanceof CiaoUserContactListFragment){
					((CiaoUserContactListFragment)mfragment).pleaseCheckBack(false);
				}else if(currentfragment instanceof CiaoOutContactListFragment){
					((CiaoOutContactListFragment)mfragment).pleaseCheckBack(false);
				}
				switchContactsFragment(currentFragment);
			}
		}, 2000);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isScreenVisible =false;
	}
	
	public void setResultBack(String id,String name,String pic){
		Intent intent = new Intent();
		intent.putExtra("_id",id);
		intent.putExtra("_name",name);
		intent.putExtra("_pic",pic);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
