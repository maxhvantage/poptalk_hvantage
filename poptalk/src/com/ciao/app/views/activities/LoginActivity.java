package com.ciao.app.views.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.LoginAsyncTaskLoader;
import com.ciao.app.service.ContactSyncService;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.csipsimple.utils.Log;
import com.poptalk.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.lockerapplication.service.LockScreenService;

/**
 * Created by rajat on 23/1/15.
 * This screen show login screen UI.
 * User can enter user name and password to login in app
 */
public class LoginActivity extends Activity {

    private EditText userIdET,passwordET;
    private LoaderManager.LoaderCallbacks<String> loginLoaderCallback;
    private RequestBean mRequestBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mRequestBean = new RequestBean();
        mRequestBean.setActivity(this);
        mRequestBean.setLoader(true);
        intViews();
        intLoader();
    }

    private void intLoader() {
        loginLoaderCallback = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new LoginAsyncTaskLoader(mRequestBean);
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                if (loader instanceof LoginAsyncTaskLoader) {
                   ((LoginAsyncTaskLoader) loader).hideLoaderDialog();
                    try {
                        JSONObject serverResponseJsonObject = new JSONObject(data);
                        String errorCode = serverResponseJsonObject.getString("error_code");
                        Log.d("Login","Login on LoginAsyncTaskLoader, error code is "+errorCode);
                        if (errorCode.equalsIgnoreCase("0")) {
                        	// Login successfull
                            //AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("response_string"));
                            //{"result":{"state_id":"91","first_name":"rajat","phone":"9999319927","referral_code":"hxea23m","email":"rajat@test.com","profile_image":"","ciao_number":["12567279131"],"country_id":"91","date_of_birth":"2015-04-22","gender":"male","user_id":"124"},"error_code":"0","response_string":"Login successful","response_error_key":"0"}
                            JSONObject userDetailJsonObject = serverResponseJsonObject.getJSONObject("result");
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserCountryCode(userDetailJsonObject.getString("country_id"));
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserPhoneNumber(userDetailJsonObject.getString("phone"));
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserGender(userDetailJsonObject.getString("gender"));
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserDOB(userDetailJsonObject.getString("date_of_birth"));
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setAlreadyLoginFlag(true);
                            AppSharedPreference.getInstance(mRequestBean.getContext()).setAlreadyVerified(true);
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserID(userDetailJsonObject.getString("user_id"));
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setProfilePic(userDetailJsonObject.getString("profile_image"));
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setTotalCredit(userDetailJsonObject.getString("credit"));
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setCommUser(userDetailJsonObject.getString("comm_user"));
                            AppSharedPreference.getInstance(mRequestBean.getActivity()).setCommSecurity(userDetailJsonObject.getString("comm_security"));
                            if(userDetailJsonObject.getString("is_lockscreen_active").equalsIgnoreCase("1")){
                            	AppSharedPreference.getInstance(mRequestBean.getActivity()).setAdLockScreenVisibility(true);
                            }else{
                            	AppSharedPreference.getInstance(mRequestBean.getActivity()).setAdLockScreenVisibility(false);
                            }

                            Log.d("Login","Login on LoginAsyncTaskLoader");
                            JSONArray ciaonumberArray = userDetailJsonObject.getJSONArray("ciao_number");
                            for(int i=0;i<ciaonumberArray.length();i++){
                            	ApplicationDAO.getInstance(mRequestBean.getActivity()).saveMyCioaNumberToDb(ciaonumberArray.getString(i));
                            }
                            if (ciaonumberArray.length() > 0) {
                                AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserCiaoNumber(ciaonumberArray.getString(0));
                            }
                            startService(new Intent(LoginActivity.this, LockScreenService.class));
                            startService(new Intent(LoginActivity.this, ContactSyncService.class));
                            startService(new Intent(LoginActivity.this, XMPPChatService.class));
                            //AppSharedPreference.getInstance(mRequestBean.getActivity()).setUserCiaoNumber(ciaonumberArray.getString(0));
                            Intent intent =new Intent(LoginActivity.this, CallActivity.class);
                            startActivity(intent);
                            Intent backIntent = new Intent();
                    		setResult(RESULT_OK, backIntent);
                            finish();

                        } else {
                        	//Login failed
                            AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("response_string"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        };
    }

    private void intViews() {
        userIdET = (EditText)findViewById(R.id.et_user_id);
        passwordET = (EditText)findViewById(R.id.et_user_password);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
        userIdET.setTypeface(type);
        passwordET.setTypeface(type);
        passwordET.setOnEditorActionListener(new OnEditorActionListener() {        
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE){
                	// on click of done button of keyboard 
                	doLogin();
                }
            return false;
            }
        });
        //userIdET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        
    }

    public void login(View view){
    	doLogin();
    }

    public void gotToForgotPasswordScreen(View view){
        startActivity(new Intent(this,ForgotPasswordActivity.class));
    }

    // Get user name and password from respective field and hit api to validate user credentials
    private void doLogin() {
        if (!AppSharedPreference.getInstance(LoginActivity.this).getIsConnectedToInternet()){
            DialogUtils.showInternetAlertDialog(LoginActivity.this);
            return;
        }
        Log.d("Login","Login executed");
        if(userIdET.getText().toString().trim().length() == 0){
            AppUtils.showTost(this,"Please enter your user phone number or email");
        }else if(passwordET.getText().toString().trim().length() == 0){
            AppUtils.showTost(this,"Please enter your password");
        }else {
            JSONObject jsonObject = new JSONObject();
            try {
                Log.d("Login","Login on try with device id "+AppSharedPreference.getInstance(this).getDeivceToken());
                jsonObject.put("user_security",getString(R.string.user_security_key));
                jsonObject.put("user_device_token",AppSharedPreference.getInstance(this).getDeivceToken());
                jsonObject.put("emailPhone",userIdET.getText().toString().trim());
                jsonObject.put("password",passwordET.getText().toString().trim());
                mRequestBean.setJsonObject(jsonObject);
                getLoaderManager().restartLoader(0, null, loginLoaderCallback);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
