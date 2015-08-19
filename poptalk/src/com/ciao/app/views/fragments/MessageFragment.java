package com.ciao.app.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ciao.app.constants.AppConstants;
import com.ciao.app.views.activities.CallActivity;
import com.poptalk.app.R;

/**
 * Created by rajat on 25/1/15.
 * This screen will appear on tap of message tab in bottom of screen
 */
public class MessageFragment  extends Fragment implements View.OnClickListener {
    private View mView;
    private LinearLayout messageTabLL,smsTabLL;
    private ImageView messageIV,smsIV;
    private View messageV,smsV;
    private FragmentActivity mActivity;
    private int mCurrentFragment = AppConstants.MESSAGE_VIEW_FRAGMENT_ID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message,container,false);
        mActivity = getActivity();
        intView(mView);
        intListener();
        return mView;
    }

	private void intListener() {
		messageTabLL.setOnClickListener(this);
		smsTabLL.setOnClickListener(this);
	}

    private void intView(View mView) {
        messageTabLL = (LinearLayout)mView.findViewById(R.id.ll_message);
        smsTabLL= (LinearLayout)mView.findViewById(R.id.ll_sms);
        messageIV= (ImageView)mView.findViewById(R.id.iv_message);
        smsIV= (ImageView)mView.findViewById(R.id.iv_sms);
        messageV= (View)mView.findViewById(R.id.v_message);
        smsV= (View)mView.findViewById(R.id.v_sms);
       
    }

    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	switchFragment(mCurrentFragment);
    }
    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.ll_message:
              switchFragment(AppConstants.MESSAGE_VIEW_FRAGMENT_ID);
              break;
          case R.id.ll_sms:
              switchFragment(AppConstants.SMS_FRAGMENT_ID);
              break;
      }
    }

    private void switchFragment(int fragmentId){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragmentId){
            case AppConstants.MESSAGE_VIEW_FRAGMENT_ID:
            	try {
            		messageTabLL.setBackgroundColor(getResources().getColor(R.color.color_call_top_bar_gb));
                    //smsTabLL.setBackgroundColor(getResources().getColor(android.R.color.white));
                    fragmentTransaction.replace(R.id.ll_message_fragment_container,new MessageViewFragment());
                    messageIV.setImageResource(R.drawable.ciao_selected);
                    smsIV.setImageResource(R.drawable.sms_normal);
                    messageV.setVisibility(View.VISIBLE);
                    smsV.setVisibility(View.INVISIBLE);
                    ((CallActivity)mActivity).toggleCreateGroupIcon(true);
                    mCurrentFragment=AppConstants.MESSAGE_VIEW_FRAGMENT_ID;
				} catch (Exception e) {
					// TODO: handle exception
				}

                break;
            case AppConstants.SMS_FRAGMENT_ID:
//                messageTabLL.setBackgroundColor(getResources().getColor(android.R.color.white));
                smsTabLL.setBackgroundColor(getResources().getColor(R.color.color_call_top_bar_gb));
                messageIV.setImageResource(R.drawable.ciao_normal);
                smsIV.setImageResource(R.drawable.sms_selected);
                messageV.setVisibility(View.INVISIBLE);
                smsV.setVisibility(View.VISIBLE);
                fragmentTransaction.replace(R.id.ll_message_fragment_container,new SmsViewFragment());
                ((CallActivity)mActivity).toggleCreateGroupIcon(false);
                mCurrentFragment = AppConstants.SMS_FRAGMENT_ID;
                break;
        }
        fragmentTransaction.commit();
    }
}