package in.lockerapplication.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.utils.AnalyticsUtils;
import com.ciao.app.views.activities.CallActivity;
import com.poptalk.app.R;

import in.lockerapplication.MyBaseActivity;
import in.lockerapplication.TabActivityPager;
import in.lockerapplication.adNetworks.AdMediatorFragment;
import in.lockerapplication.adNetworks.AdMediatorManager;
import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.preferences.AppPreferencesKeys;
import in.lockerapplication.utility.HomeKeyLocker;
import in.lockerapplication.views.CircularRing;
import in.lockerapplication.views.IIntersectionListner;

public class FragmentMain extends Fragment implements AdMediatorFragment.onFailMediatorListener{

	protected View view_main;
	private String url1;
	private Activity mActivity;
	private CircularRing mCircularRing;
	// private boolean mIsDestroyedUsingCallBroadcast;
	private ScreenReceiver mReceiver;
	public static boolean wasScreenOn = true;
	private AdMediatorManager mediatorManager;
	private Fragment childFragment;

	private HomeKeyLocker mHomeKeyLocker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if(view_main == null)
			view_main = inflater.inflate(R.layout.activity_main_lock_screen, null);
		mActivity = getActivity();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mReceiver = new ScreenReceiver();
		getActivity().registerReceiver(mReceiver, filter);

		mCircularRing = (CircularRing) view_main
				.findViewById(R.id.cr_lock_pattern);
		mCircularRing.setIntersectListner(new IIntersectionListner() {

			@Override
			public void intersectionBitmapNumber(int i) {

				if (i == 3) {
					getActivity().getSupportFragmentManager().popBackStack();
					((MyBaseActivity) getActivity()).finishActivity();
					Intent homeIntent = new Intent(Intent.ACTION_MAIN);
					homeIntent.addCategory(Intent.CATEGORY_HOME);
					homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(homeIntent);
					getActivity().getSupportFragmentManager().popBackStack();
					((MyBaseActivity) getActivity()).finish();
					AnalyticsUtils.sendEvent("lockscreen", new AnalyticsUtils.Param("navigated", "unlocked_phone"));
				} else if (i == 2) {
					/*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
					sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					sendIntent.setData(Uri.parse("sms:"));
					startActivity(sendIntent);*/
					Intent sendIntent = new Intent(getActivity(), CallActivity.class);
					sendIntent.putExtra("current_fragment", AppConstants.MESSAGE_FRAGMENT_ID);
					sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(sendIntent);
					getActivity().getSupportFragmentManager().popBackStack();
					((MyBaseActivity) getActivity()).finishActivity();
					AnalyticsUtils.sendEvent("lockscreen", new AnalyticsUtils.Param("navigated", "messages_tab"));
				} else if (i == 1) {
					/*Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:"));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);*/
					Intent sendIntent = new Intent(getActivity(), CallActivity.class);
					sendIntent.putExtra("current_fragment", AppConstants.CALL_FRAGMENT_ID);
					sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(sendIntent);
					getActivity().getSupportFragmentManager().popBackStack();
					((MyBaseActivity) getActivity()).finishActivity();
					AnalyticsUtils.sendEvent("lockscreen", new AnalyticsUtils.Param("navigated", "calls_tab"));
				} else if (i == 0) {
					Intent intent = new Intent(getActivity(), TabActivityPager.class);
					getActivity().startActivityForResult(intent, 101);
					((MyBaseActivity) getActivity()).finish();
					AnalyticsUtils.sendEvent("lockscreen", new AnalyticsUtils.Param("navigated", "deals_tab"));
				}
			}
		});
		return view_main;
	}



	@Override
	public void onDetach() {
		super.onDetach();
	}

	private void init() {
		if (AppSharedPreference.getInstance(getActivity().getApplicationContext()).getBoolean(AppPreferencesKeys.MAIN_ACTIVITY_STATE_VISIBLE)) {
			initMediatorManager();
			inflateAdMediator();
			AppSharedPreference.getInstance(getActivity().getApplicationContext()).setBoolean(AppPreferencesKeys.MAIN_ACTIVITY_STATE_VISIBLE, false);
		} else {
			AppSharedPreference.getInstance(getActivity().getApplicationContext()).setBoolean(AppPreferencesKeys.MAIN_ACTIVITY_STATE_VISIBLE, true);
		}
	}
	private void initMediatorManager(){
		this.mediatorManager = new AdMediatorManager();
	}

	// Embeds the child fragment dynamically
	private void inflateAdMediator() {
		if(AppSharedPreference.getInstance(getActivity()).getIsConnectedToInternet()) {
			loadNextFragment();
		}
		else{
			loadDefaultFragment();
			Log.d("Ad Mediator Manager", "Try inflate default lockscreen fragment");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		init();

		mHomeKeyLocker = new HomeKeyLocker();
		mHomeKeyLocker.lock(mActivity);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mHomeKeyLocker != null) {
			mHomeKeyLocker.unlock();
			mHomeKeyLocker = null;
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mHomeKeyLocker != null) {
			mHomeKeyLocker.unlock();
			mHomeKeyLocker = null;
		}
	}

	@Override
	public void onDestroy() {
		mCircularRing.destroyView();
		getActivity().unregisterReceiver(mReceiver);
		if (mHomeKeyLocker != null) {
			mHomeKeyLocker.unlock();
			mHomeKeyLocker = null;
		}
		super.onDestroy();
	}

	/*
    *
    * Methods required by onFailMediatorListener
    * */

	@Override
	public void onMediatorFailed() {
		inflateAdMediator();
	}

	@Override
	public void loadNextFragment() {
		loadFragment(this.mediatorManager.getNextMediator());
	}

	@Override
	public void loadDefaultFragment(){
		loadFragment(new defaultLockscreenFragment());
	}

	private void loadFragment(Fragment frag){
		childFragment = frag;
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.adMediatorContainer, childFragment).commit();
	}

	public class ScreenReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				// do whatever you need to do here
				wasScreenOn = false;


			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// and do whatever you need to do here
				wasScreenOn = true;
			}
		}
	}

	private void removeChildFragment(){
		try{
			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.remove(childFragment);
			transaction.commit();
		}catch(Exception e){
			e.printStackTrace();
			Log.e("FragmentMain","Unable to remove childFragment");
		}
	}

	/**
	 * Method called on successfully addition of credit
	 */
	public void onSuccessAddCredit(CreditResponseBean responseBean) {
		AppSharedPreference.getInstance(mActivity).setTotalCredit(responseBean.getCredits());
		((MyBaseActivity) mActivity).updateCredits();
	}

	/**
	 * Method called on Error
	 */
	public void onError() {
		Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
	}

}
