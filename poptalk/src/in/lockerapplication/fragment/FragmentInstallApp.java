package in.lockerapplication.fragment;

import org.json.JSONObject;

import in.lockerapplication.MyBaseActivity;
import in.lockerapplication.TabActivityPager;
import in.lockerapplication.asyncTask.AddCreditAsyncTask;
import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.nativead.NativeAdSampleActivity;
import in.lockerapplication.networkKeys.NetworkKeys;
import in.lockerapplication.networkcall.CheckConnection;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.utils.AppUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;

public class FragmentInstallApp  extends Fragment  implements AdListener{
	private View view;
	protected static final String TAG = NativeAdSampleActivity.class.getSimpleName();
	private TextView nativeAdStatus;
	private LinearLayout nativeAdContainer;
	private Button showNativeAdButton;
	private View adView;
	private NativeAd nativeAd;
	private String [] Placement_Ad_IDs = {
			"600904370022819_736251729821415",
			"600904370022819_737341936379061",
			"600904370022819_737342003045721"};
	private int ad_index = 0;

	public String getAd_ID(){
		return Placement_Ad_IDs[(ad_index++ % Placement_Ad_IDs.length)];
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_native_ad_demo, container, false);

		nativeAdContainer = (LinearLayout)view.findViewById(R.id.nativeAdContainer);

		inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adView = inflater.inflate(R.layout.ad_unit, nativeAdContainer);

		nativeAdStatus = (TextView) view.findViewById(R.id.nativeAdStatus);
		showNativeAdButton = (Button)view.findViewById(R.id.loadNativeAdButton);

		loadAdd();
		showNativeAdButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				loadAdd();
			}
		});
		return view;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(nativeAd.isAdLoaded() == false)
			nativeAd.destroy();
	}
	private void loadAdd() {
		nativeAdStatus.setText("Fetching some awesome...");

		// Create a native ad request with a unique placement ID (generate your own on the Facebook app settings).
		// Use different ID for each ad placement in your app.
		//AdSettings.addTestDevice("b31e7cbb4041a1c4c04b968511803f4");
		nativeAd = new NativeAd(getActivity(),getAd_ID());

		// Set a listener to get notified when the ad was loaded.
		nativeAd.setAdListener(FragmentInstallApp.this);

		nativeAd.loadAd();
	}

	@Override
	public void onError(Ad ad, AdError error) {
		nativeAdStatus.setText("Ad failed to load: " + error.getErrorMessage());
	}

	@Override
	public void onAdClicked(Ad ad) {
		// Use this function to detect when an ad was clicked.
		//AppUtils.callAddCredit(getActivity(), AppSharedPreference.getInstance(getActivity()).getUserID(), AppConstants.Advertisement, AppConstants.FACEBOOK_BANNER, null, null);
//		AppUtils.playBeep(getActivity());
		//		int credits=Integer.parseInt(AppSharedPreference.getInstance(getActivity()).getTotalCredit());
		//		credits=credits+4;
		//		AppSharedPreference.getInstance(getActivity()).setTotalCredit(Integer.toString(credits));
		//		((TabActivityPager)getActivity()).showCredit(credits);
	}

	@Override
	public void onAdLoaded(Ad ad) {
        Context c = getActivity();
        if (nativeAd == null || nativeAd != ad) {
			// Race condition, load() called again before last ad was displayed
			Toast.makeText(c, "Hold on awesomeness is coming", Toast.LENGTH_SHORT).show();
			return;
		}


		// Unregister last ad
		nativeAd.unregisterView();
		nativeAdStatus.setText("");
        if(c == null) //no longer active view prevents app crash b/c async task returns but null context
            return;
        inflateAd(nativeAd, adView, c);
        showNativeAdButton.setVisibility(View.VISIBLE);


		// Registering a touch listener to log which ad component receives the touch event.
		// We always return false from onTouch so that we don't swallow the touch event (which
		// would prevent click events from reaching the NativeAd control).
		// The touch listener could be used to do animations.
		nativeAd.setOnTouchListener(new OnTouchListener() 
		{
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					switch (view.getId()) {
					case R.id.nativeAdCallToAction:
						Log.d(TAG, "Call to action button clicked");
						break;
					case R.id.nativeAdImage:
						Log.d(TAG, "Main image clicked");
						break;
					default:
						Log.d(TAG, "Other ad component clicked");
					}
				}
				return false;
			}
		});
	}


	public static void inflateAd(NativeAd nativeAd, View adView, Context context) {
		// Create native UI using the ad metadata.

		ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.nativeAdIcon);
		TextView nativeAdTitle = (TextView) adView.findViewById(R.id.nativeAdTitle);
		TextView nativeAdBody = (TextView) adView.findViewById(R.id.nativeAdBody);
		ImageView nativeAdImage = (ImageView) adView.findViewById(R.id.nativeAdImage);
		TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.nativeAdSocialContext);
		Button nativeAdCallToAction = (Button) adView.findViewById(R.id.nativeAdCallToAction);
		RatingBar nativeAdStarRating = (RatingBar) adView.findViewById(R.id.nativeAdStarRating);

		// Setting the Text
		nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
		nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
		nativeAdCallToAction.setVisibility(View.VISIBLE);
		nativeAdTitle.setText(nativeAd.getAdTitle());
		nativeAdBody.setText(nativeAd.getAdBody());

		// Downloading and setting the ad icon.
		NativeAd.Image adIcon = nativeAd.getAdIcon();
		NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

		// Downloading and setting the cover image.
		NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
		int bannerWidth = adCoverImage.getWidth();
		int bannerHeight = adCoverImage.getHeight();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;
		nativeAdImage.setLayoutParams(new LinearLayout.LayoutParams(
				screenWidth,
				Math.min((int) (((double) screenWidth / (double) bannerWidth) * bannerHeight), screenHeight / 3)
				));
		NativeAd.downloadAndDisplayImage(adCoverImage, nativeAdImage);

		NativeAd.Rating rating = nativeAd.getAdStarRating();
		if (rating != null) {
			nativeAdStarRating.setVisibility(View.VISIBLE);
			nativeAdStarRating.setNumStars((int) rating.getScale());
			nativeAdStarRating.setRating((float) rating.getValue());
		} else {
			nativeAdStarRating.setVisibility(View.GONE);
		}

		// Wire up the View with the native ad, the whole nativeAdContainer will be clickable
		nativeAd.registerViewForInteraction(adView);

		// Or you can replace the above call with the following function to specify the clickable areas.
		// nativeAd.registerViewForInteraction(nativeAdContainer, Arrays.asList(nativeAdCallToAction, nativeAdImage));
	}

	/**
	 * Method called on successfully addition of credit**/
	public void onSuccessAddCredit(CreditResponseBean responseBean){
		//((TabActivityPager)getActivity()).updateCredits(responseBean.getCredits());
		//        AppPreferences.INSTANCE.initAppPreferences(getActivity());

		//AppSharedPreference.getInstance(getActivity()).setTotalCredit(Integer.toString(responseBean.getCredits()));
	}
	public void onError(){
		Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
	}

}