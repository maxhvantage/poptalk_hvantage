package com.ciao.app.views.activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ciao.app.adapters.SplashPagerAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.netwrok.NetworkStatusBroadcastReceiver;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.GetDeviceTokenInBackGroud;
import com.ciao.app.netwrok.backgroundtasks.UserSyncAsyncTaskLoader;
import com.ciao.app.utils.AnimationUtils;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.ciao.app.views.customviews.viewpagerindicator.CirclePageIndicator;
import com.facebook.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mopub.mobileads.FacebookBanner;
import com.poptalk.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import in.lockerapplication.service.LockScreenService;


//This is landing screen of the app.This screen will show phone bounce animation if user is not logged in app.
public class SplashActivity extends FragmentActivity implements AnimationListener{
	private ViewPager mViewPager;
	private SplashPagerAdapter mSplashPagerAdapter;
	private CirclePageIndicator mCirclePageIndicator;
	private ImageView appLogoIV,appLogoIV1,appLogoIV2;
	private Handler handler = new Handler();
	private Handler flipHandler = new Handler();
	private Animation animation;
	private Runnable runnable,screenFlipRunnable;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private GoogleCloudMessaging gcm;
	private String gcmRegId;
	private LinearLayout mSignLL,mLoginLL;
    private LoaderManager.LoaderCallbacks<String> syncLoaderCallback;
    private RequestBean mRequestBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_loading_screen);

        mRequestBean = new RequestBean();
        mRequestBean.setActivity(this);
        mRequestBean.setLoader(true);

		startService(new Intent(SplashActivity.this, LockScreenService.class));
		/*startService(new Intent(this, ContactSyncService.class));*/
		sendBroadcast(new Intent(this, NetworkStatusBroadcastReceiver.class)); //check internet connection on start
		intViews();
		createAppFolderOnSdcard();

		generateFBKeyHash();
        intLoader();

		if(checkPlayServices()){
			gcm = GoogleCloudMessaging.getInstance(this);
			gcmRegId = AppSharedPreference.getInstance(this).getDeivceToken();
			if(gcmRegId.isEmpty()||(gcmRegId.equalsIgnoreCase("12345"))){
				registerGCMInBackground();
			}
			setGcmRegId(gcmRegId);
			Log.d("DeviceId",gcmRegId);
			syncUserData();
		}
		screenFlipRunnable = new Runnable() {

			@Override
			public void run() {
				mViewPager.setCurrentItem(1, true);
				mLoginLL.setPressed(true);
			}
		};
		runnable = new Runnable() {
			@Override
			public void run() {
				if (AppSharedPreference.getInstance(SplashActivity.this).getAlreadyLoginFlag()) {
					if (AppSharedPreference.getInstance(SplashActivity.this).getAlreadyVerified()) {
						startActivity(new Intent(SplashActivity.this,CallActivity.class));
						AppUtils.getFacebookLogger(SplashActivity.this).logEvent("active");
					} else {
						startActivity(new Intent(SplashActivity.this, RegistrationNextActivity.class));
						AppUtils.getFacebookLogger(SplashActivity.this).logEvent("install");
					}
					finish();

				} else {
					setContentView(R.layout.activity_splash);
					appLogoIV = (ImageView) findViewById(R.id.iv_app_logo);
					mViewPager = (ViewPager) findViewById(R.id.vpPager);
					mSignLL = (LinearLayout)findViewById(R.id.l_sign_up);
					mLoginLL = (LinearLayout)findViewById(R.id.l_log_in);
					mSplashPagerAdapter = new SplashPagerAdapter(getSupportFragmentManager());
					mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.vpi_circle);
					mViewPager.setAdapter(mSplashPagerAdapter);
					mCirclePageIndicator.setViewPager(mViewPager);
					// registerReceiver(new ContactSyncBroadCastReceiver(),new
					// IntentFilter(ContactSyncBroadCastReceiver.PROCESS_RESPONSE));
					mSignLL.setPressed(true);
					mViewPager	.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
						@Override
						public void onPageScrolled(int position,
												   float positionOffset,
												   int positionOffsetPixels) {

						}

						@Override
						public void onPageSelected(int position) {
							if(position==1){
								mLoginLL.setPressed(true);
								mSignLL.setPressed(false);
							}else if (position==0) {
								mSignLL.setPressed(true);
								mLoginLL.setPressed(false);
							}
						}

						@Override
						public void onPageScrollStateChanged(int state) {

						}
					});
					flipHandler.postDelayed(screenFlipRunnable, 10000);

				}

			}
		};

		if(!AppSharedPreference.getInstance(this).getAlreadyLoginFlag()){
			handler.postDelayed(runnable, 3000);
		}else{
			handler.postDelayed(runnable, 0);
			if(!AppSharedPreference.getInstance(this).getIsConnectedToInternet())
				DialogUtils.showInternetAlertDialog(SplashActivity.this);
		}
		if(!AppSharedPreference.getInstance(this).getCiaoRatesSynced()){
			//AppUtils.smsCallPrices(this);
		}

	}

    private void intLoader() {
        syncLoaderCallback = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new UserSyncAsyncTaskLoader(mRequestBean);
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                if (loader instanceof UserSyncAsyncTaskLoader) {
                    ((UserSyncAsyncTaskLoader) loader).hideLoaderDialog();
                    try {
                        JSONObject serverResponseJsonObject = new JSONObject(data);
                        //String errorCode = serverResponseJsonObject.getString("error_code");
                        Log.d("UserSync", "User sync has been executed with the response "+serverResponseJsonObject.toString()+" -- device ID is "+getGcmRegId());

                    } catch (JSONException e) {
                        //e.printStackTrace();
                        Log.d("UserSync", "Failed on splash with error " + e.toString());
                    }

                } else {
                    Log.d("UserSync", "User sync on else");
                }

            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        };
    }

	private void syncUserData(){

        Log.d("UserSync", "function executed on splash");
        JSONObject jsonObject = new JSONObject();
        try {
            //com.csipsimple.utils.Log.d("Login", "Login on try");
            jsonObject.put("user_security",this.getString(R.string.user_security_key));
            jsonObject.put("user_device_token",getGcmRegId());
            jsonObject.put("user_id",AppSharedPreference.getInstance(this).getUserID());
            mRequestBean.setJsonObject(jsonObject);
            getLoaderManager().restartLoader(0, null, syncLoaderCallback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

	private void intViews() {
		// TODO Auto-generated method stub

		appLogoIV  = (ImageView)findViewById(R.id.iv_app_logo);
		appLogoIV1 = (ImageView)findViewById(R.id.imageView1);
		appLogoIV2 = (ImageView)findViewById(R.id.imageView2);
		animation = AnimationUtils.bounceAppLogoAnimation();
		animation.setAnimationListener(this);
		//mProgressBar.startAnimation(animation);
		appLogoIV.startAnimation(animation);
		appLogoIV1.startAnimation(AnimationUtils.leftToRight());
		appLogoIV2.startAnimation(AnimationUtils.rightToLeft());



	}

	public void signUp(View view) {
		Intent registrationIntent = new Intent(this, RegistrationActivity.class);
		startActivityForResult(registrationIntent, 1002);

	}

	public void login(View view) {
		Intent loginIntent = new Intent(this, LoginActivity.class);
		startActivityForResult(loginIntent, 1001);
	}

	/*
	 * General Method to generate Hash-key for facebook app. Not to be used
	 * everytime.
	 */
	public void generateFBKeyHash() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.poptalk.app", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:",Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private void createAppFolderOnSdcard() {

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File appFolder = new File(AppConstants.APP_IMAGE_BASE_FOLDER);
			if (!appFolder.exists()) {
				appFolder.mkdirs();
			}

		}

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		/*
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(appLogoIV, "scaleX", 3.0f);
		scaleXAnimator.setDuration(1000);
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(appLogoIV, "scaleY", 4.0f);
		scaleYAnimator.setDuration(1000);
		AnimatorSet set = new AnimatorSet();
		set.playTogether(scaleXAnimator, scaleYAnimator);
		set.start();*/


	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		handler.removeCallbacks(runnable);
		flipHandler.removeCallbacks(screenFlipRunnable);
	}

	private void registerGCMInBackground(){
		new GetDeviceTokenInBackGroud(this).execute();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("Ciao", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
		if(mViewPager!=null){
			if(mViewPager.getCurrentItem()==1){
				mLoginLL.setPressed(true);
				mSignLL.setPressed(false);
			}else if (mViewPager.getCurrentItem()==0) {
				mSignLL.setPressed(true);
				mLoginLL.setPressed(false);
				flipHandler.postDelayed(screenFlipRunnable, 10000);
			}
		}

	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1001){
			if(resultCode == RESULT_OK){
				finish();
			}
		}
		if(requestCode==1002){
			if(resultCode == RESULT_OK){
				finish();
			}
		}
	}

	public String getGcmRegId() {
		return gcmRegId;
	}

	public void setGcmRegId(String gcmRegId) {
		this.gcmRegId = gcmRegId;
	}
}