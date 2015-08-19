package com.ciao.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.broadcastreceiver.ContactSyncBroadCastReceiver.ContactSyncListener;
import com.ciao.app.utils.AnalyticsUtils;
import com.facebook.AppEventsLogger;
import com.flurry.android.FlurryAgent;

/**
 * Created by rajat on 9/3/15.
 */
public class CiaoApplication extends Application {


	private ContactSyncListener mContactSyncListener;
	private static CiaoApplication application;
	@Override
	public void onCreate() {
		super.onCreate();
		AppEventsLogger.activateApp(this);
		AnalyticsUtils.startAnalytics(this);
		AppSharedPreference.getInstance(getApplicationContext());
		initImageLoader(getApplicationContext());
		application = this;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		AppEventsLogger.deactivateApp(this);
	}

	public void setContactSyncListener(ContactSyncListener mContactSyncListener){
		this.mContactSyncListener = mContactSyncListener;
	}
	
	public ContactSyncListener getContactSyncListener(){
		return this.mContactSyncListener;
	}
	
	public static CiaoApplication getInstance(){
		return application;
	}
}
