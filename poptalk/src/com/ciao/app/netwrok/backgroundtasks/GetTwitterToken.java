package com.ciao.app.netwrok.backgroundtasks;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.poptalk.app.R;
import com.ciao.app.constants.AppConstants;
import com.facebook.AccessToken;
/*
 * This AsyntaskLoader is used to twitter  token in background
 */
public class GetTwitterToken extends AsyncTask<String, String, String> {
	
    RequestToken requestToken=null;
    AccessToken accessToken;
    String oauth_url,oauth_verifier,profile_url;
    Dialog auth_dialog;
    WebView web;
    ProgressDialog progress;
    Bitmap bitmap;
    Context context;
    Twitter twitter;


    public GetTwitterToken(Context context)
    {
    	this.context = context;
    }
    
    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
    	
    	twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(AppConstants.CONSUMER_KEY,AppConstants.CONSUMER_SECRET);
        
    }
	@Override
	protected String doInBackground(String... params) {
		
		 try{
             requestToken=twitter.getOAuthRequestToken();
             oauth_url=requestToken.getAuthorizationURL();
             }
         catch (TwitterException e){
             e.printStackTrace();
         }return  oauth_url;
               
	}
	
	@Override
	protected void onPostExecute(String oauth_url) {
		
		if(oauth_url!=null){
            auth_dialog=new Dialog(context);
            auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            auth_dialog.setContentView(R.layout.auth_dialog);
            web= (WebView) auth_dialog.findViewById(R.id.webv);
            web.loadUrl(oauth_url);
            Log.d("Twitter OAUTH URL", oauth_url);
            web.setWebViewClient(new WebViewClient(){
                boolean authComplete=false;
                public void onPageStarted(WebView view,String url,Bitmap favicon){
                    super.onPageStarted(view, url, favicon);
                }
                public void onPageFinished(WebView view,String url) {
                    super.onPageFinished(view, url);
                    Log.d("Twitter finish URL",url);
                    if (url.contains("oauth_verifier") && authComplete == false) {
                        authComplete = true;
                        Uri uri = Uri.parse(url);
                        oauth_verifier = uri.getQueryParameter("oauth_verifier");
                        auth_dialog.dismiss();
                        new GetAccessTwitterToken(context,oauth_verifier,twitter,requestToken).execute();
                    } else if (url.contains("denied")) {
                        auth_dialog.dismiss();
                        Toast.makeText(context, "Sorry! Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            auth_dialog.show();
            auth_dialog.setCancelable(true);
        }
        else {
            Toast.makeText(context,"Sorry! Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();
           }
    }
	}
		
	


