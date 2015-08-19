package in.lockerapplication.receiver;

import com.ciao.app.apppreference.AppSharedPreference;

import in.lockerapplication.MyBaseActivity;
import in.lockerapplication.preferences.AppPreferencesKeys;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * this is broadcast receiver class use to handle Screen locking functionality
 * through this class we check that which user (Guest or Master) is accessing
 * phone
 */
public class LockScreenReceiver extends BroadcastReceiver  
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
		{
			Log.d("Lockscreen", "starting");
			if(AppSharedPreference.getInstance(context).getAdLockScreenVisibility() && AppSharedPreference.getInstance(context).getAlreadyVerified()){
				Intent serviceIntent = new Intent(context, MyBaseActivity.class);
				serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(serviceIntent);
			}
			
		}
	}
}