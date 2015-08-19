package in.lockerapplication.adNetworks;

import java.util.Comparator;

/**
 * Created by Simon on 6/29/2015.
 */
public class MediatorComparator implements Comparator<AdMediatorFragment> {

    @Override
    public int compare(AdMediatorFragment mediator_A, AdMediatorFragment mediator_B) {
       return mediator_A.getRank().compareTo(mediator_B.getRank());
    }
}
