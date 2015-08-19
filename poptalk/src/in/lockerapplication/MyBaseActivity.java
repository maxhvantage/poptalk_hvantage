package in.lockerapplication;

import in.lockerapplication.fragment.FragmentMain;
import in.lockerapplication.fragment.MenuFragment;
import in.lockerapplication.preferences.AppPreferencesKeys;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ciao.app.service.GetTotalCredit;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * This is the base class to handle all the fragments.
 */
public class MyBaseActivity extends FragmentActivity implements
		OnClickListener {
	private Fragment mFrag;
	private int index = 1;
	private TextView mTotalCreditTV;
	private boolean mIsDestroyedUsingCallBroadcast;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() != null
					&& intent.getAction().equals(
							getString(R.string.intent_filter_kill_activity))) {
				mIsDestroyedUsingCallBroadcast = true;
				finish();
			}
		}
	};

	protected void onResume() {
		super.onResume();
	}

	@Override
	public void overridePendingTransition(int enterAnim, int exitAnim) {
		Log.e("000000000000000", "jjjjjjjjjjjjjjjjjjj");
		super.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.container_layout);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		updateCredits();
		
		mIsDestroyedUsingCallBroadcast = false;
		LocalBroadcastManager
				.getInstance(MyBaseActivity.this)
				.registerReceiver(
						mBroadcastReceiver,
						new IntentFilter(
								getString(R.string.intent_filter_kill_activity)));

		if (mFrag == null) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			startService(new Intent(this, GetTotalCredit.class));
			mFrag = new FragmentMain();
		}

		

		mTotalCreditTV = (TextView)findViewById(R.id.tv_total_credit);
		mTotalCreditTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
		mTotalCreditTV.setOnClickListener(this);
		//MenuFragment menuFragment = new MenuFragment();

		//setBehindContentView(R.layout.menu_frame);
		//getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		//FragmentTransaction fragmentTransaction = getSupportFragmentManager()
		//		.beginTransaction();
		//fragmentTransaction.replace(R.id.menu_frame, menuFragment);
		//fragmentTransaction.commit();

		//SlidingMenu sm = getSlidingMenu();
		//sm.setSecondaryMenu(R.layout.menu_frame_right);
		//sm.setSlidingEnabled(false);
		//sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mFrag, "one").commit();
		//sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		//sm.setShadowWidthRes(R.dimen.shadow_width);
		//sm.setShadowDrawable(R.drawable.shadow);
		//sm.setMode(SlidingMenu.RIGHT);
		//sm.setBehindScrollScale(0.25f);
		//sm.setFadeDegree(0.25f);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//
//		case R.id.tv_total_credit:
//			toggle();
//			showSecondaryMenu();
//			break;
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		if (mFrag instanceof FragmentMain) {
			// do nothing
			return;
		}
		super.onBackPressed();
		finish();
	}

	/**
	 * Method to switch Fragment based on index
	 * 
	 * @param tag
	 * @param index
	 */
	public void switchContent(String tag, int index) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		if (this.index != index) {
			this.index = index;
			if (fragmentManager.findFragmentByTag(tag) == null) {
				switch (index) {

				default:
					break;
				}
			} else {
				mFrag = fragmentManager.findFragmentByTag(tag);
			}
			fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
			fragmentTransaction.replace(R.id.content_frame, mFrag, tag);
			fragmentTransaction.commitAllowingStateLoss();
			fragmentManager.executePendingTransactions();
		}
		//toggle();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 101 && resultCode == RESULT_OK) {
			//toggle();
			//showSecondaryMenu();
		} else if (requestCode == 101 && resultCode == 0) {
			startActivity(getIntent());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_HOME)
		{
			Log.d("Test", "Home button pressed!");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		if (!mIsDestroyedUsingCallBroadcast)
		    AppSharedPreference.getInstance(this).setBoolean(AppPreferencesKeys.MAIN_ACTIVITY_STATE_VISIBLE, false);
		LocalBroadcastManager.getInstance(MyBaseActivity.this).unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	/**
	 * Method used to finish current activity
	 */
	public void finishActivity() {
		finish();
	}

	public void updateCredits() {
		((TextView) findViewById(R.id.tv_total_credit)).setText(AppSharedPreference.getInstance(this).getTotalCredit());
	}
}
