package in.lockerapplication.receiver;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;

import in.lockerapplication.MyBaseActivity;
import in.lockerapplication.preferences.AppPreferencesKeys;
import in.lockerapplication.service.LockScreenService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * this is broadcast receiver class use to handle Screen locking functionality
 * through this class we check that which user (Guest or Master) is accessing
 * phone
 */
public class BootReceiver extends BroadcastReceiver  
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) 
		{
			if(AppSharedPreference.getInstance(context).getAdLockScreenVisibility()){
				Intent serviceIntent = new Intent(context, MyBaseActivity.class);
				serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(serviceIntent);
				context.startService(new Intent(context, LockScreenService.class));
			}
			
		}
	}
}