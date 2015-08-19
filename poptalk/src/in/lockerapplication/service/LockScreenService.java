package in.lockerapplication.service;

import in.lockerapplication.receiver.LockScreenReceiver;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.ciao.app.utils.AppUtils;

/**
 * this is a service class use to register receiver for screen off and on.
 * through this class screen off and on functionality and changes in any
 * provider will reflect from here.
 */
public class LockScreenService extends Service {
	private BroadcastReceiver mReceiver;
	KeyguardManager.KeyguardLock mKeyguardLock;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("-LockScreenService", "test on create");
		super.onCreate();
		mReceiver = new LockScreenReceiver();
		try {
			mKeyguardLock = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).newKeyguardLock("PopTalk");
			mKeyguardLock.disableKeyguard();

			IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.setPriority(1986);
			registerReceiver(mReceiver, filter);

			AppUtils.getFacebookLogger(this).logEvent("lockscreen");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}
