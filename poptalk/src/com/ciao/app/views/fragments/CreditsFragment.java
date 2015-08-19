package com.ciao.app.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.datamodel.PopTalkRatesBean;
import com.ciao.app.netwrok.backgroundtasks.GetAvailableCallMinutes;
import com.ciao.app.utils.AnalyticsUtils;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.ciao.app.views.activities.BuyItemsActivity;
import com.ciao.app.views.activities.CheckinActivity;
import com.ciao.app.views.activities.CountrySelectionActivity;
import com.ciao.app.views.activities.InviteFriendDescriptionActivity;
import com.ciao.app.views.activities.ShareToEarnCreditActivity;
import com.poptalk.app.R;
import com.sponsorpay.SponsorPay;
import com.sponsorpay.publisher.SponsorPayPublisher;

/**
 * Created by rajat on 25/1/15.
 */
public class CreditsFragment extends Fragment implements View.OnClickListener {
	private View mView;
	private TextView earnCreditLBLTV, earnCreditTV, inviteFriendLBLTV, inviteFriendTV, shareLBLTV, shareTV, purchaseLBLTV, countryTV;
	private static final int OFFERWALL_REQUEST_CODE = 1;
	private String appId,userId,securityToken;
	private String countryCode = "1";
	private String countryName = "United States";
	private TextView callMinuteTV,smsTV,landLineMinTv;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_credit_latest_new, container, false);
		intViews(mView);
		//Get available call on mobile ,land line and sms count for current credits
		new GetAvailableCallMinutes(getActivity(), CreditsFragment.this,countryCode).execute();
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();

		// initialize Fyber wall
		try {
			appId = "30130";
			userId = AppSharedPreference.getInstance(getActivity()).getUserID();
			securityToken = "270f3fade77a19bb6e6ec3162954b3d1";
			SponsorPay.start(appId, userId, securityToken, getActivity());
		} catch (RuntimeException e){
			Log.d("Fyber Wall", e.getLocalizedMessage());
		}
	}

	private void intViews(View mView) {
		countryTV = (TextView) mView.findViewById(R.id.tv_country);
		(mView.findViewById(R.id.rl_earn_credits)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_invite_via_message)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_invite_via_social)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_buy_credits)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_country)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_checkin)).setOnClickListener(this);
		earnCreditTV = (TextView) mView.findViewById(R.id.tv_earn_free_credit_desc);
		inviteFriendTV = (TextView) mView.findViewById(R.id.tv_invite_friend_desc);
		shareTV = (TextView) mView.findViewById(R.id.tv_share_desc);
		earnCreditLBLTV = (TextView) mView.findViewById(R.id.tv_eran_free_credits);
		inviteFriendLBLTV = (TextView) mView.findViewById(R.id.tv_invite_friend);
		shareLBLTV = (TextView) mView.findViewById(R.id.tv_share);
		purchaseLBLTV = (TextView) mView.findViewById(R.id.tv_purchase);
		callMinuteTV= (TextView) mView.findViewById(R.id.tv_phone_mins);
		smsTV= (TextView) mView.findViewById(R.id.tv_sms);
		landLineMinTv = (TextView) mView.findViewById(R.id.tv_ciao_phone_mins);
		setText();

	}

	private void setText() {
		earnCreditTV.setText(Html.fromHtml("Up to <b><font color=\"#e2242b\">+100&nbsp;</font></b>per completed offer"));
		shareTV.setText(Html.fromHtml("<b><font color=\"#e2242b\">+10&nbsp;</font></b> every month for sharing"));
		inviteFriendTV.setText(Html.fromHtml("When they have join, <b><font color=\"#e2242b\">+20&nbsp;</font></b>for you,<b><font color=\"#e2242b\">+20&nbsp;</font></b>for them.<b><font color=\"#e2242b\">+20&nbsp;</font></b>every month they have Ciao"));
		earnCreditLBLTV.setText(Html.fromHtml("Earn free credits"));
		inviteFriendLBLTV.setText(Html.fromHtml("Invite a friend for credits"));
		shareLBLTV.setText(Html.fromHtml("Share to social profile"));
		purchaseLBLTV.setText(Html.fromHtml("Purchase credits"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_earn_credits:
			AnalyticsUtils.sendEvent("app_credits", new AnalyticsUtils.Param("navigated", "earn_credits"));
			Intent offerWallIntent = SponsorPayPublisher.getIntentForOfferWallActivity(getActivity().getApplicationContext(), true);
			getActivity().startActivityForResult(offerWallIntent, OFFERWALL_REQUEST_CODE);
			//startActivity(new Intent(getActivity(), FyberWallOfferActivity.class));
			break;
		case R.id.rl_invite_via_message:
			AnalyticsUtils.sendEvent("app_credits", new AnalyticsUtils.Param("navigated", "via_message"));
			startActivity(new Intent(getActivity(), InviteFriendDescriptionActivity.class));
			break;
		case R.id.rl_invite_via_social:
			AnalyticsUtils.sendEvent("app_credits", new AnalyticsUtils.Param("navigated", "via_social"));
			startActivity(new Intent(getActivity(), ShareToEarnCreditActivity.class));
			break;
		case R.id.rl_buy_credits:
			AnalyticsUtils.sendEvent("app_credits", new AnalyticsUtils.Param("navigated", "buy_credits"));
			Intent intent = new Intent(getActivity(), BuyItemsActivity.class);
			intent.putExtra("screen_id", "1");
			startActivity(intent);
			break;
		case R.id.rl_country:
			Intent countryIntent = new Intent(getActivity(),CountrySelectionActivity.class);
			startActivityForResult(countryIntent, 1);
			break;
		case R.id.rl_checkin:
			Intent checkinIntent = new Intent(getActivity(),CheckinActivity.class);
			startActivity(checkinIntent);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 1 && resultCode ==1)
		{
			if(data!=null){
				if(data.getStringExtra("_countryname")!=null){
					countryName = data.getStringExtra("_countryname");
				}
				if(data.getStringExtra("_countrycode")!=null){
					countryCode = data.getStringExtra("_countrycode");
					countryCode = countryCode.replace("+","");
					countryCode = countryCode.replace(" ","");

					if (!AppSharedPreference.getInstance(CreditsFragment.this.getActivity()).getIsConnectedToInternet()){
						DialogUtils.showInternetAlertDialog(CreditsFragment.this.getActivity());
						return;
					}
					else
					{
						new GetAvailableCallMinutes(getActivity(), CreditsFragment.this, countryCode).execute();
					}

				}
			}
		}
		countryTV.setText(countryName);

		/*String callMins = AppUtils.getTotalCiaoOutCall(getActivity(),countryCode)+" mins";
		callMinuteTV.setText(callMins);
		landLineMinTv.setText(callMins);
		smsTV.setText(Integer.toString(AppUtils.getTotalCiaoOutSms(getActivity(),countryCode))+" sms");*/

	}

	public void setAvailableMinutes(PopTalkRatesBean popTalkRatesBean){
		if("0".equalsIgnoreCase(popTalkRatesBean.getMobileRate())){
			String mobileMins = AppUtils.getTotalCiaoOutCall(getActivity(), popTalkRatesBean.getLandlineRate())+" mins";	
			callMinuteTV.setText(mobileMins);
		}else{
			String mobileMins = AppUtils.getTotalCiaoOutCall(getActivity(), popTalkRatesBean.getMobileRate())+" mins";
			callMinuteTV.setText(mobileMins);
		}if("0".equalsIgnoreCase(popTalkRatesBean.getLandlineRate())){
			String landLineMins = AppUtils.getTotalCiaoOutCall(getActivity(),popTalkRatesBean.getMobileRate())+" mins"; 
			landLineMinTv.setText(landLineMins);
		}else{
			String landLineMins = AppUtils.getTotalCiaoOutCall(getActivity(),popTalkRatesBean.getLandlineRate())+" mins"; 
			landLineMinTv.setText(landLineMins);
		}
		String numberOfSmsLeft = AppUtils.getTotalCiaoOutSms(getActivity(), popTalkRatesBean.getSmsRate())+" sms";
		smsTV.setText(numberOfSmsLeft);
	}


}
