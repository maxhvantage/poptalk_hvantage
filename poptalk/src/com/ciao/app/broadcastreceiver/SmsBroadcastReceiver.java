package com.ciao.app.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.VerifyNumberActivity;

/**
 * Created by rajat on 16/2/15.
 * This class is used to parse the SMS verification code and auto fill it to UI during the registration process in the app.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {

            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String message = currentMessage.getDisplayMessageBody();
                    if(message.contains("Your PopTalk")){
                        String code = AppUtils.getVerificationCodeFromSMS(message);
                        VerifyNumberActivity.setVerificationCode(code);
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }


}
