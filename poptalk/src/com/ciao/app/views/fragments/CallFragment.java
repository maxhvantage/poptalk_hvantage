package com.ciao.app.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.utils.AnalyticsUtils;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.CallActivity;
import com.poptalk.app.R;
/**
 * Created by rajat on 25/1/15.
 * This screen will appear on the tap of call tab in bottom bar.
 * This fragment have two child fragment one is keypadFragment and another one is CallLogFragment
 */
public class CallFragment extends Fragment implements OnClickListener{
	private View mView;
	private LinearLayout keypadLL,callLogLL/*,contactListLL*/;
	private Fragment currentFragment;
	private TextView keypadTV,callLogTV/*,contactListTV*/;
	private SherlockFragmentActivity mActivity;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);


	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_call_screen,container,false);
		mActivity = (SherlockFragmentActivity)getActivity();
		intViews(mView);
		intListener();
		switchFragment(AppConstants.KEYPAD_FRAGMENT_ID);
		return mView;
	}

	private void intListener() {
		keypadLL.setOnClickListener(this);
		callLogLL.setOnClickListener(this);
		//contactListLL.setOnClickListener(this);
	}

	private void intViews(View mView) {
		keypadLL = (LinearLayout)mView.findViewById(R.id.ll_keypad);
		callLogLL = (LinearLayout)mView.findViewById(R.id.ll_call_log);
		//contactListLL = (LinearLayout)mView.findViewById(R.id.ll_contact_list);
		keypadTV = (TextView)mView.findViewById(R.id.tv_keypad);
		callLogTV = (TextView)mView.findViewById(R.id.tv_call_log);
		//contactListTV= (TextView)mView.findViewById(R.id.tv_contacts_list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.ll_keypad:
			AnalyticsUtils.sendEvent("app_calls", new AnalyticsUtils.Param("navigated", "keypad"));
			switchFragment(AppConstants.KEYPAD_FRAGMENT_ID);
			break;
		case R.id.ll_call_log:
			AnalyticsUtils.sendEvent("app_calls", new AnalyticsUtils.Param("navigated", "call_log"));
			switchFragment(AppConstants.CALLLOG_FRAGMENT_ID);
			break;
			/*case R.id.ll_contact_list:
              switchFragment(AppConstants.CONTACTS_FRAGMENT_ID);
              break;*/
		}
	}

	private void switchFragment(int fragmentId){
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		switch (fragmentId){
		case AppConstants.KEYPAD_FRAGMENT_ID:
			currentFragment = new KeypadFragment();
			((View)mView.findViewById(R.id.v_keypad)).setVisibility(View.VISIBLE);
			((View)mView.findViewById(R.id.v_call_log)).setVisibility(View.INVISIBLE);
			//((View)mView.findViewById(R.id.v_contact_list)).setVisibility(View.INVISIBLE);
			fragmentTransaction.replace(R.id.ll_call_screen_fragment_container,currentFragment);
			((CallActivity)mActivity).changeHeader(AppConstants.KEYPAD_FRAGMENT_ID);
			keypadTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.dialpad_selected, 0, 0);
			callLogTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.calllog, 0, 0);
			//contactListTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.contact_list, 0, 0);
			break;
		case AppConstants.CALLLOG_FRAGMENT_ID:
			currentFragment = new CallLogFragment();
			((View)mView.findViewById(R.id.v_keypad)).setVisibility(View.INVISIBLE);
			((View)mView.findViewById(R.id.v_call_log)).setVisibility(View.VISIBLE);
			//((View)mView.findViewById(R.id.v_contact_list)).setVisibility(View.INVISIBLE);
			fragmentTransaction.replace(R.id.ll_call_screen_fragment_container,currentFragment);
			((CallActivity)mActivity).changeHeader(AppConstants.KEYPAD_FRAGMENT_ID);
			keypadTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.dialpad, 0, 0);
			callLogTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.calllog_selected, 0, 0);
			//contactListTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.contact_list, 0, 0);
			break;
		/*case AppConstants.CONTACTS_FRAGMENT_ID:
			currentFragment = new ContactListFragment();
			((View)mView.findViewById(R.id.v_keypad)).setVisibility(View.INVISIBLE);
			((View)mView.findViewById(R.id.v_call_log)).setVisibility(View.INVISIBLE);
			//((View)mView.findViewById(R.id.v_contact_list)).setVisibility(View.VISIBLE);
			fragmentTransaction.replace(R.id.ll_call_screen_fragment_container,currentFragment);
			((CallActivity)mActivity).changeHeader(AppConstants.CONTACTS_FRAGMENT_ID);
			keypadTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.dialpad, 0, 0);
			callLogTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.calllog, 0, 0);
			//contactListTV.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.contact_list_selected, 0, 0);
			break;*/
		}
		fragmentTransaction.commit();
	}

	public Fragment getCurrentFragment(){
		return currentFragment ;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(currentFragment instanceof KeypadFragment){
			if(resultCode == 1){
				((KeypadFragment)currentFragment).setCountryCode(data);
			}
		}
	}


}
