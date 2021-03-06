package com.ciao.app.netwrok.backgroundtasks;

import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.util.Log;

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;

/**
 * Created by rajat on 16/2/15.
 * This AsyntaskLoader is used to process the Forgot password in background
 */
public class ForgotPasswordAsyntaskLoader extends AsyncTaskLoader<String> implements BaseLoader{

    private RequestBean mRequestBean;
    private NetworkCall mNetworkCall;
    private JSONObject jsonObject;
    private String mRespronse;

    public ForgotPasswordAsyntaskLoader(RequestBean mRequestBean) {
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
        mRespronse=NetworkCall.getInstance(mRequestBean.getActivity()).hitNetwork(AppNetworkConstants.FORGOT_PASSWORD_URL,jsonObject);
        //Log.e("response", mRespronse);
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
