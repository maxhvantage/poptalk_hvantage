package com.ciao.app.netwrok.backgroundtasks;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.ShareToEarnCreditActivity;
/*
 * This asyncTask is used to post on twitter.
 */
public class PostOnTwitter extends AsyncTask<String,String,String>{
	
	private ProgressDialog progress;
	private Context context;
	private String tweetText;
	
	public PostOnTwitter(Context context,String tweetText)
	{
		this.context = context;
		this.tweetText = tweetText;
	}
	
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		 progress = new ProgressDialog(context);
         progress.setMessage("Posting tweet ...");
         progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         progress.setIndeterminate(true);
         progress.show();
	}

	@Override
	protected String doInBackground(String... params) {
		 ConfigurationBuilder builder = new ConfigurationBuilder();
		 builder.setOAuthConsumerKey(AppConstants.CONSUMER_KEY);
         builder.setOAuthConsumerSecret(AppConstants.CONSUMER_SECRET);
         

         AccessToken accessToken = new AccessToken(AppSharedPreference.getInstance(context).getTwitterAccessToken(),AppSharedPreference.getInstance(context).getTwitterAccessTokenSecret());
         Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

         try {
             twitter4j.Status response = twitter.updateStatus(tweetText);
             return response.toString();
         } catch (TwitterException e) {
             e.printStackTrace();
         }
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		  if (result != null) {
              progress.dismiss();
              Toast.makeText(context, "Tweet Sucessfully Posted", Toast.LENGTH_SHORT).show();
              AppUtils.socialShare(context, AppSharedPreference.getInstance(context).getUserID(),"twitter");
              if(context instanceof ShareToEarnCreditActivity){
            	  ((ShareToEarnCreditActivity)context).updateCredit();
              }
              progress.dismiss();
          } else {
              progress.dismiss();
              Toast.makeText(context, "Error while tweeting !", Toast.LENGTH_SHORT).show();
              progress.dismiss();
          }

      }
	}


