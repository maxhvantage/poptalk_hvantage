package com.ciao.app.constants;

/**
 * Created by rajat on 25/1/15.
 * This class is used to define constants used in app
 */
public class AppConstants {
    public final static int CALL_FRAGMENT_ID = 0;
    public final static int MESSAGE_FRAGMENT_ID = 1;
    public final static int CREDITS_FRAGMENT_ID = 2;
    public final static int SETTINGS_FRAGMENT_ID = 3;
    public final static int KEYPAD_FRAGMENT_ID = 4;
    public final static int CALLLOG_FRAGMENT_ID = 5;
    public final static int CONTACTS_FRAGMENT_ID = 6;
    public final static int MESSAGE_VIEW_FRAGMENT_ID = 7;
    public final static int SMS_FRAGMENT_ID = 8;
    public final static int CHAT_FRAGMENT_ID = 8;
    public final static int CIAO_CONTACT_FRAGMENT_ID = 9;
    public final static int CIAO_OUT_CONTACT_FRAGMENT_ID = 10;
    public final static int CONTACT_INVITE = 1;
    public final static int CONTACT_DETAIL = 2;
    public final static int CONTACT_MESSAGE = 3;


    public static String APP_SHARED_PREFERENCE = "ciao_preference";
    public static boolean FB_LOGIN  = false;
    public static String APP_IMAGE_BASE_FOLDER = android.os.Environment.getExternalStorageDirectory() + "/Ciao/Images";
    public static String APP_VIDEO_BASE_FOLDER = android.os.Environment.getExternalStorageDirectory() + "/Ciao/Video";
    public static String APP_PROFILE_PIC = APP_IMAGE_BASE_FOLDER+"/profile_pic.png";
    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }
    public final static int SMS_SENT = 1;
    public final static int SMS_RECEIVED = 2;
    public final static int SMS_SENDING_FAILED = 3;
    //public final static String GCM_SENDER_ID = "98264692131";
    public final static String GCM_SENDER_ID = "34825774810";
    public static final String INTESTIAL_ADD = "interstital_image";
    public static final String FACEBOOK_INTESTIAL = "facebook_interstital";
    public static final String FACEBOOK_BANNER = "facebook_banner";
    public static final String Advertisement = "advertisement";
    public static final String CIAO_APP = "ciao_app";
    public static final String BANNER_IMAGE = "banner_image";
    public static final String INTERTIAL_IMAGE = "interstital_image";
    public static final String INTERTIAL_VIDEO = "interstital_video";
    public static final String UNLOCK = "unlock";
    public static final String REFFERAL = "refferal";
    public static final String INVITE_FRIEND = "invite_friend";
    public static final String SHARE = "share";
    public static final String PURCHASE = "purchase";
    public static final String FYBER_WALL = "fyber_wall";
    public static final String CONSUMER_KEY = "o3SVjCemfIegNESqcnyWW9F26";//twitter key
    public static final String CONSUMER_SECRET = "xStTQyhzzKhPzWCy6fF66FVQYdM1PvqrYUtutfQ9YcWoYam2Iy";//twitter
    public static final String APP_LINK = "https://play.google.com/store/apps/details?id=com.poptalk.app";
    public static final String DEDUCTION_TYPE_CALL  = "outbound_voice";
    public static final String DEDUCTION_TYPE_SMS = "outbound_sms";
    public static final String EDIT_GROUP_NAME="2tkUHRM1DnsBwixYSisS71RKK#@!";
    public static final String SALT="2tkUHRM1DnsBwixYSisS71RKK";
}
