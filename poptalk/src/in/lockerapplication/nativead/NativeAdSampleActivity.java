/**
 * Copyright 2014 Facebook, Inc.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web and mobile services and APIs
 * provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */

package in.lockerapplication.nativead;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.admarvel.android.ads.AdMarvelActivity;
import com.admarvel.android.ads.AdMarvelAd;
import com.admarvel.android.ads.AdMarvelInterstitialAds;
import com.admarvel.android.ads.AdMarvelInterstitialAds.AdMarvelInterstitialAdListener;
import com.admarvel.android.ads.AdMarvelUtils.ErrorReason;
import com.admarvel.android.ads.AdMarvelUtils.SDKAdNetwork;
import com.admarvel.android.ads.AdMarvelVideoActivity;
import com.poptalk.app.R;

public class NativeAdSampleActivity extends Activity implements
AdMarvelInterstitialAdListener {
	//implements AdListener
	private AdMarvelActivity adMarvelActivity = null;
	private AdMarvelVideoActivity adMarvelVideoActivity = null;

	private String _intertialSiteId = "95958";//92760
	private String _partnerId = "b3e4c9294378931a";

	protected static final String TAG = NativeAdSampleActivity.class.getSimpleName();
	private TextView nativeAdStatus;
	private LinearLayout nativeAdContainer;

//	private ImageView image;
	Random ran;
	int[] photos={R.drawable.chat_list_bg, R.drawable.chat_list_bg, R.drawable.chat_list_bg};

	private Button showNativeAdButton;
	private View adView;

	private String [] Placement_Ad_IDs = {"600904370022819_736251729821415",
			"600904370022819_737341936379061",
	"600904370022819_737342003045721"};
	private int ad_index = 0;

	public String getAd_ID(){
		return Placement_Ad_IDs[(ad_index++ % Placement_Ad_IDs.length)];
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_native_ad_demo);

		//		nativeAdContainer = (LinearLayout) findViewById(R.id.nativeAdContainer);
		//
		//		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//		adView = inflater.inflate(R.layout.ad_unit, nativeAdContainer);
		//
		//		nativeAdStatus = (TextView) findViewById(R.id.nativeAdStatus);
		showNativeAdButton = (Button)findViewById(R.id.loadNativeAdButton);

//		image=(ImageView)findViewById(R.id.iv_dummy);
//
//		ran=new Random();
//		int i=ran.nextInt(photos.length);
//		image.setImageResource(photos[i]);

		showNativeAdButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

//				int k=ran.nextInt(photos.length);
//				image.setImageResource(photos[k]);

				//				nativeAdStatus.setText("Requesting an ad...");
				//
				//				// Create a native ad request with a unique placement ID (generate your own on the Facebook app settings).
				//				// Use different ID for each ad placement in your app.
				//				nativeAd = new NativeAd(NativeAdSampleActivity.this, getAd_ID());
				//
				//				//                Test mode device hash: b31e7cbb4041a1c4c04b968511803f4
				//
				//				// Set a listener to get notified when the ad was loaded.
				//				nativeAd.setAdListener(NativeAdSampleActivity.this);
				//
				//				// When testing on a device, add its hashed ID to force test ads.
				//				// The hash ID is printed to log cat when running on a device and loading an ad.
				//				AdSettings.addTestDevice("f128768b686b0405cb414ed10d1fa033");
				//				AdSettings.addTestDevice("b31e7cbb4041a1c4c04b968511803f4");
				//
				//				// Initiate a request to load an ad.
				//				nativeAd.loadAd();
			}
		});

		//        showNativeAdListButton.setOnClickListener(new View.OnClickListener() {
		//            @Override
		//            public void onClick(View v) {
		//                Intent intent = new Intent(NativeAdSampleActivity.this, NativeAdListActivity.class);
		//                NativeAdSampleActivity.this.startActivity(intent);
		//            }
		//        });

		loadIntertialsAds();
	}


	private AdMarvelInterstitialAds adMarvelInterstitialAds;

	/**
	 * Method used to load Intertial ads inside application
	 ****/
	private void loadIntertialsAds(){

		adMarvelInterstitialAds = new AdMarvelInterstitialAds(NativeAdSampleActivity.this, 0, 0x726D6D, 0x00FF00, 0x000000);
		Map<String, Object> targetParams = new HashMap<String, Object>();
		targetParams.put("APP_VERSION", "1.0");

		AdMarvelInterstitialAds.setListener(NativeAdSampleActivity.this);
		AdMarvelInterstitialAds.setEnableClickRedirect(true);
		adMarvelInterstitialAds.requestNewInterstitialAd(NativeAdSampleActivity.this, targetParams, _partnerId,  _intertialSiteId);

	}

	@Override
	public void onRequestInterstitialAd()
	{
		Log.e("admarvel", "onRequestInterstitialAd");
		
		((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
	}

	@Override
	public void onReceiveInterstitialAd(SDKAdNetwork sdkAdNetwork,String publisherid, AdMarvelAd adMarvelAd){
		//Interstitial Ad is now available, call below API when your App is ready to display interstitial
		adMarvelInterstitialAds.displayInterstitial(NativeAdSampleActivity.this, sdkAdNetwork, publisherid, adMarvelAd);
		Log.e("admarvel", "onReceiveInterstitialAd");
		((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.GONE);

	}

	@Override
	public void onFailedToReceiveInterstitialAd(SDKAdNetwork sdkAdNetwork,
			String publisherid, int errorCode, ErrorReason errorReason) {

		Log.e("admarvel", "onFailedToReceiveInterstitialAd; errorCode: "
				+ errorCode + " errorReason: " + errorReason.toString());

		//		loadAds(_bannerSiteId);
	}

	@Override
	public void onCloseInterstitialAd() {
		Log.e("admarvel", "onCloseInterstitialAd");

		if (NativeAdSampleActivity.this.adMarvelActivity != null) {
			NativeAdSampleActivity.this.adMarvelActivity.finish();
			NativeAdSampleActivity.this.adMarvelActivity = null;
		} else if (NativeAdSampleActivity.this.adMarvelVideoActivity != null){
			NativeAdSampleActivity.this.adMarvelVideoActivity.finish();
			NativeAdSampleActivity.this.adMarvelVideoActivity = null;
		}
	}

	@Override
	public void onClickInterstitialAd(String clickUrl) {
		if (clickUrl != null) {
			Log.e("admarvel", "InterstitialClickUrl: " + clickUrl);

			//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
			//startActivity(browserIntent);
			//			Intent in= new Intent(MainActivity.this,Webview.class);
			//			in.putExtra("url", clickUrl);
			//			startActivity(in);

			//			String clicckUrl="http://ads.admarvel.com/fam/ck.php?p=__pid=b3e4c9294378931a__sid=92760__bid=1078445__cb=a271e9651c__h=1424801922__acp=fddd7df5e3b43e589b40967ab23030fe__s=532225658f9dade51ef950a39aad18a9";
			//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clicckUrl));
			//			startActivity(browserIntent);
		}
	}

	@Override
	public void onAdmarvelActivityLaunched(AdMarvelActivity a){
		Log.e("admarvel", "onAdmarvelActivityLaunched");
		NativeAdSampleActivity.this.adMarvelActivity = a;

	}

	@Override
	public void onAdMarvelVideoActivityLaunched(AdMarvelVideoActivity a) {
		Log.e("admarvel", "onAdmarvelVideoActivityLaunched");
		NativeAdSampleActivity.this.adMarvelVideoActivity = a;
	}

	@Override
	public void onInterstitialDisplayed() {
		Log.e("admarvel", "onInterstitialDisplayed");
	}
	

	//	@Override
	//	public void onError(Ad ad, AdError error) {
	//		nativeAdStatus.setText("Ad failed to load: " + error.getErrorMessage());
	//	}
	//
	//	@Override
	//	public void onAdClicked(Ad ad) {
	//		Toast.makeText(this, "Ad Clicked", Toast.LENGTH_SHORT).show();
	//	}
	//
	//	@Override
	//	public void onAdLoaded(Ad ad) {
	//		if (nativeAd == null || nativeAd != ad) {
	//			// Race condition, load() called again before last ad was displayed
	//			return;
	//		}
	//
	//		// Unregister last ad
	//		nativeAd.unregisterView();
	//
	//		nativeAdStatus.setText("");
	//
	//		inflateAd(nativeAd, adView, this);
	//
	//		// Registering a touch listener to log which ad component receives the touch event.
	//		// We always return false from onTouch so that we don't swallow the touch event (which
	//		// would prevent click events from reaching the NativeAd control).
	//		// The touch listener could be used to do animations.
	//		nativeAd.setOnTouchListener(new OnTouchListener(){
	//			@Override
	//			public boolean onTouch(View view, MotionEvent event) {
	//				if (event.getAction() == MotionEvent.ACTION_DOWN) {
	//					switch (view.getId()) {
	//					case R.id.nativeAdCallToAction:
	//						Log.d(TAG, "Call to action button clicked");
	//						break;
	//					case R.id.nativeAdImage:
	//						Log.d(TAG, "Main image clicked");
	//						break;
	//					default:
	//						Log.d(TAG, "Other ad component clicked");
	//					}
	//				}
	//				return false;
	//			}
	//		});
	//	}

	//	public static void inflateAd(NativeAd nativeAd, View adView, Context context) {
	//		// Create native UI using the ad metadata.
	//
	//		ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.nativeAdIcon);
	//		TextView nativeAdTitle = (TextView) adView.findViewById(R.id.nativeAdTitle);
	//		TextView nativeAdBody = (TextView) adView.findViewById(R.id.nativeAdBody);
	//		ImageView nativeAdImage = (ImageView) adView.findViewById(R.id.nativeAdImage);
	//		TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.nativeAdSocialContext);
	//		Button nativeAdCallToAction = (Button) adView.findViewById(R.id.nativeAdCallToAction);
	//		RatingBar nativeAdStarRating = (RatingBar) adView.findViewById(R.id.nativeAdStarRating);
	//
	//		// Setting the Text
	//		nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
	//		nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
	//		nativeAdCallToAction.setVisibility(View.VISIBLE);
	//		nativeAdTitle.setText(nativeAd.getAdTitle());
	//		nativeAdBody.setText(nativeAd.getAdBody());
	//
	//		// Downloading and setting the ad icon.
	//		NativeAd.Image adIcon = nativeAd.getAdIcon();
	//		NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
	//
	//		// Downloading and setting the cover image.
	//		NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
	//		int bannerWidth = adCoverImage.getWidth();
	//		int bannerHeight = adCoverImage.getHeight();
	//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	//		Display display = wm.getDefaultDisplay();
	//		DisplayMetrics metrics = new DisplayMetrics();
	//		display.getMetrics(metrics);
	//		int screenWidth = metrics.widthPixels;
	//		int screenHeight = metrics.heightPixels;
	//		nativeAdImage.setLayoutParams(new LinearLayout.LayoutParams(
	//				screenWidth,
	//				Math.min((int) (((double) screenWidth / (double) bannerWidth) * bannerHeight), screenHeight / 3)
	//				));
	//		NativeAd.downloadAndDisplayImage(adCoverImage, nativeAdImage);
	//
	//		NativeAd.Rating rating = nativeAd.getAdStarRating();
	//		if (rating != null) {
	//			nativeAdStarRating.setVisibility(View.VISIBLE);
	//			nativeAdStarRating.setNumStars((int) rating.getScale());
	//			nativeAdStarRating.setRating((float) rating.getValue());
	//		} else {
	//			nativeAdStarRating.setVisibility(View.GONE);
	//		}
	//
	//		// Wire up the View with the native ad, the whole nativeAdContainer will be clickable
	//		nativeAd.registerViewForInteraction(adView);
	//
	//		// Or you can replace the above call with the following function to specify the clickable areas.
	//		// nativeAd.registerViewForInteraction(nativeAdContainer, Arrays.asList(nativeAdCallToAction, nativeAdImage));
	//	}
}
