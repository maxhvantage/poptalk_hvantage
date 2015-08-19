package com.ciao.app.netwrok.backgroundtasks;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;
import com.mopub.common.util.AsyncTasks;

import org.json.JSONObject;

/**
 * Created by victorylin on 7/13/2015.
 *
 * update the credit after purchase credit
 */
public class CreditUpdateTaskLoader extends AsyncTaskLoader<String> implements BaseLoader {
    private RequestBean mRequestBean;
    private NetworkCall mNetworkCall;
    private JSONObject jsonObject;
    private String mRespronse;

    public CreditUpdateTaskLoader(RequestBean requestBean) {
        super(requestBean.getContext());
        this.mRequestBean = requestBean;
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
        mRespronse=NetworkCall.getInstance(mRequestBean.getActivity()).hitNetwork(AppNetworkConstants.UPDEDE_CREDIT_FROM_INAPP,jsonObject);
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
