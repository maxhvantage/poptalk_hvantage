package com.ciao.app.netwrok.backgroundtasks;

import java.util.List;

import android.content.AsyncTaskLoader;

import com.ciao.app.datamodel.Contact;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.utils.ContactHelper;

/**
 * Created by rajat on 6/2/15.
 */
public class ContactAsyntaskLoader extends AsyncTaskLoader<List<Contact>> implements BaseLoader {
    private RequestBean mRequestBean;
    private List<Contact> contactList;
    private NetworkCall mNetworkCall;


    public ContactAsyntaskLoader(RequestBean mRequestBean) {
        super(mRequestBean.getContext());
        this.mRequestBean = mRequestBean;
        this.mNetworkCall = NetworkCall.getInstance(mRequestBean.getContext());

    }

    @Override
    protected void onStartLoading() {
        if(contactList!=null){
            deliverResult(contactList);
        }
        if(contactList == null || takeContentChanged()){
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
    public List<Contact> loadInBackground() {
        contactList = new ContactHelper().fetchContacts(mRequestBean.getContext());
        return contactList;

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
