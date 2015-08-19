package com.ciao.app.apppreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.ciao.app.constants.AppConstants;
import com.ciao.app.netwrok.backgroundtasks.UpdateDeviceTokenAsyncTask;
import com.csipsimple.ui.prefs.CallLimitPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.lockerapplication.preferences.AppPreferencesKeys;

/**
 * Created by rajat on 3/2/15.
 */
public class AppSharedPreference {
	public static AppSharedPreference appSharedPreference;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private Context mContext;
	private Map<String, ServiceMethod> serviceMethodMap = new HashMap<String, ServiceMethod>();

	/*
	* Interface to help map each get and set method to be callable by string key
	* */
	public interface ServiceMethod {
		public Object execute();
	}

	/*
	* Add mappings of keywords to methods to execute
	* */
	private void addServiceMethods(){
		serviceMethodMap.put("dob", new ServiceMethod() {
            public Object execute() {
                return getUserDOB();
            }
        });
		serviceMethodMap.put("gender",new ServiceMethod() {
            public Object execute() {
                return getUserGender();
            }
        });
		serviceMethodMap.put("ciaoNumber",new ServiceMethod() {
            public Object execute() {
                return getUserCiaoNumber();
            }
        });
		serviceMethodMap.put("msisdn",new ServiceMethod() {
            public Object execute() {
                return getUserMSISDN();
            }
        });
		serviceMethodMap.put("countryCode",new ServiceMethod() {
            public Object execute() {
                return getUserCountryCode();
            }
        });
		serviceMethodMap.put("phoneNumber",new ServiceMethod() {
            public Object execute() {
                return getUserPhoneNumber();
            }
        });
		serviceMethodMap.put("userID",new ServiceMethod() {
			public Object execute() {
				return getUserID();
			}
		});
		serviceMethodMap.put("comm_user",new ServiceMethod() {
			public Object execute() {
				return getCommUser();
			}
		});
		serviceMethodMap.put("comm_security",new ServiceMethod() {
			public Object execute() {
				return getCommSecurity();
			}
		});
	}

	public HashMap<String,Object> getUserParams(ArrayList<String> userKeyParams){
		HashMap<String,Object> userParams = new HashMap<String,Object>();
		for(String key : userKeyParams) {
			ServiceMethod method = serviceMethodMap.get(key);
			if(method != null){
				userParams.put(key,method.execute());
				Log.d("User Param added ", key + ":" + method.execute());
			}
		}
		return userParams;
	}

	public static AppSharedPreference getInstance(Context context){
		if(appSharedPreference == null){
			appSharedPreference = new AppSharedPreference(context);
		}
		return appSharedPreference;
	}

