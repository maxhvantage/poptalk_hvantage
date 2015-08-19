package com.ciao.app.views.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.TextView.OnEditorActionListener;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.datamodel.SignUpBean;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.SignupAsyncTaskLoader;
import com.ciao.app.service.GCMIntentService;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.csipsimple.service.SipService;
import com.poptalk.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import in.lockerapplication.service.LockScreenService;

/**
 * Created by rajat on 31/1/15.
 * This screen will show the option where user can select the country and enter the mobile in order to receive the verification code.
 */
public class RegistrationNextActivity extends Activity {
	private SignUpBean signUpBean;
	//private Spinner countrySP, stateSP;
	private EditText phoneET;
	private TextView cancelTV;
	private LoaderManager.LoaderCallbacks<String> signupLoaderCallback;
	private RequestBean mRequestBean;
	private TextView countryNameTV, countryCodeTV, tncTV;
	private LinearLayout countryLayout;
	private String countryCode = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration_next_screen);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (getIntent() != null && getIntent().getExtras() != null) {
			signUpBean = (SignUpBean) getIntent().getExtras().getSerializable("sign_up_bean");
		} else {
			signUpBean = new SignUpBean();
			signUpBean.setUserID(AppSharedPreference.getInstance(this).getUserID());
		}
        tncTV = (TextView)findViewById(R.id.tv_tnc);
        tncTV.setText(Html.fromHtml("<a href=\"\">I agree to the Terms and Conditions.</a>"));
        tncTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationNextActivity.this, TermsAndConditionActivity.class));
            }
        });
		mRequestBean = new RequestBean();
		mRequestBean.setActivity(this);
		mRequestBean.setLoader(true);
		intViews();
		intLoader();

	}

	private void intLoader() {
		signupLoaderCallback = new LoaderManager.LoaderCallbacks<String>() {
			@Override
			public Loader<String> onCreateLoader(int id, Bundle args) {
				return new SignupAsyncTaskLoader(mRequestBean,signUpBean);
			}

			@Override
			public void onLoadFinished(Loader<String> loader, String data) {
				if (loader instanceof SignupAsyncTaskLoader) {
					((SignupAsyncTaskLoader) loader).hideLoaderDialog();
					try {
						JSONObject serverResponseJsonObject = new JSONObject(data);
						String errorCode = serverResponseJsonObject.getString("error_code");
						if (errorCode.equalsIgnoreCase("0")) {
							AppSharedPreference.getInstance(mRequestBean.getActivity()).setRegisteredOnChatServer(false);
							startService(new Intent(mRequestBean.getActivity(), XMPPChatService.class));
							//AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("response_string"));
							JSONObject userDetailJsonObject = serverResponseJsonObject.getJSONObject("result");
							AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserCountryCode(signUpBean.getCountryCode());
							AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserPhoneNumber(signUpBean.getPhoneNumber());
							AppSharedPreference.getInstance(mRequestBean.getActivity()).setAlreadyLoginFlag(true);
							AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserID(userDetailJsonObject.getString("user_id"));
							AppSharedPreference.getInstance(mRequestBean.getActivity()).setProfilePic(userDetailJsonObject.getString("profile_image"));
							//AppSharedPreference.getInstance(mRequestBean.getActivity()).setVerificationCode(userDetailJsonObject.getString("verification_code"));
							AppSharedPreference.getInstance(mRequestBean.getActivity()).setCommUser(userDetailJsonObject.getString("comm_user"));
							AppSharedPreference.getInstance(mRequestBean.getActivity()).setCommSecurity(userDetailJsonObject.getString("comm_security"));



							/*  Intent intent = new Intent(mRequestBean.getContext(),VerifyNumberActivity.class);
                            startActivity(intent);
                            Intent backIntent = new Intent();
            				setResult(RESULT_OK, backIntent);
            				finish();*/

							if (!AppSharedPreference.getInstance(RegistrationNextActivity.this).getIsConnectedToInternet()){
								DialogUtils.showInternetAlertDialog(RegistrationNextActivity.this);
								return;
							}
							else
							{
								goToVerificationActivity(signUpBean.getCountryCode()+signUpBean.getPhoneNumber());
							}

                            
						} else {
							AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("response_string"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
						AppUtils.showTost(mRequestBean.getActivity(), e.toString());
					}

				}
			}

			@Override
			public void onLoaderReset(Loader<String> loader) {

			}
		};
	}

	private void intViews() {
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
		/* countrySP = (Spinner) findViewById(R.id.sp_country);
        stateSP = (Spinner) findViewById(R.id.sp_state);*/
		signUpBean.setCountryCode("+1");
		phoneET = (EditText) findViewById(R.id.et_phone_number);
		phoneET.setTypeface(type);
		cancelTV = (TextView) findViewById(R.id.tv_cancel_registration);
		cancelTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (XMPPChatService.getConnection() != null) {
					XMPPChatService.getConnection().disconnect();
				}
				stopService(new Intent(RegistrationNextActivity.this, XMPPChatService.class));
				stopService(new Intent(RegistrationNextActivity.this, SipService.class));
				stopService(new Intent(RegistrationNextActivity.this, LockScreenService.class));
				stopService(new Intent(RegistrationNextActivity.this, GCMIntentService.class));
				AppSharedPreference.getInstance(RegistrationNextActivity.this).setAlreadyLoginFlag(false);
				AppSharedPreference.getInstance(RegistrationNextActivity.this).setCommUser(null);
				AppSharedPreference.getInstance(RegistrationNextActivity.this).setUserID(null);
				Intent intent = new Intent(RegistrationNextActivity.this,SplashActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				finish();
			}
		});
		phoneET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		countryCodeTV = (TextView)findViewById(R.id.tv_country_code);
		countryNameTV = (TextView)findViewById(R.id.tv_country_name);
		countryLayout = (LinearLayout)findViewById(R.id.ll_country);
		countryLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegistrationNextActivity.this,CountrySelectionActivity.class);
				startActivityForResult(intent, 1);

			}
		});
		phoneET.setOnEditorActionListener(new OnEditorActionListener() {        
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId==EditorInfo.IME_ACTION_DONE){
					doRegistration();
				}
				return false;
			}
		});
	}

	public void goToNextScreen(View view) {
		doRegistration();
	}

	private void doRegistration() {

		if (!AppSharedPreference.getInstance(RegistrationNextActivity.this).getIsConnectedToInternet()){
			DialogUtils.showInternetAlertDialog(RegistrationNextActivity.this);
			return;
		}

		signUpBean.setCountryCode(countryCode);
		String phoneNumber = phoneET.getText().toString().trim();
		phoneNumber = AppUtils.parseFormattedNumber(phoneNumber);
		signUpBean.setPhoneNumber(phoneNumber);
		JSONObject formValidationResponse = AppUtils.isFormDataValid(signUpBean, 1);
		try {
			JSONObject responseJsonObject = formValidationResponse.getJSONObject("response");
			boolean isFormValid = responseJsonObject.getBoolean("isFormValid");
			if (isFormValid) {
                if (((CheckBox) findViewById(R.id.cb_tnc)).isChecked()) {
                    getLoaderManager().restartLoader(1, null, signupLoaderCallback);
                } else {
                    AppUtils.showTost(RegistrationNextActivity.this, "You can only proceed if you agree to PopTalk's Terms and Conditions.");
                }

			} else {
				AppUtils.showTost(this, responseJsonObject.getString("error_message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("CountryCode", "Result code is "+resultCode);
		if(resultCode == 1)
		{

			countryCode = data.getStringExtra("_countrycode").replace("+", "");

			Log.d("CountryCode","Country code is "+data.getStringExtra("_countrycode"));
			countryCodeTV.setText(data.getStringExtra("_countrycode"));
			countryNameTV.setText(data.getStringExtra("_countryname"));
		}

	}

	public void goToVerificationActivity(String number){


			Intent intent = new Intent(mRequestBean.getContext(),VerifyNumberActivity.class);
			intent.putExtra("number", number);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			Intent backIntent = new Intent();
			setResult(RESULT_OK, backIntent);
			finish();

	}
}
