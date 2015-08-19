package in.lockerapplication.adNetworks;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import in.lockerapplication.asyncTask.AdImpressionAysncTask;

/**
 * Created by Simon on 6/24/2015.
 * Should be loaded as Child Fragment always
 * Parent Fragment should implement onFailMediatorListener
 */
public abstract class AdMediatorFragment extends Fragment{
    protected static final Integer DEFAULT_RANK = 0;
    protected static final Integer LOW_RANK = -1;
    protected static final Integer MID_RANK = 1;
    protected static final Integer HIGH_RANK = 2;
    protected static final String RANK = "rank";
    protected static final String AD_LIST = "adUnitList";
    protected static final int AD_UNIT_TIMEOUT = 2500;
    protected static String LOG_TAG = AdMediatorFragment.class.getSimpleName();
    private int AD_UNIT_REQUEST_FAILED_COUNTER;
    private static final int AD_UNIT_REQUEST_FAILED_LIMIT = 3;
    private static Handler timeOutAdRequestHandler;
    private Integer rank;
    private TreeSet<AdUnit> sortableAdUnitSet;
    private Iterator<AdUnit> adUnitIterator;
    protected AdUnit currentAd;
    private boolean isAdUnitDisplayed;

    public AdMediatorFragment(){
        resetRequestCounter();
        sortableAdUnitSet = new TreeSet<AdUnit>(new AdUnitComparator());
        if(timeOutAdRequestHandler == null) {
            Log.d(LOG_TAG,"create timeout handler");
            timeOutAdRequestHandler = new Handler();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(this.getParentFragment() == null){
            throw new IllegalStateException(this.toString()
                    + " needs to be inflated as a Child Fragment");
        }
        else if((this.getParentFragment() instanceof onFailMediatorListener)){
        }
        else
            throw new ClassCastException(this.getParentFragment().toString()
                    + " needs to Implement AdMediatorFragment.onFailMediatorListener");
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Bundle b = getArguments();
//        if(b != null){
//            setRank(b.getInt(RANK, DEFAULT_RANK));
//            ArrayList<AdUnit> list = b.getParcelableArrayList(AD_LIST);
//            setAdUnitsFromParcels(list);
//            Log.d("AdMediatorFragment","Bundle Size " + b.keySet().size());
//            Log.d("AdMediatorFragment","Bundle RANK " + b.getInt(RANK));
//        }
//        initializeAdUnitIterator();
//    }


    public Integer getRank() {
        return rank;
    }

    protected void setRank(Integer rank){
        this.rank = rank;
    }

    protected void setIsAdUnitDisplayed(boolean b){
        this.isAdUnitDisplayed = b;
    }

    protected void setAdUnitsFromParcels(ArrayList<AdUnit> adUnits){
        if(adUnits == null)
            return;
        for(AdUnit u : adUnits){
            addNewAdUnit(u);
        }
    }

    protected void addNewAdUnit(AdUnit unit){
        sortableAdUnitSet.add(unit);
    }

    protected void removeAdUnit(AdUnit unit){
        sortableAdUnitSet.remove(unit);
    }

    protected AdUnit getNextAdUnit(){
        if(!adUnitIterator.hasNext())
            throw new NoSuchElementException("Should try to reinitialize iterator or switch mediators now. No more ad units.");
        setCurrentAd(adUnitIterator.next());
        return currentAd;
    }

    protected AdUnit getCurrentAd(){
        return currentAd;
    }

    protected void setCurrentAd(AdUnit ad){
        this.currentAd = ad;
    }

    protected void initializeAdUnitIterator(){
        adUnitIterator = sortableAdUnitSet.iterator();
    }

    protected void setComparator(Comparator<AdUnit> comp){
       TreeSet<AdUnit> updatedSet = new TreeSet<AdUnit>(comp);
        updatedSet.addAll(sortableAdUnitSet);
        sortableAdUnitSet = updatedSet;
    }

    protected void toggleAdUnit(){
        try{
            AdUnit currentAd = getNextAdUnit();
            Log.d(LOG_TAG, "Switched Ad Unit to " + currentAd.getAd_id());
            setUpAdUnit();
            requestNewAd();
        }
        catch (NoSuchElementException e){
            switchMediator();
        }

    }

    protected void requestNewAd(){
        requestAdUnit();
        //startAdUnitTimeOut(AD_UNIT_TIMEOUT);
    }

    protected abstract void setUpAdUnit();

    protected abstract void requestAdUnit();

    protected void startAdUnitTimeOut(int timeout){
        isAdUnitDisplayed = false;
        timeOutAdRequestHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                onAdRequestFailed();
            }
        },timeout); //delay in milliseconds
    }

    protected void onAdRequestFailed(){
        if (isAdUnitDisplayed == false) {
            AD_UNIT_REQUEST_FAILED_COUNTER++;
            Log.d("AdMediatorFragment","ad load times failed : " + AD_UNIT_REQUEST_FAILED_COUNTER+"");
            if(AD_UNIT_REQUEST_FAILED_COUNTER >= AD_UNIT_REQUEST_FAILED_LIMIT)
                loadBackupFragment();
            toggleAdUnit();
        }
    }

    /*
    * Call the method the parent fragment must implement to switch child fragments
    *
    * */
    protected void switchMediator(){
        try{
        if(this.getActivity() == null || this.isHidden())
            throw new IllegalStateException();
        else
            ((onFailMediatorListener)getParentFragment()).onMediatorFailed();
        }catch(IllegalStateException e){
            Log.d("AdMediatorFragment","this child fragment is already hidden");
        }
    }

    protected void loadBackupFragment(){
        try{
            ((onFailMediatorListener)getParentFragment()).loadDefaultFragment();
        }catch(IllegalStateException e){
            Log.d("AdMediatorFragment","this child fragment is already hidden");
        }
    }

    public void reportAdUnitDisplayed() {
        Log.d(LOG_TAG,"ad unit displayed!");
        isAdUnitDisplayed = true;
        resetRequestCounter();
        new AdImpressionAysncTask(getCurrentAd(),getActivity()).execute();
    }

    public interface onFailMediatorListener {
        public void onMediatorFailed();
        public void loadDefaultFragment();
        public void loadNextFragment();
    }

    private void resetRequestCounter(){
        AD_UNIT_REQUEST_FAILED_COUNTER = 0;
    }



}
