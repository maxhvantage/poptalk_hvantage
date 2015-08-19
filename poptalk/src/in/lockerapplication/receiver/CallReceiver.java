package in.lockerapplication.receiver;

import in.lockerapplication.MyBaseActivity;
import in.lockerapplication.preferences.AppPreferencesKeys;
import in.lockerapplication.service.LockScreenService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;

/**
 * this is broadcast receiver class use to handle Screen locking functionality
 * through this class we check that which user (Guest or Master) is accessing
 * phone
 */
public class CallReceiver extends BroadcastReceiver {

	private AppSharedPreference mAppPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		mAppPreferences = AppSharedPreference.getInstance(context);
		try {
			// TELEPHONY MANAGER class object to register one listner
			TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			// Create Listner
			MyPhoneStateListener PhoneListener = new MyPhoneStateListener(context);
			// Register listener for LISTEN_CALL_STATE
			tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

		} catch (Exception e) {
			Log.e("Phone Receive Error", " " + e);
		}

	}

	class MyPhoneStateListener extends PhoneStateListener {
		private Context mContext;

		public MyPhoneStateListener(Context context){
			mContext = context;
		}

		public void onCallStateChanged(int state, String incomingNumber) {

			Log.d("MyPhoneListener", state + "   incoming no:" + mAppPreferences.getInt(AppPreferencesKeys.LAST_CALL_STATE));

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Log.e("CALL_STATE_RINGING", "CALL_STATE_RINGING");
				mContext.stopService(new Intent(mContext, LockScreenService.class));
				mAppPreferences.setInt(AppPreferencesKeys.LAST_CALL_STATE, TelephonyManager.CALL_STATE_RINGING);
				Intent intent = new Intent(mContext.getString(R.string.intent_filter_kill_activity));
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				Log.e("CALL_STATE_IDLE", "CALL_STATE_IDLE");
				mAppPreferences.setInt(AppPreferencesKeys.LAST_CALL_STATE, TelephonyManager.CALL_STATE_IDLE);
				if(! AppSharedPreference.getInstance(mContext).getAlreadyLoginFlag())
					break; //don't start lockscreen service or base activity view if not logged in
				else if (mAppPreferences.getInt(AppPreferencesKeys.LAST_CALL_STATE) == TelephonyManager.CALL_STATE_RINGING
						&& mAppPreferences.getBoolean(AppPreferencesKeys.MAIN_ACTIVITY_STATE_VISIBLE)) {
					Intent serviceIntent = new Intent(mContext, MyBaseActivity.class);
					serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mContext.startActivity(serviceIntent);
				}
				mContext.startService(new Intent(mContext, LockScreenService.class));

				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.e("CALL_STATE_OFFHOOK", "CALL_STATE_OFFHOOK");
				// mLastCallState=TelephonyManager.CALL_STATE_OFFHOOK;
				break;

			}
		}
	}
}