	public AppSharedPreference(Context context) {
		this.mContext = context;
		this.sharedPreferences = context.getSharedPreferences(AppConstants.APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		this.editor = sharedPreferences.edit();
		this.addServiceMethods();
	}
	public Editor getEditor()
	{
		return editor;
	}

	public void commitEditor()
	{
		editor.commit();
	}

	public void clearEditor()
	{
		editor.clear();
		editor.commit();
	}

	public void setUserFirstName(String userFirstName){
		editor.putString("fname", userFirstName);
		editor.commit();
	}
	public void setUserEmail(String userEmail){
		editor.putString("email", userEmail);
		editor.commit();
	}
	public void setCommUser(String userEmail){
		editor.putString("comm_user", userEmail);
		editor.commit();
	}
	public void setCommSecurity(String userEmail){
		editor.putString("comm_security", userEmail);
		editor.commit();
	}

	public void setUserGender(String userGender){
		editor.putString("gender",userGender);
		editor.commit();
	}

	public void setUserDOB(String userDOB){
		editor.putString("dateOfBirth",userDOB);
		editor.commit();
	}

	public void setUserCountryCode(String countryCode){
		editor.putString("country_code",countryCode);
		editor.commit();
	}
	public void setUserPhoneNumber(String phoneNumber){
		editor.putString("phone_number", phoneNumber);
		editor.commit();
	}
	public void setUserCiaoNumber(String phoneNumber){
		editor.putString("ciao_number",phoneNumber);
		editor.commit();
		CallLimitPreference.getInstance(mContext).setUserCiaoNumber(phoneNumber);
	}

	public void setAlreadyLoginFlag(boolean flag){
		editor.putBoolean("already_login", flag);
		editor.commit();
	}

	public void setAlreadyVerified(boolean flag){
		editor.putBoolean("account_verified", flag);
		editor.commit();
	}

	public boolean getAlreadyVerified(){
		return  sharedPreferences.getBoolean("account_verified", false);
	}
	public  void setUserID(String userID){
		editor.putString("user_id",userID);
		editor.commit();
	}
	public  String getUserID(){
		return sharedPreferences.getString("user_id", "-1");

	}

	public String getUserGender(){
		return sharedPreferences.getString("gender",null);
	}
	public String getUserDOB(){
		return sharedPreferences.getString("dateOfBirth",null);
	}

	public boolean getAlreadyLoginFlag(){
		return  sharedPreferences.getBoolean("already_login", false);
	}

	public String getUserCountryCode(){
		return  sharedPreferences.getString("country_code", "1");
	}
	public String getUserPhoneNumber(){
		return  sharedPreferences.getString("phone_number",null);
	}
    public String getUserMSISDN() { return getUserCountryCode()+getUserPhoneNumber();}
	public String getUserCiaoNumber(){
		return  sharedPreferences.getString("ciao_number",null);
	}
	public String getProfilePic(){
		return  sharedPreferences.getString("profile_pic",null);
	}
	public String getCommUser(){
		return  sharedPreferences.getString("comm_user",null);
	}
	public String getCommSecurity(){
		return  sharedPreferences.getString("comm_security",null);
	}
	public void setProfilePic(String profilePic){
		editor.putString("profile_pic",profilePic);
		editor.commit();
	}

	public void setAdLockScreenVisibility(boolean visibility){
		editor.putBoolean("lock_screen_visibility",visibility);
		editor.commit();
	}
	public boolean getAdLockScreenVisibility(){
		return  sharedPreferences.getBoolean("lock_screen_visibility",true);
	}
	public void setVerificationCode(String verificationCode){
		editor.putString("verification_code",verificationCode);
		editor.commit();
	}
	public String getVerificationCode(){
		return sharedPreferences.getString("verification_code",null);
	}

	public void setForgotPasswordRequest(boolean changePasswordRequest) {
		editor.putBoolean("forgot_password",changePasswordRequest);
		editor.commit();
	}

	public boolean getForgotPasswordRequest(){
		return sharedPreferences.getBoolean("forgot_password",false);
	}

	public void setAppContactSynced(boolean isSynced){
		editor.putBoolean("isSynced",isSynced);
		editor.commit();
	}

	public boolean getAppContactSyncing(){
		return sharedPreferences.getBoolean("isSyncing",false);
	}
	public void setAppContactSyncing(boolean isSynced){
		editor.putBoolean("isSyncing",isSynced);
		editor.commit();
	}

	public boolean getAppContactSynced(){
		return sharedPreferences.getBoolean("isSynced",false);
	}

	public void setRegisteredOnChatServer(boolean isRegistered){
		editor.putBoolean("registered",isRegistered);
		editor.commit();
	}
	public void setLoggedInOnChatServer(boolean isLogin){
		editor.putBoolean("login",isLogin);
		editor.commit();
	}
	public boolean getRegisteredOnChatServer(){
		return sharedPreferences.getBoolean("registered",false);
	}
	public boolean getLoggedInOnChatServer(){
		return sharedPreferences.getBoolean("login",false);
	}

	public void setChatSceenVisbility(boolean flag){
		editor.putBoolean("chat_on",flag);
		editor.commit();
	}

	public boolean getChatSceenVisbility(){
		return sharedPreferences.getBoolean("chat_on",false);
	}

	public void setSMSSceenVisbility(boolean flag){
		editor.putBoolean("sms_on",flag);
		editor.commit();
	}

	public boolean getSMSSceenVisbility(){
		return sharedPreferences.getBoolean("sms_on",false);
	}

	public void setDeviceToken(String deviceToken){
		editor.putString("device_token",deviceToken);
		editor.commit();
		if(!getUserID().equalsIgnoreCase("-1")){
			new UpdateDeviceTokenAsyncTask(mContext).execute();
		}
	}

	public String getDeivceToken(){
		return sharedPreferences.getString("device_token", "12345");
	}
	public String getTotalCredit(){
		return sharedPreferences.getString("total_credit", "0");
	}
	public  void setTotalCredit(String totalCredit){
		editor.putString("total_credit",totalCredit);
		editor.commit();
	}

	public boolean getIsConnectedToInternet(){
		return sharedPreferences.getBoolean("isConnectedToInternet",false);
	}

	public void setIsConnectedToInternet(boolean b) {
		editor.putBoolean("isConnectedToInternet",b);
		editor.commit();
	}

	public void clearPreferences() {
		editor.clear();
		editor.commit();
	}

	public boolean getBoolean(String key) {
		return sharedPreferences.getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultVal) {
		return sharedPreferences.getBoolean(key, defaultVal);
	}

	public int getInt(String key) {
		return sharedPreferences.getInt(key, 0);
	}

	public int getInt(String key, int defaultVal) {
		return sharedPreferences.getInt(key, defaultVal);
	}

	public long getLong(String key) {
		return sharedPreferences.getLong(key, 0);
	}

	public long getLong(String key, long defaultVal) {
		return sharedPreferences.getLong(key, defaultVal);
	}

	public String getString(String key) {
		return sharedPreferences.getString(key, "");
	}

	public String getString(String key, String defaultVal) {
		return sharedPreferences.getString(key, defaultVal);
	}



	public void setBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void setInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void setLong(String key, long value) {
		editor.putLong(key, value);
		editor.commit();

	}

	public void setString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public String getTwitterAccessToken()
	{
		return sharedPreferences.getString("twitter_access_token",null);
	}

	public void setTwitterAccessToken(String twitterAccessToken)
	{
		editor.putString("twitter_access_token", twitterAccessToken);
		editor.commit();
	}

	public String getTwitterAccessTokenSecret()
	{
		return sharedPreferences.getString("twitter_access_token_secret",null);
	}

	public void setTwitterAccessTokenSecret(String twitterAccessTokenSecret)
	{
		editor.putString("twitter_access_token_secret", twitterAccessTokenSecret);
		editor.commit();
	}


	public void setIncrementedCredit(String creditIncreases) {
		editor.putString("inc_credit", creditIncreases);
		editor.commit();

	}
	public String getIncrementedCredit(){
		return sharedPreferences.getString("inc_credit","0.0");
	}

	public void setChekinDate(String credit) {

		editor.putString(AppPreferencesKeys.ChekinDate, credit);
		editor.commit();  
	}

	public String getChekinDate(){
		return sharedPreferences.getString(AppPreferencesKeys.ChekinDate,"");
	}

	public void SetChekinButtonVisibility(Boolean credit) {
		editor.putBoolean(AppPreferencesKeys.ChekinButtonVisibility, credit);
		editor.commit();  
	}

	public Boolean getChekinButtonVisibility(){
		return sharedPreferences.getBoolean(AppPreferencesKeys.ChekinButtonVisibility,false);
	}

	public void setAppOpened(boolean isAppOpened){
		editor.putBoolean("is_app_opened", isAppOpened);
		editor.commit(); 	
	}

	public Boolean isAppOpened(){
		return sharedPreferences.getBoolean("is_app_opened", false);
	}

	public void setProfilePicUploaded(boolean isPicUploaded){
		editor.putBoolean("pic_uploaded", isPicUploaded);
		editor.commit(); 	
	}
	public Boolean getProfilePicUploaded(){
		return sharedPreferences.getBoolean("pic_uploaded", false);	
	}
	public void setProfilePicUploading(boolean isPicUploading){
		editor.putBoolean("pic_uploading", isPicUploading);
		editor.commit(); 	
	}

	public Boolean getIsProfilePicUploading(){
		return sharedPreferences.getBoolean("pic_uploading", false);
	}
	
	public synchronized void setLastCallDuration(long callDuration){
		editor.putLong("call_duration",callDuration);
		editor.commit();
		
	}
	public long getLastCallDuration(){
		return sharedPreferences.getLong("call_duration", 0);
	}

	public void setVerificationRequestId(String requestID) {
		editor.putString("requestID",requestID);
		editor.commit();
		
	}
	public String getVerificationRequestId() {
		return sharedPreferences.getString("requestID", "");
		
	}

	public void setCiaoRatesSynced(boolean ratesSynced) {
		editor.putBoolean("ratesSynced",ratesSynced);
		editor.commit();
	}
	public boolean getCiaoRatesSynced(){
		return sharedPreferences.getBoolean("ratesSynced", false);
	}
	
	public void setIsCaioOutCall(boolean ciaoOut) {
		editor.putBoolean("ciao_out",ciaoOut);
		editor.commit();
	}
	public boolean getIsCaioOutCall(){
		return sharedPreferences.getBoolean("ciao_out", false);
	}
	
	public void updateMissedCallCount() {
		int oldCount = getMissedCallCount();
		editor.putInt("missed_call_count", oldCount+1);
		editor.commit();
		
	}
	 
	public int getMissedCallCount(){
		return sharedPreferences.getInt("missed_call_count", 0);
	}
	
	public void setMissedCallCount() {
		editor.putInt("missed_call_count", 0);
		editor.commit();
		
	}
	
}
