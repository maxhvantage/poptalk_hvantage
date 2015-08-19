package in.lockerapplication.fragment;



import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ciao.app.utils.AppUtils;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.poptalk.app.R;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import in.lockerapplication.adNetworks.AdMediatorFragment;
import in.lockerapplication.adNetworks.AdUnit;
import in.lockerapplication.asyncTask.AdImpressionAysncTask;
import in.lockerapplication.utility.MoPubUserParamHelper;

/**
 * Created by Simon on 6/24/2015.
 */
public class MoPubFragment extends AdMediatorFragment implements MoPubView.BannerAdListener{
    private View child_view;
    private MoPubView moPubView;
    private static String MOPUB_CUSTOM_AD_ID = "4567d163326e4b3a9e40789122a33382";
    private static String MOPUB_MEDIUM_AD_ID = "ef5096abe7c8458ca3d03847598c1c07";
    private String keywords;
    private static final String LOG_TAG = MoPubFragment.class.getSimpleName();
    public MoPubFragment() {
        super();
        setRank(AdMediatorFragment.DEFAULT_RANK);
        addNewAdUnit(new AdUnit(AdUnit.MEDIUM, MOPUB_MEDIUM_AD_ID, LOG_TAG));
        addNewAdUnit(new AdUnit(AdUnit.CUSTOM, MOPUB_CUSTOM_AD_ID, LOG_TAG));
        initializeAdUnitIterator();
    }

    /*
    Factory method for producing MoPubFragments
     */
    public static MoPubFragment newInstance(){
        MoPubFragment frag = new MoPubFragment();
        //ArrayList<AdUnit> list = new ArrayList<>();
        //list.add(new AdUnit(AdUnit.MEDIUM, MOPUB_MEDIUM_AD_ID,LOG_TAG));
        //list.add(new AdUnit(AdUnit.CUSTOM, MOPUB_CUSTOM_AD_ID, LOG_TAG));
        //Bundle b = new Bundle();
        //b.putInt(AdMediatorFragment.RANK, AdMediatorFragment.MID_RANK);
        //b.putParcelableArrayList(AdMediatorFragment.AD_LIST, list);
        //frag.setArguments(b);
        return frag;
    }

    @Override
    protected void setUpAdUnit() {
        moPubView.setAdUnitId(getCurrentAd().getAd_id());
        moPubView.setKeywords(keywords);
    }

    @Override
    protected void requestAdUnit() {
        moPubView.loadAd();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(child_view == null) {
            child_view = inflater.inflate(R.layout.fragment_mopub_lockscreen, container, false);
            moPubView = (MoPubView) child_view.findViewById(R.id.mopubAd);
            moPubView.setBannerAdListener(this);
            keywords = new MoPubUserParamHelper(getActivity()).fetchUserParams();
            Log.d(LOG_TAG, " Passing these keywords " + keywords);
            super.toggleAdUnit();
        }
        return child_view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        super.requestNewAd();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroyView();
        if(moPubView != null)
            moPubView.destroy();
    }

    @Override
    public void onBannerLoaded(MoPubView banner) {
        Log.d(LOG_TAG, "Adformat: " + banner.getAdFormat());
        Log.d(LOG_TAG,"Banner Ad Size : width: " + banner.getAdWidth() + " height: " + banner.getHeight());
        Log.d(LOG_TAG,"url "+ banner.getClickTrackingUrl());
        Log.d(LOG_TAG,"banner is shown? " + ( banner.isShown() ?  "True" : "False"));
        if(!banner.isShown() || banner.getAdWidth() == 0)
            super.onAdRequestFailed();
        else
            super.reportAdUnitDisplayed();
    }

    //MarvelViewListener methods
    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
       Log.d(LOG_TAG,errorCode.toString());
        super.onAdRequestFailed();
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        Log.d(LOG_TAG,"banner clicked");
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {
        Log.d(LOG_TAG,"banner expanded");
    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {
        Log.d(LOG_TAG, "banner collapsed");
    }

}
