package com.ciao.app.netwrok.backgroundtasks;

/**
 * Created by rajat on 3/2/15.
 */

import android.content.AsyncTaskLoader;
import android.util.Log;

import com.ciao.app.datamodel.SignUpBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;
/*
 * This asyncTaskloader  is used to complete the sign up process in background.
 */
public class SignupAsyncTaskLoader extends AsyncTaskLoader<String> implements BaseLoader {
    private RequestBean mRequestBean;
    private NetworkCall mNetworkCall;
    //private JSONObject jsonObject;
    private SignUpBean signUpBean;
    private String mRespronse;


    public SignupAsyncTaskLoader(RequestBean mRequestBean,SignUpBean signUpBean) {
        super(mRequestBean.getContext());
        this.mRequestBean = mRequestBean;
        this.mNetworkCall = NetworkCall.getInstance(mRequestBean.getContext());
        this.signUpBean = signUpBean;
        //this.jsonObject = mRequestBean.getJsonObject();
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
        mRespronse=NetworkCall.getInstance(mRequestBean.getActivity()).hitSignUpService(signUpBean,mRequestBean);
        
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
