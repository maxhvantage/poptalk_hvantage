package com.ciao.app.sms;

/**
 * Created by rajat on 12/3/15.
 */

import android.net.Uri;
import android.provider.Telephony.Sms.Inbox;

/**
 * A basic SmsQuery on android.provider.Telephony.Sms.Inbox
 * 
 * not in used
 */
public interface SmsQuery {
    int TOKEN = 1;

    static final Uri CONTENT_URI = Uri.parse("content://sms");

    static final String[] PROJECTION = {
            Inbox._ID,
            Inbox.ADDRESS,
            Inbox.BODY,
            Inbox.DATE,
    };

    static final String SORT_ORDER = Inbox.DEFAULT_SORT_ORDER;
    int ID = 0;
    int ADDRESS = 1;
    int BODY = 2;
    int DATE = 3;
}
