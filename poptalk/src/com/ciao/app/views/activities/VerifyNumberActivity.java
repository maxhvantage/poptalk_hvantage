package com.ciao.app.views.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.broadcastreceiver.SmsBroadcastReceiver;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.AccountVerificationLoader;
import com.ciao.app.netwrok.backgroundtasks.GenerateVerificationCodeAsynTask;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.ciao.app.views.customviews.CustomTextView;
import com.poptalk.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

/**
 * Created by rajat on 9/2/15.
 * This Activity show the screen where user can enter the verification code received from poptalk and verification process will take place at this screen
 */
public class VerifyNumberActivity extends Activity {
	private LoaderManager.LoaderCallbacks<String> verifcationLoaderCallback;
	private RequestBean mRequestBean;
	private static EditText verificationCodeET;
	private static CustomTextView countdownET, resendET;
	private String verificationCode,number;
	private SmsBroadcastReceiver smsReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_number);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		smsReceiver= new SmsBroadcastReceiver();
		registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
		verificationCodeET = (EditText) findViewById(R.id.et_verification_code);
		countdownET = (CustomTextView) findViewById(R.id.tv_countdown_sms);
		resendET = (CustomTextView) findViewById(R.id.tv_resend_sms);
		resendET.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendSmsVerificationPin();
				AppUtils.showTost(VerifyNumberActivity.this, "Your pin was resent via SMS.");
			}
		});
		resendET.setMovementMethod(LinkMovementMethod.getInstance());
		resendET.setText(Html.fromHtml(getResources().getString(R.string.txt_resend)));
		verificationCodeET.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					try {
						doVerification();
					} catch (Exception e) {
						// do nothing
					}
				}
				return false;
			}
		});
		mRequestBean = new RequestBean();
		mRequestBean.setActivity(this);
		mRequestBean.setLoader(true);
		intLoader();
		number = AppSharedPreference.getInstance(this).getUserCountryCode()+AppSharedPreference.getInstance(this).getUserPhoneNumber();

		sendSmsVerificationPin();

	}

	private void sendSmsVerificationPin() {
		resendET.setVisibility(View.GONE);
		countdownET.setVisibility(View.VISIBLE);

		new CountDownTimer(180000, 1000) {
			SimpleDateFormat df = new SimpleDateFormat("mm:ss");

			public void onTick(long millisUntilFinished) {
				countdownET.setText("Estimated time remaining: " + df.format(millisUntilFinished));
			}

			public void onFinish() {
				resendET.setVisibility(View.VISIBLE);
				countdownET.setVisibility(View.GONE);
			}
		}.start();

		try {
			Log.d("CountryCode", "Full number on VerifyNumberActivity " + number);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("user_security", getString(R.string.user_security_key));
			jsonObject.put("user_id", AppSharedPreference.getInstance(mRequestBean.getActivity()).getUserID());
			jsonObject.put("device_token", AppSharedPreference.getInstance(mRequestBean.getActivity()).getDeivceToken());
			mRequestBean.setJsonObject(jsonObject);
			new GenerateVerificationCodeAsynTask(this, mRequestBean).execute();
		}catch (Exception e) {
			Log.e("sms-verification", e.getMessage());
		}
	}

	private void intLoader() {
	}

	public void goToPreviousScreen(View view) {
		finish();
	}

	public void verify(View view) {
		try {
			doVerification();
		} catch (Exception e) {
			// do nothing
		}

	}

	private void doVerification() throws Exception {
		// if network is not enable. go to the phone setting for the network enable.
		if (!AppSharedPreference.getInstance(VerifyNumberActivity.this).getIsConnectedToInternet()){
			DialogUtils.showInternetAlertDialog(VerifyNumberActivity.this);
			return;
		}

		verificationCode = (verificationCodeET).getText().toString();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("user_security", getString(R.string.user_security_key));
			jsonObject.put("user_id", AppSharedPreference.getInstance(mRequestBean.getActivity()).getUserID());
			jsonObject.put("device_token", AppSharedPreference.getInstance(mRequestBean.getActivity()).getDeivceToken());
			jsonObject.put("pin", verificationCode);
		} catch (Exception e) {
			Log.e("sms-verification", e.getMessage());
		}
		mRequestBean.setJsonObject(jsonObject);
		String status = new GenerateVerificationCodeAsynTask(this, mRequestBean).execute().get();

		if (status.equalsIgnoreCase("0")) {
			AppSharedPreference.getInstance(mRequestBean.getContext()).setAlreadyVerified(true);
			if (AppSharedPreference.getInstance(mRequestBean.getContext()).getForgotPasswordRequest()) {
				gotoChangePasswordScreen();
			} else {
				AppSharedPreference.getInstance(mRequestBean.getContext()).setTotalCredit("200");
				gotoCallScreen();
			}
		} else {
			AppUtils.showTost(mRequestBean.getActivity(), getResources().getString(R.string.invalid_pin));
			AppSharedPreference.getInstance(mRequestBean.getContext()).setAlreadyVerified(false);
		}
	}

	private void gotoCallScreen() {
		AppUtils.getFacebookLogger(this).logEvent("signup");

		Intent intent = new Intent(this, CallActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();
		startActivity(intent);
		Intent backIntent = new Intent();
		setResult(RESULT_OK, backIntent);

	}

	private void gotoChangePasswordScreen() {
		Intent intent = new Intent(this, ChangePasswordActivity.class);
		startActivity(intent);
	}

	public static void setVerificationCode(String code){
		if(verificationCodeET!=null){
			verificationCodeET.setText(code);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1001){
			if(resultCode == RESULT_OK){
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}
	
}
