package com.ciao.app.netwrok.backgroundtasks;

import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.interfaces.MedaiUploadListener;
import com.ciao.app.netwrok.NetworkCall;

/**
 * Created by rajat on 5/3/15.
 *
 * This asyncTask is used to upload the image in background.
 */

public class MediaUploadHandler extends AsyncTask<MultipartEntity, Integer, JSONObject> {


    private Context activity;
    private MedaiUploadListener listener;
    private String serverurl;
    private ProgressDialog pd = null;
    private String dialogText = "please wait...";
    private boolean isShowLoader;


    public MediaUploadHandler(Activity activity,MedaiUploadListener listener,boolean isShowLoader) {

        this.activity = activity;
        this.listener = listener;

    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        try {

            if(isShowLoader){
                pd = new ProgressDialog(activity);
                pd.setTitle("Processing...");
                pd.setMessage(dialogText);
                pd.setCancelable(true);
                pd.setIndeterminate(true);
                pd.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Test","exception occures" + e.getMessage().toString());
        }

    }

    @Override
    protected JSONObject doInBackground(MultipartEntity... reqEntity) {
        JSONObject jsonResponseData = NetworkCall.getInstance(activity).uploadChatMediaFile(AppNetworkConstants.UPLOAD_CHAT_MEDIA_FILES, reqEntity[0]);
        return jsonResponseData;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);

        if(listener != null)
            listener.getServerResponse(result);
        if(pd != null && pd.isShowing())
            pd.dismiss();
    }



}
