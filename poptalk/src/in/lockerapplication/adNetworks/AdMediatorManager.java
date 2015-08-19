package in.lockerapplication.adNetworks;

import android.util.Log;
import android.view.View;

import java.util.Iterator;
import java.util.TreeSet;

import in.lockerapplication.fragment.AdmarvelFragment;
import in.lockerapplication.fragment.FacebookFragment;
import in.lockerapplication.fragment.MoPubFragment;

/**
 * Created by Simon on 6/24/2015.
 */
public class AdMediatorManager {
    private TreeSet<AdMediatorFragment> sortableMediatorSet;
    private Iterator <AdMediatorFragment> mediatorIterator;
    public AdMediatorManager(){
        initializeMediatorSet();
    }

    public AdMediatorFragment getNextMediator(){
        if(!mediatorIterator.hasNext()){
            Log.e("AdMediatorManager", " no more mediators left ");
            initializeIterator();
        }
        return mediatorIterator.next();
    }

    private void initializeMediatorSet(){
        this.sortableMediatorSet = new TreeSet<AdMediatorFragment>(new MediatorComparator());
        Log.e("AdMediatorManager", " attempt to initialize manager");
        this.sortableMediatorSet.add(AdmarvelFragment.newInstance());
        this.sortableMediatorSet.add(MoPubFragment.newInstance());
        this.sortableMediatorSet.add(FacebookFragment.newInstance());
        initializeIterator();
    }

    private void initializeIterator(){
        Log.e("AdMediatorManager", " attempt to initialize iterator again");
        mediatorIterator = this.sortableMediatorSet.descendingIterator();//replace iterator() with descendingIterator() for proper ordering
    }

}
