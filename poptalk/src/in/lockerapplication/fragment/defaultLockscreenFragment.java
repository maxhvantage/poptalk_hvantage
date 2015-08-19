package in.lockerapplication.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.ciao.app.utils.AnimationUtils;
import com.poptalk.app.R;

/**
 * Created by Simon on 7/8/2015.
 */
public class defaultLockscreenFragment extends android.support.v4.app.Fragment {
    private View mView;
    private Animation fadeIn;
    private Animation fadeOut;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_ad_default,container,false);
        //mView.setVisibility(View.GONE);
        //fadeIn = android.view.animation.AnimationUtils.loadAnimation(getActivity(),R.anim.fade_in);
        //fadeIn.setDuration(2000);
        //mView.startAnimation(fadeIn);
        return mView;
    }
}
