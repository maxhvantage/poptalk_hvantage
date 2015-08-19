package com.ciao.app.views.fragments;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class InterstialContactListFragment extends Fragment implements OnClickListener{
	private View mView,popTalkV,contactV;
	private SherlockFragmentActivity mActivity;
	//	private CiaoUserContactListFragment mCiaoUserFragment; 
	//	private CiaoOutContactListFragment mCiaoOutUserFragment;
	private Bundle arguments;
	private RelativeLayout ciaoPopTalkRL,ciaoContactsRL;
	private int currentFragment;
	private Fragment mfragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mActivity = (SherlockFragmentActivity) getActivity();
		mView = inflater.inflate(R.layout.fragment_app_contacts, container, false);
		arguments = new Bundle();
		arguments.putInt("operation", AppConstants.CONTACT_DETAIL);
		switchContactsFragment(AppConstants.CIAO_CONTACT_FRAGMENT_ID);

		ciaoPopTalkRL = (RelativeLayout)mView.findViewById(R.id.rl_poptalk);
		ciaoContactsRL = (RelativeLayout)mView.findViewById(R.id.rl_contact);
		popTalkV = (View)mView.findViewById(R.id.v_poptalk);
		contactV = (View)mView.findViewById(R.id.v_contact);

		ciaoPopTalkRL.setOnClickListener(this);
		ciaoContactsRL.setOnClickListener(this);
		ciaoPopTalkRL.setSelected(true);
		ciaoContactsRL.setSelected(false);
		return mView;
	}

	@Override
	public void onResume() {
		
		super.onResume();
		/*if(AppSharedPreference.getInstance(getActivity()).getAppContactSyncing()){
			CheckBack(true);
		}else{
			CheckBack(false);
		}*/
	}
	
	//Change fragments on tap of tabs in top of screen
	public void switchContactsFragment(int fragmentId) {
		FragmentManager fragmentManager = getChildFragmentManager();;
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
			currentFragment = AppConstants.CIAO_CONTACT_FRAGMENT_ID;
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_poptalk:
			if(!AppSharedPreference.getInstance(getActivity()).getAppContactSyncing()){
				ciaoPopTalkRL.setSelected(true);
				ciaoContactsRL.setSelected(false);
				popTalkV.setVisibility(View.VISIBLE);
				contactV.setVisibility(View.GONE);
				switchContactsFragment(AppConstants.CIAO_CONTACT_FRAGMENT_ID);	
			}
			
			break;
		case R.id.rl_contact:
			if(!AppSharedPreference.getInstance(getActivity()).getAppContactSyncing()){
				ciaoPopTalkRL.setSelected(false);
				ciaoContactsRL.setSelected(true);
				popTalkV.setVisibility(View.GONE);
				contactV.setVisibility(View.VISIBLE);
				switchContactsFragment(AppConstants.CIAO_OUT_CONTACT_FRAGMENT_ID);	
			}
			
			break; 	
		}
	}

	//	public void updateList(){
	//		if(currentFragment == AppConstants.CIAO_CONTACT_FRAGMENT_ID){
	//			mCiaoUserFragment.notifyListAdapter();
	//		}
	//	}

	public void CheckBack(boolean bool){
		if(mfragment instanceof CiaoUserContactListFragment){
			((CiaoUserContactListFragment)mfragment).pleaseCheckBack(bool);
		}else if(mfragment instanceof CiaoOutContactListFragment){
			((CiaoOutContactListFragment)mfragment).pleaseCheckBack(bool);
		}
	}
}
