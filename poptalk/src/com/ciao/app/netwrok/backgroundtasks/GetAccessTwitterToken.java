package com.ciao.app.netwrok.backgroundtasks;


import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.views.activities.ShareToEarnCreditActivity;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
/*
 * This AsyntaskLoader is used to twitter access token in background
 */
public class GetAccessTwitterToken extends AsyncTask<String, String, Boolean>{

  AccessToken accessToken;
  RequestToken requestToken=null;
  Twitter twitter;
  String  profileUrl,oauth_verifier;
  ProgressDialog progress;
  Context context;
  
  public GetAccessTwitterToken(Context context,String oauth_verifier,Twitter twitter,RequestToken requestToken)
  {
	  this.oauth_verifier = oauth_verifier;
	  this.context = context;
	  this.twitter = twitter;
	   this.requestToken = requestToken;
  }
  
  @Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		progress=new ProgressDialog(context);
        progress.setMessage("Fetching Data...");
        progress.setIndeterminate(true);
        progress.show();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		
		try{
            accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
           
            AppSharedPreference.getInstance(context).setTwitterAccessToken(accessToken.getToken());
            AppSharedPreference.getInstance(context).setTwitterAccessTokenSecret(accessToken.getTokenSecret());
            User user = twitter.showUser(accessToken.getUserId());
           // profileUrl = user.getOriginalProfileImageURL();
          //  edit.putString("NAME", user.getName());
           // edit.putString("IMAGE_URL", user.getOriginalProfileImageURL());

           // edit.commit();
            
            progress.dismiss();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
                    return true;
		
	}
	
	@Override
		protected void onPostExecute(Boolean response) {
		
		 if(accessToken != null){
             progress.hide();
             if(context instanceof ShareToEarnCreditActivity){
            	 ((ShareToEarnCreditActivity)context).showTweetDialog();
             }
             
         }
		}

}
