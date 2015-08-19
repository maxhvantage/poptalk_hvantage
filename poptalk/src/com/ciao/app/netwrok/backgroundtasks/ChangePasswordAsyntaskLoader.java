package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.util.Log;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;

/**
 * Created by rajat on 5/2/15.
 * This Asyntask is used to process the password change request in background
 * 
 */
public class ChangePasswordAsyntaskLoader extends AsyncTaskLoader<String> implements BaseLoader {
    private RequestBean mRequestBean;
    private NetworkCall mNetworkCall;
    private JSONObject jsonObject;
    private String mRespronse;


    public ChangePasswordAsyntaskLoader(RequestBean mRequestBean) {
        super(mRequestBean.getContext());
        this.mRequestBean = mRequestBean;
        this.mNetworkCall = NetworkCall.getInstance(mRequestBean.getContext());
        this.jsonObject = mRequestBean.getJsonObject();
    }

    @Override
    protected void onStartLoading() {
        if(mRespronse!=null){
            deliverResult(mRespronse);
        }
        if(mRespronse == null || takeContentChanged()){
            showLoaderDialog();
            forceLoad();
        }
    }
    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
        hideLoaderDialog();
    }
    @Override
    public String loadInBackground() {
        if(AppSharedPreference.getInstance(mRequestBean.getContext()).getForgotPasswordRequest()){
            mRespronse=NetworkCall.getInstance(mRequestBean.getActivity()).hitNetwork(AppNetworkConstants.UPDATE_CHANGE_PASSWORD,jsonObject);
        }else {
            mRespronse=NetworkCall.getInstance(mRequestBean.getActivity()).hitNetwork(AppNetworkConstants.CHANGE_PASSWORD,jsonObject);
        }
        Log.e("response", mRespronse);
        return mRespronse;

    }

    @Override
    public void showLoaderDialog() {
        if (mRequestBean.isLoader()) {
            mNetworkCall.showProgressDialog(mRequestBean.getActivity(), mRequestBean.getLoaderMessage(), false);
        }
    }

    @Override
    public void hideLoaderDialog() {
        if (mRequestBean.isLoader()) {
            mNetworkCall.dismissDialog();
        }
    }
}
