package in.lockerapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admarvel.android.ads.AdMarvelAd;
import com.admarvel.android.ads.AdMarvelUtils;
import com.admarvel.android.ads.AdMarvelVideoEventListener;
import com.admarvel.android.ads.AdMarvelView;
import com.poptalk.app.R;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import in.lockerapplication.Webview;
import in.lockerapplication.adNetworks.AdMediatorFragment;
import in.lockerapplication.adNetworks.AdUnit;
import in.lockerapplication.asyncTask.AdImpressionAysncTask;
import in.lockerapplication.utility.AdmarvelUserParamHelper;

/**
 * Created by Simon on 6/24/2015.
 */
public class AdmarvelFragment extends AdMediatorFragment
        implements
            AdMarvelView.AdMarvelViewListener,
            AdMarvelView.AdMarvelViewExtendedListener,
            AdMarvelVideoEventListener
{
    private AdMarvelView adMarvelView;
    private View childView;
    private static final String LOCKSCREEN_BACKFILL = "127454";
    private static final String LOCKSCREEN_FULLPAGE = "116189";
    private String _partnerId = "b3e4c9294378931a";
    private String adColonyId = "1.0|app89c4c924f8b9489f84|vza5c9ecbedd30401686";
    private Map<String,Object> targetParams;
    private static final String LOG_TAG = AdmarvelFragment.class.getSimpleName();
    public AdmarvelFragment() {
        super();
        setRank(AdMediatorFragment.HIGH_RANK);
        addNewAdUnit(new AdUnit(AdUnit.BANNER, LOCKSCREEN_BACKFILL, LOG_TAG));
        AdUnit ad = new AdUnit(AdUnit.CUSTOM, LOCKSCREEN_FULLPAGE, LOG_TAG);
        addNewAdUnit(ad);
        setCurrentAd(ad);
        initializeAdUnitIterator();
    }

    /*
    /Factory method for producing AdMarvel Fragments
     */
    public static AdmarvelFragment newInstance(){
        AdmarvelFragment frag = new AdmarvelFragment();
        Bundle b = new Bundle();
        b.putInt(AdMediatorFragment.RANK, AdMediatorFragment.HIGH_RANK);
        frag.setArguments(b);
        return frag;
    }

    @Override
    protected void setUpAdUnit() {
    }

    @Override
    protected void requestAdUnit() {
        adMarvelView.requestNewAd(
                targetParams,
                _partnerId,
                getCurrentAd().getAd_id(),
                getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        childView = inflater.inflate(R.layout.fragment_admarvel_lockscreen,container, false);
        adMarvelView = (AdMarvelView) childView.findViewById(R.id.ad);
        adMarvelView.setEnableClickRedirect(false);
        adMarvelView.setDisableAnimation(false);
        adMarvelView.setEnableAutoScaling(false);
        AdMarvelUtils.enableLogging(true);
        adMarvelView.setListener(this);
        adMarvelView.setExtendedListener(this);
        adMarvelView.setVideoEventListener(this);
        // Initializing SDK
        Map<AdMarvelUtils.SDKAdNetwork, String> publisherIds = new HashMap<AdMarvelUtils.SDKAdNetwork, String>();
        publisherIds.put(AdMarvelUtils.SDKAdNetwork.ADCOLONY, adColonyId);
        AdMarvelUtils.initialize(getActivity(), publisherIds);
        targetParams = new AdmarvelUserParamHelper(getActivity()).fetchUserParams();
        adMarvelView.start(getActivity());
        adMarvelView.setBackgroundColor(getResources().getColor(R.color.transparent));
        return childView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        adMarvelView.start(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        super.requestNewAd();
        Log.d(LOG_TAG, "onResume");
        adMarvelView.resume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        adMarvelView.pause(getActivity());

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        adMarvelView.stop(getActivity());


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
        if(adMarvelView != null)
            adMarvelView.destroy();

    }

    //AdmarvelViewListener Methods



    @Override
    public void onReceiveAd(AdMarvelView adMarvelView) {
        Log.d(LOG_TAG, "onReceiveAd");
        adMarvelView.focus();
    }
    @Override
    public void onRequestAd(AdMarvelView adMarvelView) {
        Log.d(LOG_TAG, "onRequestAd");
    }

    @Override
    public void onFailedToReceiveAd(AdMarvelView adMarvelView,
                                    int errorCode, AdMarvelUtils.ErrorReason errorReason) {
        Log.d(LOG_TAG,
                "onFailedToReceiveAd; errorCode: " + errorCode
                        + " errorReason: " + errorReason.toString());
        if(!(errorCode == 304 && errorReason.equals("AD_REQUEST_IN_PROCESS_EXCEPTION")))
            super.onAdRequestFailed();
    }

    @Override
    public void onExpand() {
        Log.d(LOG_TAG, "onExpand");
    }

    @Override
    public void onClose() {
        Log.d(LOG_TAG, "onClose");
    }

    @Override
    public void onClickAd(AdMarvelView adMarvelView, final String clickUrl) {
        if (clickUrl != null) {
            Log.d(LOG_TAG, "ClickUrl: " + clickUrl);
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Intent in = new Intent(getActivity(), Webview.class);
                    in.putExtra("url", "" + clickUrl);
                    getActivity().startActivity(in);
                }
            });
        }
    }

    /*
    * AdmarvelViewExtendedListener
    * */
    @Override
    public void onAdFetched(AdMarvelView adMarvelView, AdMarvelAd adMarvelAd) {
        Log.d(LOG_TAG,"onAdFetched");
    }

    @Override
    public void onAdDisplayed(AdMarvelView adMarvelView) {
        if(adMarvelView.isShown())
            super.reportAdUnitDisplayed();
    }

    /*
    *
    * Admarvel VideoEvent Listener Methods
    * */
    @Override
    public void onAudioStart() {
        Log.d(LOG_TAG, "Video Event Audio Started");
        // should try to mute by default;
    }

    @Override
    public void onAudioStop() {
        Log.d(LOG_TAG, "Video Event Audio Ended");
        adMarvelView.requestNewAd(targetParams,_partnerId, getCurrentAd().getAd_id(),getActivity());
    }
}
