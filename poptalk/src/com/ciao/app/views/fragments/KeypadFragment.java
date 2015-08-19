package com.ciao.app.views.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.BaseResponseBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.GetCallLimitAndCharge;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.CallActivity;
import com.ciao.app.views.activities.CountrySelectionActivity;
import com.csipsimple.api.SipProfile;
import com.csipsimple.ui.incall.InCallCard;
import com.csipsimple.ui.prefs.CallLimitPreference;
import com.csipsimple.utils.PlaceCall;

/**
 * Created by rajat on 28/1/15.
 * 
 */
public class KeypadFragment extends PlaceCall implements View.OnClickListener {
	private View mView;
	private EditText numberET;
	private Button key1, key2, key3, key4, key5, key6, key7, key8, key9, key0,keyAstric, keyHash;
	private ImageView clearDigitIV, makeCallIV;
	//private Spinner flagSpinner;
	private RequestBean mRequestBean;
	private LoaderManager.LoaderCallbacks<String> twilioLoaderCallback;
	private String countryCode = "+1";
	private Context mContext;
	private LinearLayout countryLL;
	private TextView countryNameTV,countryCodeTV;
	private int requestCode=1;
	private String callRates;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		mView = inflater.inflate(R.layout.fragment_keypad, container, false);
		mContext = getActivity();
		mRequestBean = new RequestBean();
		mRequestBean.setActivity(getActivity());
		mRequestBean.setLoader(true);
		initLoaders();
		intViews(mView);
		intListener();

