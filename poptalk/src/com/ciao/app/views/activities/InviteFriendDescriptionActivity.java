package com.ciao.app.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.utils.AnimationUtils;
import com.ciao.app.utils.AppUtils;

/**
 * Created by rajat on 5/2/15.
 */
public class InviteFriendDescriptionActivity extends Activity {
    private TextView myNumberTV,totalCreditTV;
    private String numberTxt;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_freind);
        myNumberTV = (TextView) findViewById(R.id.tv_ciao_number);
        numberTxt = "Your PopTalk number is: \n" + AppUtils.formatCiaoNumberUsingParentheses(AppSharedPreference.getInstance(this).getUserCiaoNumber());
        myNumberTV.setText(numberTxt);
        
        totalCreditTV = (TextView) findViewById(R.id.tv_total_credits);
        totalCreditTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
    }

    public void goToPreviousScreen(View view) {
        finish();
    }

    public void gotInviteContactScreen(View view) {
        //startActivity(new Intent(this, InviteContactsActivity.class));
    	Intent intent = new Intent(this,InterstialContactListActivity.class);
		intent.putExtra("operation",AppConstants.CONTACT_INVITE);
		startActivity(intent);
    	
    }
    public void animateCoin(View view){
        AnimationUtils.rotateCreditCoin((ImageView)view);
        playBeep();
    }
    public void playBeep() {
        try {
            AssetFileDescriptor afd = getAssets().openFd("coins.mp3");
            if(player==null){
                player = new MediaPlayer();
            }
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
            player = null;
        } catch (Exception e) {
            e.printStackTrace();
            player = null;
        }
    }
}
