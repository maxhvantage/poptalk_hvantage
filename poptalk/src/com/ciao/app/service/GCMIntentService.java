package com.ciao.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.utils.AppUtils;
import com.google.android.gcm.GCMBaseIntentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = "GCMIntentService";
	public GCMIntentService() {
		super(AppConstants.GCM_SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId)
	{
		//Log.e(TAG, "Device registered: regId = " + registrationId);
	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		//Log.e(TAG, "Device unregistered");
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
        Log.d("PushNotification", "executed");
        Bundle b= intent.getExtras();
		if(b!=null)
		{
			String response = b.getString("payload");
			if(response!=null){
				Log.e("Response = ", response);

				Log.d("PushNotification", "response is "+response);

				try {
					JSONObject responseJsonObject = new JSONObject(response);
					String type = responseJsonObject.getString("type");

                    Log.d("PushNotification", "type is "+type);

                    if(type.equalsIgnoreCase("daily_credit_status")){
						JSONObject creditResponseJsonObject = responseJsonObject.getJSONObject("result");
						String totalCredit = creditResponseJsonObject.getString("credit");
						String creditIncreases = creditResponseJsonObject.getString("inc_credit");
						AppSharedPreference.getInstance(context).setIncrementedCredit(creditIncreases);
						AppSharedPreference.getInstance(context).setTotalCredit(totalCredit);
					} else if (type.equalsIgnoreCase("inform_for_check_in")) {
						String message = responseJsonObject.getString("message");
						JSONObject senderIdJsonObject = responseJsonObject.getJSONObject("result");
						String userId = senderIdJsonObject.getString("user_id");
						String userName = "";
						AppUtils.showCheckInNotification(context,userId,userName,message);
					} else if (type.equalsIgnoreCase("fyber_callback")) {
						JSONObject creditResponseJsonObject = responseJsonObject.getJSONObject("result");
						String amount = creditResponseJsonObject.getString("amount");
						int amountEarnFromfyber = Integer.parseInt(amount);
						int totalCredit = Integer.parseInt(AppSharedPreference.getInstance(context).getTotalCredit());
						int newTotalCredit = totalCredit + amountEarnFromfyber;
						AppSharedPreference.getInstance(context).setTotalCredit(Integer.toString(newTotalCredit));
						//AppUtils.showTost(context, amount+" credited");
						AppUtils.showFyberWallNotification(context, "You have earned " + amountEarnFromfyber + " credits from fyber wall");
					} else if (type.equalsIgnoreCase("new ciao message")) {
                        //startService(new Intent(this, GetSMSServices.class));

                    } else if (type.equalsIgnoreCase("check_message")){
                        try {
                            String timeStamp = ApplicationDAO.getInstance(GCMIntentService.this).getLastSMSTime();
                            if (timeStamp.length() > 10) {
                                timeStamp = timeStamp.substring(0, timeStamp.length() - 4);
                            }
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("user_security", "abchjds1256a12dasdsad67672");
                            jsonObject.put("user_id", AppSharedPreference.getInstance(GCMIntentService.this).getUserID());
                            jsonObject.put("last_timestamp", timeStamp);
                            if(NetworkStatus.isConected(GCMIntentService.this)){
                                String resp = NetworkCall.getInstance(GCMIntentService.this).hitNetwork(AppNetworkConstants.GET_USER_SMS, jsonObject);
                                JSONObject responseJson = new JSONObject(resp);
                                String errorCode = responseJson.getString("error_code");
                                if(errorCode.equalsIgnoreCase("0")){
                                    JSONArray messageResultArray = responseJson.getJSONArray("result");
                                    for(int i=0;i<messageResultArray.length();i++){
                                        JSONObject messageJsonObject = messageResultArray.getJSONObject(i);
                                        String message = messageJsonObject.getString("message");
                                        String senderId = messageJsonObject.getString("sender_number");
                                        String messageTime = messageJsonObject.getString("message_timestamp");
                                        long time = Long.parseLong(messageTime);
                                        ApplicationDAO.getInstance(GCMIntentService.this).saveSmsInDb(senderId, message, AppConstants.SMS_RECEIVED, AppConstants.SMS_RECEIVED, true,time);
                                        if(AppSharedPreference.getInstance(GCMIntentService.this).getSMSSceenVisbility()){
                                            AppUtils.playChatBubbleSound(GCMIntentService.this);
                                        }else{
                                            AppUtils.showSMSNotification(GCMIntentService.this, senderId, message, "", "");
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            // TODO: handle exception
                        }


//                    } else {
//						long time = System.currentTimeMillis();
//						String message = responseJsonObject.getString("message");
//						JSONObject senderIdJsonObject = responseJsonObject.getJSONObject("result");
//						String senderId = senderIdJsonObject.getString("sender_ciao_number");
//						ApplicationDAO.getInstance(context).saveSmsInDb(senderId, message, AppConstants.SMS_RECEIVED, AppConstants.SMS_RECEIVED, true,time);
//					    if(AppSharedPreference.getInstance(context).getSMSSceenVisbility()){
//					    	AppUtils.playChatBubbleSound(context);
//					    }else{
//					    	AppUtils.showSMSNotification(context, senderId, message, "", "");
//					    }
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Log.e(TAG, "Received message ---"+response);
			}
		}
	}


	/*private class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {
			final Context context = params[0].getApplicationContext();
			return isAppOnForeground(context);
		}

		boolean isAppOnForeground(Context context)
		{
			ActivityManager activityManager = (ActivityManager) context
			.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appProcesses = activityManager
			.getRunningAppProcesses();
			if (appProcesses == null) {
				return false;
			}
			final String packageName = context.getPackageName();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
					return true;
				}
			}
			return false;
		}
	}
*/
	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total)
	{
		//Log.e(TAG, "Received deleted messages notification");
		/*String message = getString(R.string.gcm_deleted, total);
		generateNotification(context, message);*/
	}


	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "Received error: " + errorId);
		//        Methods.displayMessage(context, getString(R.string.gcm_error, errorId));
		//        Toast.makeText(context, getString(R.string.gcm_error, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId)
	{
		// log message
		Log.e(TAG, "Received recoverable error: " + errorId);
		//        Methods.displayMessage(context, getString(R.string.gcm_recoverable_error,
		//                errorId));
		//        Toast.makeText(context, getString(R.string.gcm_recoverable_error, Toast.LENGTH_SHORT).show();
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	static int notifyid=0;

}
