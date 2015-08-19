package in.lockerapplication.fragment;

import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.nativead.NativeAdSampleActivity;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.admarvel.android.ads.AdMarvelActivity;
import com.admarvel.android.ads.AdMarvelAd;
import com.admarvel.android.ads.AdMarvelInterstitialAds;
import com.admarvel.android.ads.AdMarvelInterstitialAds.AdMarvelInterstitialAdListener;
import com.admarvel.android.ads.AdMarvelUtils.ErrorReason;
import com.admarvel.android.ads.AdMarvelUtils.SDKAdNetwork;
import com.admarvel.android.ads.AdMarvelVideoActivity;
import com.ciao.app.utils.AnalyticsUtils;
import com.poptalk.app.R;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.utils.AppUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class FragmentClickAds extends Fragment implements InterstitialAdListener{

	private AdMarvelActivity adMarvelActivity = null;
	private AdMarvelVideoActivity adMarvelVideoActivity = null;
	private String _intertialSiteId = /*"132105"*/"95958";//92760
	private String _partnerId = "b3e4c9294378931a";
	protected static final String TAG = NativeAdSampleActivity.class.getSimpleName();
	private AdMarvelInterstitialAds adMarvelInterstitialAds;
	private View view;
	private boolean bool=false, isOperaAdmarvel=false;
	private Map<String, Object> targetParams;
	private String Intestial_image;


	private InterstitialAd interstitialFANAd;
	private String [] Placement_Ad_IDs = {
			"600904370022819_736251729821415",
			"600904370022819_737341936379061",
			"600904370022819_737342003045721"
	};
	private int ad_index = 0;

	public String getAd_ID(){
		return Placement_Ad_IDs[(ad_index++ % Placement_Ad_IDs.length)];
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_click_ads, container, false);


		adMarvelInterstitialAds = new AdMarvelInterstitialAds(getActivity(), 0, 0x726D6D, 0x00FF00, 0x000000);
		targetParams = new HashMap<String, Object>();
		targetParams.put("APP_VERSION", "1.0");
		AdMarvelInterstitialAds.setEnableClickRedirect(true);

		((Button)view.findViewById(R.id.btn_surprise_me)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
//				AppUtils.playBeep(getActivity());
//				int credits=Integer.parseInt(AppSharedPreference.getInstance(v.getContext()).getTotalCredit());
//				credits=credits+4;
//				AppSharedPreference.getInstance(v.getContext()).setTotalCredit(Integer.toString(credits));
//				((TabActivityPager)getActivity()).showCredit(credits);

				if(isOperaAdmarvel){
					isOperaAdmarvel=false;
					Intestial_image= AppConstants.FACEBOOK_INTESTIAL;
					loadFacebookIntestitial();
				}else{
					isOperaAdmarvel=true;
					Intestial_image=AppConstants.INTESTIAL_ADD;
					loadIntertialsAds();
				}
			}
		});


		AdMarvelInterstitialAds.setListener(new AdMarvelInterstitialAdListener() {
			@Override
			public void onRequestInterstitialAd()
			{
				Log.e("admarvel", "onRequestInterstitialAd");

				bool=false;
				((ProgressBar)view.findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
			}

			@Override
			public void onReceiveInterstitialAd(SDKAdNetwork sdkAdNetwork,String publisherid, AdMarvelAd adMarvelAd){
				//Interstitial Ad is now available, call below API when your App is ready to display interstitial
				adMarvelInterstitialAds.displayInterstitial(getActivity(), sdkAdNetwork, publisherid, adMarvelAd);
				Log.e("admarvel", "onReceiveInterstitialAd");
				((ProgressBar)view.findViewById(R.id.progressBar1)).setVisibility(View.GONE);

				bool=true;
			}

			@Override
			public void onFailedToReceiveInterstitialAd(SDKAdNetwork sdkAdNetwork,
					String publisherid, int errorCode, ErrorReason errorReason) {

				Log.e("admarvel", "onFailedToReceiveInterstitialAd; errorCode: "
						+ errorCode + " errorReason: " + errorReason.toString());

				bool=true;
				((ProgressBar)view.findViewById(R.id.progressBar1)).setVisibility(View.GONE);

				//		loadAds(_bannerSiteId);
			}

			@Override
			public void onCloseInterstitialAd() {
				Log.e("admarvel", "onCloseInterstitialAd");

				bool=true;

				if (adMarvelActivity != null){
					adMarvelActivity.finish();
					adMarvelActivity = null;
				}else if (adMarvelVideoActivity != null){
					adMarvelVideoActivity.finish();
					adMarvelVideoActivity = null;
				}
			}

			@Override
			public void onClickInterstitialAd(String clickUrl) {
				//if (clickUrl != null) {
				Log.e("interstitial","click interstitial");
				//AppUtils.callAddCredit(getActivity(), AppSharedPreference.getInstance(getActivity()).getUserID(), AppConstants.Advertisement, AppConstants.INTERTIAL_IMAGE, null, null);
//				AppUtils.playBeep(getActivity());
//				int credits=AppPreferences.INSTANCE.getCredits();
//				credits=credits+4;
//				AppPreferences.INSTANCE.SetCredits(credits);
//				((TabActivityPager)getActivity()).showCredit(credits);
			}
			@Override
			public void onAdmarvelActivityLaunched(AdMarvelActivity a){
				Log.e("admarvel", "onAdmarvelActivityLaunched");
				adMarvelActivity = a;
			}

			@Override
			public void onAdMarvelVideoActivityLaunched(AdMarvelVideoActivity a){
				Log.e("admarvel", "onAdmarvelVideoActivityLaunched");
				adMarvelVideoActivity = a;
			}

			@Override
			public void onInterstitialDisplayed() {
				// TODO Auto-generated method stub
				Log.e("admarvel", "onInterstitialDisplayed");

			}
		});

		return view;
	}



	/**
	 * Method used to load Intertial ads inside application
	 ****/
	private void loadIntertialsAds(){
		adMarvelInterstitialAds.requestNewInterstitialAd(getActivity(), targetParams, _partnerId,  _intertialSiteId);
	}

	/**
	 * Method used to load FACEBOOK Intertial ads
	 */
	private void loadFacebookIntestitial(){

		// Create the interstitial unit with a placement ID (generate your own on the Facebook app settings).
		// Use different ID for each ad placement in your app.
		interstitialFANAd = new InterstitialAd(getActivity(), getAd_ID());
		// Set a listener to get notified on changes or when the user interact with the ad.
		interstitialFANAd.setAdListener(FragmentClickAds.this);
		// Load a new interstitial.
		interstitialFANAd.loadAd();
	}

	@Override
	public void onDestroy(){
		//		adView.destroy();
		if (interstitialFANAd != null) {
			interstitialFANAd.destroy();
			interstitialFANAd = null;
		}
		super.onDestroy();
	}

	@Override
	public void onError(Ad ad, AdError error){
		//		if (ad == adView){
		//			adStatusLabel.setText("Ad failed to load: " + error.getErrorMessage());
		//		} else
		if (ad == interstitialFANAd) {
			//			interstitialAdStatusLabel.setText("Interstitial ad failed to load: " + error.getErrorMessage());
		}
	}

	@Override
	public void onAdLoaded(Ad ad) {
		//		if (ad == adView) {
		//			adStatusLabel.setText("");
		//		} else
		if (ad == interstitialFANAd){

			if (interstitialFANAd == null || !interstitialFANAd.isAdLoaded()) {
				// Ad not ready to show.
				//				interstitialAdStatusLabel.setText("Ad not loaded. Click load to request an ad.");
			} else {
				// Ad was loaded, show it!
				interstitialFANAd.show();
				//				interstitialAdStatusLabel.setText("");
			}
		}
	}

	@Override
	public void onInterstitialDisplayed(Ad ad) {
//		Toast.makeText(getActivity(), "Interstitial Displayed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInterstitialDismissed(Ad ad) {
//		Toast.makeText(getActivity(), "Interstitial Dismissed", Toast.LENGTH_SHORT).show();

		//Optional, cleanup.
		interstitialFANAd.destroy();
		interstitialFANAd = null;
	}

	@Override
	public void onAdClicked(Ad ad) {
		Log.e("click","add clickedd");
		AnalyticsUtils.sendEvent("lockscreen", new AnalyticsUtils.Param("clicked_ad", "facebook"));
		//AppUtils.callAddCredit(getActivity(), "109", AppConstants.Advertisement, AppConstants.FACEBOOK_INTESTIAL, null, null);
//		AppUtils.playBeep(getActivity());
//		int credits=AppPreferences.INSTANCE.getCredits();
//		credits=credits+4;
//		AppPreferences.INSTANCE.SetCredits(credits);
//		((TabActivityPager)getActivity()).showCredit(credits);
	}

	/**
	 * Method called on successfully addition of credit**/
	public void onSuccessAddCredit(CreditResponseBean responseBean){
		/*((TabActivityPager)getActivity()).updateCredits(responseBean.getCredits());
		AppSharedPreference.getInstance(getActivity()).setTotalCredit(Integer.toString(responseBean.getCredits()));*/
	}
	public void onError(){
		Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
	}

}