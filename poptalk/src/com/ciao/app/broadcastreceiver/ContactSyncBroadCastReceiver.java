package com.ciao.app.broadcastreceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ciao.app.CiaoApplication;
import com.ciao.app.service.ContactSyncService;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.InterstialContactListActivity;

/**
 * Created by rajat on 11/2/15.
 * This class notify the app about contact sync operation progress.
 */
public class ContactSyncBroadCastReceiver extends BroadcastReceiver {
	public static final String PROCESS_RESPONSE = "com.ciao.app.intent.action.PROCESS_RESPONSE";

	public interface ContactSyncListener{
		public void loading();
		public void loaded();
	}

	public ContactSyncListener mContactSyncListener;
	@Override
	public void onReceive(Context context, Intent intent) {
		int status = intent.getIntExtra(ContactSyncService.KEY_STATUS,1);
		mContactSyncListener = CiaoApplication.getInstance().getContactSyncListener();
		if(status == 0){
			if(mContactSyncListener!=null){
				mContactSyncListener.loading();	
			}

		}else{
			if(mContactSyncListener!=null){
				mContactSyncListener.loaded();	
			}
		}
		
	}

	public void setContactSyncListener(ContactSyncListener mContactSyncListener){
		this.mContactSyncListener = mContactSyncListener;
		CiaoApplication.getInstance().setContactSyncListener(mContactSyncListener);
	}

}
