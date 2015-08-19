package in.lockerapplication;

import in.lockerapplication.fragment.FragmentCheckin;
import in.lockerapplication.fragment.FragmentClickAds;
import in.lockerapplication.fragment.FragmentInstallApp;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.ciao.app.utils.AnalyticsUtils;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.utils.AppUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

public class TabActivityPager extends FragmentActivity implements OnClickListener{
	private final Handler handler = new Handler();

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;

	private AdView adView;

	private String [] Placement_Ad_IDs = {"600904370022819_736251729821415",
			"600904370022819_737341936379061",
	"600904370022819_737342003045721"};


	private int ad_index = 0;

	public String getAd_ID(){
		return Placement_Ad_IDs[(ad_index++ % Placement_Ad_IDs.length)];
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_tag_layout);

		//		int credits= Integer.parseInt(AppSharedPreference.getInstance(this).getTotalCredit());
		showCredit();

		tabs = (PagerSlidingTabStrip) findViewById(R.id.lin_tabs);
		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				if(position==0){
					AnalyticsUtils.sendEvent("lockscreen_deals", new AnalyticsUtils.Param("navigated", "discover"));
				}else if(position==1){
					AnalyticsUtils.sendEvent("lockscreen_deals", new AnalyticsUtils.Param("navigated", "feeling_lucky"));
				}
				/* Update MKS*/
				/*else if(position==2){
					AnalyticsUtils.sendEvent("lockscreen_deals", new AnalyticsUtils.Param("navigated", "checkin"));
				}*/
				/*Update MKS*/
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		adapter = new MyPagerAdapter(getSupportFragmentManager());

		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);

		//		changeColor(currentColor);

		tabs.setIndicatorColor(getResources().getColor(R.color.color_app_header));
		tabs.setTextColor(getResources().getColor(R.color.textcolor_tag_text_selected));

		((ImageView)findViewById(R.id.right_menu_tab)).setOnClickListener(this);

		// Banner Ad Settle

		// Instantiate an AdView view
		adView = new AdView(this, getAd_ID(), AdSize.BANNER_HEIGHT_50);

		// Find the main layout of your activity
		LinearLayout layout = (LinearLayout)findViewById(R.id.adlayout);

		// Add the ad view to your activity layout
		layout.addView(adView);

		adView.setAdListener(new AdListener() {

			@Override
			public void onError(Ad ad, AdError error) {
				// Ad failed to load. 
				// Add code to hide the ad's view
			}

			@Override
			public void onAdLoaded(Ad ad) {
				// Ad was loaded
				// Add code to show this.jsonObject=jobs;he ad's view
				Log.e("cick","LAOD me");


			}

			@Override
			public void onAdClicked(Ad ad) {
				// Use this function to detect when an ad was clicked.
				Log.e("cick","click me");
				//			    AppUtils.callAddCredit(TabActivityPager.this, AppSharedPreference.getInstance(TabActivityPager.this).getUserID(),AppConstants.Advertisement , AppConstants.FACEBOOK_BANNER, null, null);
//				AppUtils.playBeep(TabActivityPager.this);
				//				int credits=Integer.parseInt(AppSharedPreference.getInstance(TabActivityPager.this).getTotalCredit());
				//				credits=credits+4;
				//AppPreferences.INSTANCE.SetCredits(credits);
				//				AppSharedPreference.getInstance(TabActivityPager.this).setTotalCredit(Integer.toString(credits));
				showCredit();
			}

		});
		// Request to load an ad    
		adView.loadAd();
		updateCredits();
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = {"  DISCOVER  ", "I'M FEELING LUCKY"};
		 /*Update MKS*/  //, "CHECK IN"};

		public MyPagerAdapter(FragmentManager fm){
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			if(position==0){
				return new FragmentInstallApp();
			}else if(position==1){
				return new FragmentClickAds();
			}
			/*Update MKS*/
			/*else if(position==2){
				return new FragmentCheckin();
			}*/
			/*Update MKS*/
			return null;
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.right_menu_tab:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.tv_earnCreditsInterstitial:
			((TextView)findViewById(R.id.tv_facebookNative)).setBackgroundColor(getResources().getColor(R.color.white));
			((TextView)findViewById(R.id.tv_earnCreditsInterstitial)).setBackgroundColor(getResources().getColor(R.color.bright_red));
			((ViewPager)findViewById(R.id.viewpager)).setCurrentItem(1);
			break;

		case R.id.tv_facebookNative:

			((ViewPager)findViewById(R.id.viewpager)).setCurrentItem(0);
			((TextView)findViewById(R.id.tv_facebookNative)).setBackgroundColor(getResources().getColor(R.color.bright_red));
			((TextView)findViewById(R.id.tv_earnCreditsInterstitial)).setBackgroundColor(getResources().getColor(R.color.white));
			break;

		}
	}

	/**
	 * Method used to show credit**/
	public void showCredit(){

		((TextView)findViewById(R.id.tv_total_credit)).setText(AppSharedPreference.getInstance(this).getTotalCredit());
	}

	@Override
	protected void onDestroy() {
		adView.destroy();

		super.onDestroy();
	}

	public void updateCredits(){
		((TextView)findViewById(R.id.tv_total_credit)).setText(AppSharedPreference.getInstance(this).getTotalCredit());
	}

}


