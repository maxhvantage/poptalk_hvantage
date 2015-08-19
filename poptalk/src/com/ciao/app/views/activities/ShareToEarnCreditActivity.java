package com.ciao.app.views.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.facebook.FacebookCall;
import com.ciao.app.netwrok.backgroundtasks.GetTwitterToken;
import com.ciao.app.netwrok.backgroundtasks.PostOnTwitter;
import com.ciao.app.utils.AnimationUtils;
import com.ciao.app.utils.AppUtils;
import com.facebook.Session;

/**
 * Created by rajat on 5/2/15.
 * This Activity show the option where user can share about the app with facebook and twitter to earn credits
 */
public class ShareToEarnCreditActivity extends Activity {

    private String EMAIL_SUBJECT = "Share PopTalk App";
    private String EMAIL_BODY = "I would like to invite you to use PopTalk app.";
    private TextView numberTV,totalCreditTV;
    private String numberTxt;
    private MediaPlayer player;
    private Dialog tweetDialog;
    private EditText tweet_text;
    private Button post_tweet;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_earn);
        numberTV = (TextView)findViewById(R.id.tv_my_number_to_share);
        numberTxt = "Your PopTalk Number is: \n";

        String userPhoneNumber = AppUtils.formatCiaoNumberUsingParentheses(AppSharedPreference.getInstance(this).getUserPhoneNumber());

        Log.d("PhoneNumber :", userPhoneNumber);
        numberTxt  = numberTxt + userPhoneNumber;
        numberTV.setText(numberTxt);
        
        totalCreditTV = (TextView)findViewById(R.id.tv_total_credits);
        totalCreditTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
    }

    public void goToPreviousScreen(View view){
        finish();
    }

    public void shareViaFacebook(View view){

        if (!AppSharedPreference.getInstance(ShareToEarnCreditActivity.this).getIsConnectedToInternet()){
            DialogUtils.showInternetAlertDialog(ShareToEarnCreditActivity.this);
            return;
        }

        FacebookCall.getInstance(this).postOnWall(this);

    }
    public void shareViaTwitter(View view){

        if (!AppSharedPreference.getInstance(ShareToEarnCreditActivity.this).getIsConnectedToInternet()){
            DialogUtils.showInternetAlertDialog(ShareToEarnCreditActivity.this);
            return;
        }

    	if(AppSharedPreference.getInstance(this).getTwitterAccessToken()== null)
    	{

    		new GetTwitterToken(this).execute();

    	}else
    	{
    		showTweetDialog();
    	}

    }
    public void shareViaEmail(View view){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("text/html");
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, EMAIL_SUBJECT);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, EMAIL_BODY);
        //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+fileName));
        startActivity(Intent.createChooser(emailIntent, "Sharing Options"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!AppConstants.FB_LOGIN) {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }
    
    public void animateCoin(View view){
        AnimationUtils.rotateCreditCoin((ImageView)view);
        AppUtils.playBeep(this);
    }
   
    public void updateCredit(){
    	 totalCreditTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
    	 
    }
    
    public void showTweetDialog(){
    	tweetDialog = new Dialog(this);
		tweetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		tweetDialog.setContentView(R.layout.tweet_dialog);
        tweet_text = (EditText) tweetDialog.findViewById(R.id.tweet_text);
        post_tweet = (Button) tweetDialog.findViewById(R.id.post_tweet);
        post_tweet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	tweetDialog.dismiss();
            	new PostOnTwitter(ShareToEarnCreditActivity.this,formatedTweetText()).execute();
            }
        });
        tweetDialog.show();
    }

    private String formatedTweetText(){
        return tweet_text.getText().toString().trim() + "\n " + AppConstants.APP_LINK;
    }

}
