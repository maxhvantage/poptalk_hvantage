package in.lockerapplication.fragment;

import in.lockerapplication.adNetworks.AdMediatorFragment;
import in.lockerapplication.adNetworks.AdUnit;
import in.lockerapplication.bean.CreditResponseBean;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.poptalk.app.R;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;


public class FacebookFragment extends AdMediatorFragment implements AdListener{
    private View view;
    protected static final String LOG_TAG = FacebookFragment.class.getSimpleName();
    private LinearLayout nativeAdContainer;
    private AdView nativeAd;
    private String [] Placement_Ad_IDs = {
            "600904370022819_736251729821415",
            "600904370022819_737341936379061",
            "600904370022819_737342003045721"};
    private int ad_index = 0;

    public FacebookFragment() {
        super();
        setRank(AdMediatorFragment.MID_RANK);
        addNewAdUnit(new AdUnit(AdUnit.MEDIUM, Placement_Ad_IDs[0], LOG_TAG));
        addNewAdUnit(new AdUnit(AdUnit.MEDIUM, Placement_Ad_IDs[1], LOG_TAG));
        addNewAdUnit(new AdUnit(AdUnit.MEDIUM, Placement_Ad_IDs[2], LOG_TAG));
        initializeAdUnitIterator();
    }

    public static FacebookFragment newInstance(){
        return new FacebookFragment();
    }

    @Override
    protected void setUpAdUnit() {

    }

    @Override
    protected void requestAdUnit() {
        loadAdd();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_facebook_lockscreen, container, false);
        nativeAdContainer = (LinearLayout)view.findViewById(R.id.nativeAdContainer);
        super.toggleAdUnit();
        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        nativeAd.destroy();
    }
    private void loadAdd() {
        // Create a native ad request with a unique placement ID (generate your own on the Facebook app settings).
        // Use different ID for each ad placement in your app.
        nativeAd = new AdView(getActivity(),getCurrentAd().getAd_id(), AdSize.RECTANGLE_HEIGHT_250);

        // Set a listener to get notified when the ad was loaded.
        nativeAd.setAdListener(FacebookFragment.this);

        nativeAd.loadAd();
    }

    @Override
    public void onError(Ad ad, AdError error) {
        super.onAdRequestFailed();
    }

    @Override
    public void onAdClicked(Ad ad) {
        // Use this function to detect when an ad was clicked.
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Context c = getActivity();
        if (nativeAd == null || nativeAd != ad) {
            // Race condition, load() called again before last ad was displayed
            return;
        }

        if(c == null) //no longer active view prevents app crash b/c async task returns but null context
            return;

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
                        default:
                            Log.d(LOG_TAG, "Facebook ad clicked");
                    }
                }
                return false;
            }
        });

        nativeAdContainer.addView(nativeAd);
        super.reportAdUnitDisplayed();
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
