package in.lockerapplication.adNetworks;

import java.util.Comparator;

/**
 * Created by Simon on 6/29/2015.
 */
public class AdUnitComparator implements Comparator<AdUnit>{
    @Override
    public int compare(AdUnit a, AdUnit b) {
        return a.getAd_type().compareTo(b.getAd_type());
    }
}
