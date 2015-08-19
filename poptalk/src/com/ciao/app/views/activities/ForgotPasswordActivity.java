package com.ciao.app.views.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.ForgotPasswordAsyntaskLoader;
import com.ciao.app.utils.AppUtils;

/**
 * Created by rajat on 16/2/15.
 */
public class ForgotPasswordActivity extends Activity implements LoaderManager.LoaderCallbacks<String> {
    private RequestBean mRequestBean;
    private EditText userEmailEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        userEmailEt = ((EditText)findViewById(R.id.et_user_email));
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf"); 
        userEmailEt.setTypeface(type);

        mRequestBean = new RequestBean();
        mRequestBean.setActivity(this);
        mRequestBean.setLoader(true);
    }
    public void goToPreviousScreen(View view){
        finish();
    }

    public void requestNewPassword(View view){

        String email = userEmailEt.getText().toString();
        if(AppUtils.isValidEmail(email)){
            try {
                JSONObject jsonObject =  new JSONObject();
                jsonObject.put("user_security", getString(R.string.user_security_key));
                jsonObject.put("email",email);
                mRequestBean.setJsonObject(jsonObject);
                getLoaderManager().restartLoader(0,null,this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            AppUtils.showTost(this,"Please enter a valid email");
        }

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        if (!AppSharedPreference.getInstance(ForgotPasswordActivity.this).getIsConnectedToInternet()){
            DialogUtils.showInternetAlertDialog(ForgotPasswordActivity.this);
            return null;
        }
        else
        {
            return new ForgotPasswordAsyntaskLoader(mRequestBean);
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if(loader instanceof ForgotPasswordAsyntaskLoader){
            ((ForgotPasswordAsyntaskLoader)loader).hideLoaderDialog();
            try {
                JSONObject serverResponseJsonObject  = new JSONObject(data);
                String errorCode = serverResponseJsonObject.getString("error_code");
                if (errorCode.equalsIgnoreCase("0")) {
                    // AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("response_string"));

                    AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("response_string"));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
