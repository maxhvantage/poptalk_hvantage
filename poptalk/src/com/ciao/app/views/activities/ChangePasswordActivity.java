package com.ciao.app.views.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.ChangePasswordAsyntaskLoader;
import com.ciao.app.utils.AppUtils;

/**
 * Created by rajat on 5/2/15.
 * This Activity show the ui where user can change password
 */
public class ChangePasswordActivity extends Activity {
	private EditText oldPasswordET, newPasswordET, confirmNewPasswordET;
	private LoaderManager.LoaderCallbacks<String> changeLoaderCallback;
	private RequestBean mRequestBean;
	private Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
		mRequestBean = new RequestBean();
		mRequestBean.setActivity(this);
		mRequestBean.setLoader(true);
		setContentView(R.layout.activity_change_password);
		oldPasswordET = (EditText) findViewById(R.id.et_old_password);
		newPasswordET = (EditText) findViewById(R.id.et_new_password);
		submitButton = (Button)findViewById(R.id.btn_submit);
		oldPasswordET.setTypeface(type);
		newPasswordET.setTypeface(type);
		submitButton.setTypeface(type);
		confirmNewPasswordET = (EditText) findViewById(R.id.et_confirm_new_password);
		if (AppSharedPreference.getInstance(this).getForgotPasswordRequest()) {
			oldPasswordET.setVisibility(View.GONE);
		} else {
			oldPasswordET.setVisibility(View.VISIBLE);
		}
		intLoader();
	}

	private void intLoader() {
		changeLoaderCallback = new LoaderManager.LoaderCallbacks<String>() {
			@Override
			public Loader<String> onCreateLoader(int id, Bundle args) {
				return new ChangePasswordAsyntaskLoader(mRequestBean);
			}

			@Override
			public void onLoadFinished(Loader<String> loader, String data) {
				if (loader instanceof ChangePasswordAsyntaskLoader) {
					((ChangePasswordAsyntaskLoader) loader).hideLoaderDialog();
					try {
						JSONObject serverResponseJsonObject = new JSONObject(data);
						String errorCode = serverResponseJsonObject.getString("error_code");
						if (errorCode.equalsIgnoreCase("0")) {
							//Password reset successfully
							if (AppSharedPreference.getInstance(mRequestBean.getContext()).getForgotPasswordRequest()) {
								AppSharedPreference.getInstance(mRequestBean.getContext()).setForgotPasswordRequest(false);
								AppSharedPreference.getInstance(mRequestBean.getContext()).setAlreadyLoginFlag(true);
								gotoCallScreen();
							}
						}
						AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("response_string"));
					} catch (JSONException e) {
						e.printStackTrace();
					}

					finish();
					Log.e("Response = ", data);
				}

			}

			@Override
			public void onLoaderReset(Loader<String> loader) {

			}
		};
	}

	public void goToPreviousScreen(View view) {
		finish();
	}

	public void changePassword(View view) {
		if (AppSharedPreference.getInstance(this).getForgotPasswordRequest()) {
			if (confirmNewPasswordET.getText().toString().trim().length() == 0) {
				AppUtils.showTost(this, "Please confirm your new password");
			}else if (newPasswordET.getText().toString().trim().length()<6) {
				AppUtils.showTost(this, "Please choose a password that has a min 6 characters.");
			}
			else if (!(confirmNewPasswordET.getText().toString().trim()).equals(newPasswordET.getText().toString().trim())) {
				AppUtils.showTost(this, "Password mismatch");
			}
			else if (!AppSharedPreference.getInstance(ChangePasswordActivity.this).getIsConnectedToInternet()){
				DialogUtils.showInternetAlertDialog(ChangePasswordActivity.this);
				return;
			}
			else {
				try {

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("user_security", getString(R.string.user_security_key));
					jsonObject.put("user_device_token", AppSharedPreference.getInstance(this).getDeivceToken());
					jsonObject.put("user_id", AppSharedPreference.getInstance(mRequestBean.getActivity()).getUserID());
					jsonObject.put("user_password", newPasswordET.getText().toString().trim());
					mRequestBean.setJsonObject(jsonObject);
					getLoaderManager().restartLoader(0, null, changeLoaderCallback);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} else {
			if (oldPasswordET.getText().toString().trim().length() == 0) {
				AppUtils.showTost(this, "Please enter your old password");
			} else if (newPasswordET.getText().toString().trim().length() == 0) {
				AppUtils.showTost(this, "Please enter your new password");
			} else if (confirmNewPasswordET.getText().toString().trim().length() == 0) {
				AppUtils.showTost(this, "Please confirm your new password");
			}else if (newPasswordET.getText().toString().trim().length()<6) {
				AppUtils.showTost(this, "Please choose a password that has a min 6 characters.");
			} 
			else if (!(confirmNewPasswordET.getText().toString().trim()).equals(newPasswordET.getText().toString().trim())) {
				AppUtils.showTost(this, "Password mismatch");
			}
			else if (!AppSharedPreference.getInstance(ChangePasswordActivity.this).getIsConnectedToInternet()){
				DialogUtils.showInternetAlertDialog(ChangePasswordActivity.this);
				return;
			}
			else {
				try {

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("user_security", getString(R.string.user_security_key));
					jsonObject.put("user_device_token", getString(R.string.user_device_token));
					jsonObject.put("user_id", AppSharedPreference.getInstance(mRequestBean.getActivity()).getUserID());
					jsonObject.put("user_password", newPasswordET.getText().toString().trim());
					jsonObject.put("old_password", oldPasswordET.getText().toString().trim());
					mRequestBean.setJsonObject(jsonObject);
					getLoaderManager().restartLoader(0, null, changeLoaderCallback);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}
	// Navigate to Call screen of the app
	private void gotoCallScreen() {
		Intent intent = new Intent(this, CallActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);

	}
}
