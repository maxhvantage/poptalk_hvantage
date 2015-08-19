package in.lockerapplication.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.mopub.common.util.DateAndTime;
import com.poptalk.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import in.lockerapplication.adNetworks.AdUnit;
import in.lockerapplication.bean.BaseResponseBean;
import in.lockerapplication.networkcall.NetworkCall;

/**
 * Created by Simon on 7/8/2015.
 */
public class AdImpressionAysncTask extends AsyncTask<AdUnit,Void,String> {
    private AdUnit ad;
    private Context context;
    private boolean isLoggedIn;
    private static final String NOT_LOGGED_IN_MSG= "Not Logged In Impressions Not Tracked";
    public AdImpressionAysncTask(AdUnit ad,Context context){
        this.ad = ad;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isLoggedIn = AppSharedPreference.getInstance(context).getAlreadyLoginFlag();
    }

    @Override
    protected String doInBackground(AdUnit... params) {
        if(!isLoggedIn)
            return NOT_LOGGED_IN_MSG;
        Log.e("AdImpressionAsyncTask", "Attempt to make JSONObject");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_security", context.getString(R.string.user_security_key));
            jsonObject.put("user_id", AppSharedPreference.getInstance(context).getUserID());
            jsonObject.put("id_ad", ad.getAd_id());
            //jsonObject.put("ad_type", ad.getAd_type());
            jsonObject.put("network", ad.getAd_mediator());
            jsonObject.put("created", getFormattedDate());
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        String response = com.ciao.app.netwrok.NetworkCall.getInstance(context).hitNetwork(AppNetworkConstants.REPORT_AD_IMPRESSION, jsonObject);
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("AdImpressionAysncTask", s);
    }


    private String getFormattedDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return formatter.format(DateAndTime.now());
    }
}