		return mView;
	}

	private void initLoaders() {

	}

	private void intViews(View mView) {
		Typeface type = Typeface.createFromAsset(mContext.getAssets(),"fonts/OpenSans-Regular.ttf");
		key1 = (Button) mView.findViewById(R.id.key_1);
		key2 = (Button) mView.findViewById(R.id.key_2);
		key3 = (Button) mView.findViewById(R.id.key_3);
		key4 = (Button) mView.findViewById(R.id.key_4);
		key5 = (Button) mView.findViewById(R.id.key_5);
		key6 = (Button) mView.findViewById(R.id.key_6);
		key7 = (Button) mView.findViewById(R.id.key_7);
		key8 = (Button) mView.findViewById(R.id.key_8);
		key9 = (Button) mView.findViewById(R.id.key_9);
		key0 = (Button) mView.findViewById(R.id.key_0);


		key1.setTypeface(type);
		key2.setTypeface(type);
		key3.setTypeface(type);
		key4.setTypeface(type);
		key5.setTypeface(type);
		key6.setTypeface(type);
		key7.setTypeface(type);
		key8.setTypeface(type);
		key9.setTypeface(type);
		key0.setTypeface(type);

		keyAstric = (Button) mView.findViewById(R.id.key_astric);
		keyHash = (Button) mView.findViewById(R.id.key_hash);
		numberET = (EditText) mView.findViewById(R.id.tv_number);
		clearDigitIV = (ImageView) mView.findViewById(R.id.iv_clear_digit);
		makeCallIV = (ImageView) mView.findViewById(R.id.iv_make_call_button);
		countryCodeTV = (TextView)mView.findViewById(R.id.tv_country_code);
		countryNameTV = (TextView)mView.findViewById(R.id.tv_country_name);

		numberET.setCursorVisible(false);
		numberET.setInputType(InputType.TYPE_NULL);
		numberET.setTypeface(type);
		numberET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()==0){
					numberET.setCursorVisible(false);
					clearDigitIV.setImageResource(R.drawable.contact_list_btm);
				}else{
					numberET.setCursorVisible(true);
					clearDigitIV.setImageResource(R.drawable.delete_icon);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {


			}

			@Override
			public void afterTextChanged(Editable s) {


			}
		});

		countryLL = (LinearLayout)mView.findViewById(R.id.ll_country);
		countryLL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext,CountrySelectionActivity.class);
				startActivityForResult(intent, requestCode);
			}
		});
	}

	private void intListener() {
		key1.setOnClickListener(this);
		key2.setOnClickListener(this);
		key3.setOnClickListener(this);
		key4.setOnClickListener(this);
		key5.setOnClickListener(this);
		key6.setOnClickListener(this);
		key7.setOnClickListener(this);
		key8.setOnClickListener(this);
		key9.setOnClickListener(this);
		key0.setOnClickListener(this);
		keyAstric.setOnClickListener(this);
		keyHash.setOnClickListener(this);
		clearDigitIV.setOnClickListener(this);
		makeCallIV.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.key_0:
			setDigit("0");
			break;
		case R.id.key_1:
			setDigit("1");
			break;
		case R.id.key_2:
			setDigit("2");
			break;
		case R.id.key_3:
			setDigit("3");
			break;
		case R.id.key_4:
			setDigit("4");
			break;
		case R.id.key_5:
			setDigit("5");
			break;
		case R.id.key_6:
			setDigit("6");
			break;
		case R.id.key_7:
			setDigit("7");
			break;
		case R.id.key_8:
			setDigit("8");
			break;
		case R.id.key_9:
			setDigit("9");
			break;
		case R.id.key_astric:
			setDigit("*");
			break;
		case R.id.key_hash:
			setDigit("#");
			break;
		case R.id.iv_clear_digit:
			clearDigit();
			break;
		case R.id.iv_make_call_button:

			if(NetworkStatus.isConected(mContext)){
				String number =  numberET.getText().toString();
				number = AppUtils.parseFormattedNumber(number);
				if(number.length()>=10){
					SipProfile profile = getSelectedAccount();
					if(profile!=null){
						if(profile.active){

							if (number.length() == 10) {
								makeCallIV.getDrawable().setAlpha(100);
								placeCallWithOption(null, false, getCountryCode() + number);
								AppSharedPreference.getInstance(mContext).setIsCaioOutCall(true);

								if (!AppSharedPreference.getInstance(KeypadFragment.this.getActivity()).getIsConnectedToInternet()){
									DialogUtils.showInternetAlertDialog(KeypadFragment.this.getActivity());
									return;
								}

								new GetCallLimitAndCharge(mContext, getCountryCode(), KeypadFragment.this, number).execute();

							} else {
								// In App call call for free
								makeCallIV.getDrawable().setAlpha(100);
								placeCallWithOption(null, false, number);
								AppSharedPreference.getInstance(mContext).setIsCaioOutCall(false);
							}
						}else{
							//AppUtils.showTost(getActivity(), "Your account is offline");
						}
					}else{
						//AppUtils.showTost(mContext, "Your account is not login");
					}
					/*if (number.length() == 10) {
						placeCallWithOption(null, false, "345" + getCountryCode()+ number);
					} else {
						placeCallWithOption(null, false, number);
					} */  
				}else{
					AppUtils.showTost(mContext, "Please enter a valid number"); 
				}

			}else{
				AppUtils.showTost(mContext, "Please check your internet connection");
			}



			break;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	// clear number on click od delete button
	private void clearDigit() {
		if (numberET.getText().length() > 0) {
			String phoneNumber = numberET.getText().toString().trim();
			phoneNumber = phoneNumber.replace("(", "");
			phoneNumber = phoneNumber.replace(")", "");
			phoneNumber = phoneNumber.replace("-", "");
			phoneNumber = phoneNumber.substring(0, phoneNumber.length() - 1);
			if(phoneNumber.length()==0){
				numberET.setText("");	
			}else{
				if(phoneNumber.length()<=3){
					phoneNumber = "("+phoneNumber+")";
				}else if (phoneNumber.length()>3 && phoneNumber.length()<=6) {
					String part1 = "("+phoneNumber.substring(0, 3)+")";
					String part2 = phoneNumber.substring(3,phoneNumber.length());
					phoneNumber = part1+part2;
				}else{
					phoneNumber = AppUtils.formatPhoneNumberUsingParentheses(phoneNumber);	
				}	
			}

			numberET.setText(phoneNumber);
		}else{
			if(mContext instanceof CallActivity){
				((CallActivity)mContext).switchFragment(AppConstants.CONTACTS_FRAGMENT_ID);
			}
		}

	}

	//set number in on tap of key to number place holder
	private void setDigit(String digit){
		String phoneNumber = numberET.getText().toString().trim();
		phoneNumber = phoneNumber.replace("(", "");
		phoneNumber = phoneNumber.replace(")", "");
		phoneNumber = phoneNumber.replace("-", "");
		phoneNumber = phoneNumber.replace(" ", "");
		phoneNumber = phoneNumber+digit;
		if(phoneNumber.length()<=3){
			phoneNumber = "("+phoneNumber+") ";
		}else if (phoneNumber.length()>3 && phoneNumber.length()<=6) {
			String part1 = "("+phoneNumber.substring(0, 3)+") ";
			String part2 = phoneNumber.substring(3,phoneNumber.length());
			phoneNumber = part1+part2;
		}else{
			/*phoneNumber = AppUtils.formatPhoneNumberUsingParentheses(phoneNumber)*/;
			String part1 = "("+phoneNumber.substring(0, 3)+") ";
			String part2 = phoneNumber.substring(3,6);
			String part3 = phoneNumber.substring(6,phoneNumber.length());
			phoneNumber = part1+part2+"-"+part3;
		}
		numberET.setText(phoneNumber);
	}


	public void success(BaseResponseBean responseBean) {
		// TODO Auto-generated method stub
		AppUtils.showTost(mContext, "Success ");
	}

	public void error(String getmExceptionName) {
		// TODO Auto-generated method stub
		AppUtils.showTost(mContext, getmExceptionName);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
	
	//returns selected country code
	private String getCountryCode() {
		String countryCode = countryCodeTV.getText().toString();
		countryCode = countryCode.substring(1, countryCode.length());
		return countryCode;


	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == 1)
		{
			//Country selected from Country selection screen.
			countryCode = data.getStringExtra("_countrycode");
			countryCodeTV.setText(data.getStringExtra("_countrycode"));
			countryNameTV.setText(data.getStringExtra("_countryname"));
		}

	}

	//This method set the maximum call limit on the basis user currently available credit and call rates for selected country.
	public void setCallLimitForDialedNumber(String callRates) {
		this.callRates = callRates;
		setCallLimit(AppUtils.getAvailableCallLimit(mContext, callRates));
	}

	public void setCountryCode(Intent data){
		countryCode = data.getStringExtra("_countrycode");
		countryCodeTV.setText(data.getStringExtra("_countrycode"));
		countryNameTV.setText(data.getStringExtra("_countryname"));
	}
	@Override
	public void onResume() {
		super.onResume();
		
		if(AppSharedPreference.getInstance(mContext).getIsCaioOutCall()){
			//If user made a ciao out call deduct the credits
			if(callRates!=null){
				AppUtils.handleCreditForCall(mContext, callRates);	
			}

		}

		makeCallIV.getDrawable().setAlpha(255);

	}



}
