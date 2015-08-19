package com.ciao.app.netwrok;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by rajat on 3/2/15.
 * This class is use to wrap all the request param
 */
public class RequestBean {

    private Activity activity;
    private Fragment fragment;
    private Context context;
    private boolean isLoader;
    private String loadingMessage = "Please Wait...";
    private JSONObject jsonObject;
    private String queryString;
    private List<NameValuePair> nameValuePairList;

    public void setActivity(Activity activity) {
        this.activity = activity;
        if (activity != null) {
            this.context = activity.getApplicationContext();
        }
    }

    public Activity getActivity() {
        return this.activity;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return this.fragment;
    }

    public void setLoader(boolean isLoader) {
        this.isLoader = isLoader;
    }

    public boolean isLoader() {
        return this.isLoader;
    }

    public void setLoaderMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public String getLoaderMessage() {
        return this.loadingMessage;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setNameValuePairList(List<NameValuePair> nameValuePairList) {
        this.nameValuePairList = nameValuePairList;
    }

    public List<NameValuePair> getNameValuePairList() {
        return nameValuePairList;
    }
}
