package com.ciao.app.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.View;
import android.widget.ProgressBar;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.imageloader.ImageLoader;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;

/**
 * Created by rajat on 18/2/15.
 * This class is used to upload the profile picture from "Setting" screen of the app
 */
public class ProfilePicUploaderService extends Service {

    private static ProfilePicUploaderService self = null;
    public static ProfilePicUploaderService getProfilePicUploaderServiceObject(){
        return self;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    class PicUploader{
        private ProgressBar progressBar;
        private String profilePicUrl;
        private CircularImageView profileCIV;

        PicUploader(CircularImageView profileCIV,ProgressBar progressBar,String profilePicUrl) {
          this.progressBar = progressBar;
          this.profilePicUrl = profilePicUrl;
          this.profileCIV = profileCIV;
        }

        private void execute(){
            profilePicUploader.execute();
        }

        private AsyncTask<Void, Void, Void> profilePicUploader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    AppSharedPreference.getInstance(ProfilePicUploaderService.this).setProfilePicUploaded(false);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected Void doInBackground(Void... params) {
                String respose = NetworkCall.getInstance(ProfilePicUploaderService.this).hitProfilePicUploaderService(profilePicUrl, ProfilePicUploaderService.this);
                try {
                    JSONObject jsonObject = new JSONObject(respose);
                    String errorCode = jsonObject.getString("error_code");
                    if (errorCode.equalsIgnoreCase("0")) {
                        AppSharedPreference.getInstance(ProfilePicUploaderService.this).setProfilePic(profilePicUrl);
                        AppSharedPreference.getInstance(ProfilePicUploaderService.this).setProfilePicUploading(false);
                        AppSharedPreference.getInstance(ProfilePicUploaderService.this).setProfilePicUploaded(true);
                    } else {
                        profilePicUploader.cancel(true);
                        profilePicUploader.execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    String profilePic = AppSharedPreference.getInstance(ProfilePicUploaderService.this).getProfilePic();
                    if (profilePic != null && profileCIV!=null) {
                        //http://ciao.appventurez.com/www/upload/images/a8e529314d7c089db23d3b9440e66082.png
                        if (profilePic.equalsIgnoreCase("")) {

                        } else if (profilePic.contains("http://") || profilePic.contains("https://")) {
                            new ImageLoader(ProfilePicUploaderService.this).displayImage(profilePic, profileCIV, false);
                        } else {
                            Bitmap bm = BitmapFactory.decodeFile(profilePic);
                            profileCIV.setImageBitmap(bm);
                        }

                    }
                    progressBar.setVisibility(View.GONE);
                    stopSelf();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };

    }


    public void uploadImage(CircularImageView profileCIV,ProgressBar progressBar,String profilePicUrl){
        new PicUploader(profileCIV,progressBar,profilePicUrl).execute();
    }

}
