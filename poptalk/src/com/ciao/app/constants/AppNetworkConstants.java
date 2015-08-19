package com.ciao.app.constants;


/**
 * Created by rajat on 3/2/15.
 * This class is used to define the network related constants for the app
 */
public class AppNetworkConstants {
    //"http://dev-api.poptalkapi.com"
    //"http://v2.poptalkapi.com";
    //"http://staging-api.poptalkapi.com"
    public static final String WEBSERVICE_BASE_URL2 = "http://v2.poptalkapi.com";
    public static final String WEBSERVICE_BASE_URL = WEBSERVICE_BASE_URL2;// "http://poptalkapi.com";
    public static final String SIGNUP_URL = WEBSERVICE_BASE_URL+"/api/auth/signup";
    public static final String LOGIN_URL = WEBSERVICE_BASE_URL+"/api/auth/login";
    public static final String CHANGE_PASSWORD = WEBSERVICE_BASE_URL+"/api/auth/change-password";
    public static final String UPDATE_CHANGE_PASSWORD = WEBSERVICE_BASE_URL+"/api/auth/update-forgot-password";
    public static final String UPDATE_PROFILE_PIC = WEBSERVICE_BASE_URL+"/api/auth/upadte-profile-image";
    public static final String UPDATE_GROUP_PIC = WEBSERVICE_BASE_URL+"/api/group/upload-image";
    public static final String VERIFY_ACCOUNT = WEBSERVICE_BASE_URL+"/api/auth/verify-account";
    public static final String UPDATE_CIAO_NUMBER = WEBSERVICE_BASE_URL+"/api/setting/update-ciao-number";
    public static final String SAVE_CIAO_NUMBER_ON_SIGNUP = WEBSERVICE_BASE_URL+"/api/auth/add-ciao-number";
    public static final String LOGOUT_URL = WEBSERVICE_BASE_URL+"/api/auth/logout";
    public static final String FORGOT_PASSWORD_URL = WEBSERVICE_BASE_URL+"/api/user/send-email-password-link";
    public static final String CHECK_CIAO_CONTACT_URL = WEBSERVICE_BASE_URL+"/api/user/contact-check";
    public static final String UPLOAD_CHAT_MEDIA_FILES = WEBSERVICE_BASE_URL+"/api/chat/upload-media";
    public static final String GET_TOTAL_CREDIT = WEBSERVICE_BASE_URL+"/api/credit/get-user-credit";
    public static final String UPDATE_DEVICE_TOKEN = WEBSERVICE_BASE_URL+"/api/user/update-device-token";
    public static final String CHANGE_LOCK_SCREEN_STATUS = WEBSERVICE_BASE_URL2+"/api/auth/change-lock-screen-status";
    public static final String DEDUCT_CREDIT = WEBSERVICE_BASE_URL+"/api/credit/deduct-credit";
    public static final String CREATE_GROUP = WEBSERVICE_BASE_URL+"/api/group/create";
    public static final String ADD_MEMBER_TO_GROUP = WEBSERVICE_BASE_URL+"/api/group/add-member";
    public static final String GET_GROUP_DETAIL = WEBSERVICE_BASE_URL+"/api/group/get-group-details";
    public static final String EDIT_GROUP_INFO= WEBSERVICE_BASE_URL+"/api/group/edit-group-name";
    public static final String CHECK_CIAO_NUMBER= WEBSERVICE_BASE_URL+"/api/user/is-ciao-number-available";
    public static final String VERIFICATION_URL = WEBSERVICE_BASE_URL2+"/api/auth/nexmo-verify-request";
    public static final String CODE_VERIFICATION_URL =WEBSERVICE_BASE_URL2+"/api/auth/nexmo-check-request";
    public static final String REGISTER_ON_ASTERISK = WEBSERVICE_BASE_URL+"/api/user/asterisk";
    public static final String NUMBER_SEARCH_URL = WEBSERVICE_BASE_URL2+"/api/auth/nexmo-number-search";
    public static final String UPDATE_NEXMO_NUMBER_URL = WEBSERVICE_BASE_URL2+"/api/auth/nexmo-number-update";
    public static final String BUY_NUMBER_URL = WEBSERVICE_BASE_URL2+"/api/auth/nexmo-number-buy";
    public static final String REPORT_AD_IMPRESSION = WEBSERVICE_BASE_URL2+"/api/user/impressions-counter";
    public static final String CHECK_IN = WEBSERVICE_BASE_URL2+"/api/credit/check-in";
    public static final String SYNC_DATA = WEBSERVICE_BASE_URL2+"/api/user/sync-data";
    public static final String SEND_MESSAGE = WEBSERVICE_BASE_URL2+"/api/user/nexmo-send-message";
    public static final String UPDEDE_CREDIT_FROM_INAPP = WEBSERVICE_BASE_URL2 + "/api/credit/google-validate-purchase";
    public static final String SOCIAL_SHARE = WEBSERVICE_BASE_URL2 + "/api/credit/social-share";
    public static final String GET_COMM_USER_BY_PHONE = WEBSERVICE_BASE_URL2 + "/api/calls/route-inbound";
    public static final String SMS_NUMBER_VALIDATION = WEBSERVICE_BASE_URL2 + "/api/auth/sms-number-validation";

    public enum NetworkKeys
    {
        errorcode,
        errstr,
        result,
        params,
        msisdn,
        country,
        type,
        cost,

    }
    public static final String GET_USER_SMS = WEBSERVICE_BASE_URL+"/api/user/get-user-messages";
    public static final String GET_MINUTES = WEBSERVICE_BASE_URL+"/api/credit/get-country-rates";
    public static final String GET_CALL_RATES_FOR_DAILED_NUMBER = WEBSERVICE_BASE_URL+"/api/credit/get-phone-rates";


}
