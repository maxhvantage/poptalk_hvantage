package com.ciao.app.utils;

import in.lockerapplication.asyncTask.AddCreditAsyncTask;
import in.lockerapplication.networkKeys.NetworkKeys;
import in.lockerapplication.networkcall.CheckConnection;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.SignUpBean;
import com.ciao.app.imagecropper.CropUtil;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.views.activities.CallActivity;
import com.ciao.app.views.activities.ChatActivity;
import com.ciao.app.views.activities.GroupChatActivity;
import com.ciao.app.views.activities.NewSmsActivity;
import com.ciao.app.views.activities.SplashActivity;

/**
 * Created by rajat on 3/2/15.
 */
public class AppUtils {

	public static void showTost(Context context, String toastMessage) {
		Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
	}

	public static boolean isValidatePhoneNumber(String dialedPhoneNumber) {
		if (dialedPhoneNumber.equalsIgnoreCase("")) {
			return false;
		} else if (dialedPhoneNumber.length() != 10) {
			return false;
		} else {
			return true;
		}
	}

	private static AppEventsLogger facebookLogger;

	public static AppEventsLogger getFacebookLogger(Context context) {
		if (facebookLogger == null) {
			facebookLogger = AppEventsLogger.newLogger(context);
		}

		return facebookLogger;
	}

	private static MediaPlayer player;

	// This method is used to validate the sign up  form data
	public static JSONObject isFormDataValid(SignUpBean signUpBean, int page) {
		JSONObject responseJsonObject = new JSONObject();
		JSONObject isFormValid = new JSONObject();
		if (page == 0) {
			if ((signUpBean.getUserFirstName() == null)
					|| (signUpBean.getUserFirstName().length() == 0)) {
				try {
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message", "Please enter your full name.");
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			} else if ((signUpBean.getEmail() == null)
					|| (signUpBean.getEmail().length() == 0)) {
				try {
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message", "Please enter your email id.");
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			} else if (!isValidEmail(signUpBean.getEmail())) {
				try {
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message","Please enter a valid email address.");
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			} else if ((signUpBean.getDob() == null)
					|| (signUpBean.getDob().length() == 0)) {
				try {
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message", "Please enter your birthday.");
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			} else if ((signUpBean.getPassword() == null)
					|| (signUpBean.getPassword().length() == 0)) {
				try {
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message","Please enter your password.");
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			} else if (signUpBean.getGender() == null) {
				try {
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message",
							"Please select a gender.");
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			}

			else if(!validatePasswordPattern(signUpBean.getPassword())){
				try	{
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message","Please choose a password that has a min 6 characters.");
					responseJsonObject.put("response", isFormValid);
					return	responseJsonObject;
				} catch (JSONException e){
					return null;
				}

			}else if (signUpBean.getReferalCode().length()>0&&signUpBean.getReferalCode().length()<10) {
				try	{
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message","Please enter a valid referral code.");
					responseJsonObject.put("response", isFormValid);
					return	responseJsonObject;
				} catch (JSONException e){
					return null;
				}
			}
			else if (signUpBean.getReferalCode().length()==10) {
				try	{
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message","1");
					responseJsonObject.put("response", isFormValid);
					return	responseJsonObject;
				} catch (JSONException e){
					return null;
				}
			}

			else {
				try {
					isFormValid.put("isFormValid", true);
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			}
		} else {
			if ((signUpBean.getPhoneNumber() == null)
					|| (signUpBean.getPhoneNumber().length() == 0)) {
				try {
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message",
							"Please Enter Your Phone Number.");
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			} else if (signUpBean.getPhoneNumber().length() != 10) {
				try {
					isFormValid.put("isFormValid", false);
					isFormValid.put("error_message",
							"Please enter a valid phone number");
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			} else {
				try {
					isFormValid.put("isFormValid", true);
					responseJsonObject.put("response", isFormValid);
					return responseJsonObject;
				} catch (JSONException e) {
					return null;
				}
			}
		}

	}

	/**
	 * check whether the given mail id is valid or not
	 *
	 * @param email
	 *            : string which need to be test
	 * @return : return true if valid or false if not valid
	 */
	public static boolean isValidEmail(CharSequence email) {
		if (TextUtils.isEmpty(email)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		}
	}

	public static boolean validatePasswordPattern(String password) {
		/*Pattern passwordPattern = Pattern
				.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})");
		Matcher mtch = passwordPattern.matcher(password);
		if (mtch.matches()) {
			return true;
		}*/
		if(password.length()>=6){
			return true;
		}
		return false;
	}

	public static boolean isSdCardAvailable() {
		Boolean isSDPresent = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);
		if (isSDPresent) {
			return true;
		} else
			return false;
	}

	public static String getVerificationCodeFromSMS(String sms) {
		String verificationCode = sms.replaceAll("\\D+", "");
		verificationCode = verificationCode.substring(0, 4);
		return verificationCode;
	}

	public static Bitmap hanldeImageRotation(Uri uri, Context context) {
		String filename = getFileNameFromUri(uri, context);
		Bitmap rotatedBitmap = null;
		if (filename != null) {
			try {
				int rotate = CropUtil.getExifRotation(CropUtil.getFromMediaUri(context, context.getContentResolver(), uri));
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
				Matrix matrix = new Matrix();
				matrix.postRotate(rotate);
				rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return rotatedBitmap;
	}

	public static String getFileNameFromUri(Uri uri, Context context) {
		String scheme = uri.getScheme();
		String fileName = null;
		if (scheme.equals("file")) {
			fileName = uri.getLastPathSegment();
		} else if (scheme.equals("content")) {
			String[] proj = { MediaStore.Images.Media.TITLE };
			Cursor cursor = context.getContentResolver().query(uri, proj, null,
					null, null);
			if (cursor != null && cursor.getCount() != 0) {
				int columnIndex = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
				cursor.moveToFirst();
				fileName = cursor.getString(columnIndex);
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return fileName;
	}

	public static String getRealPathFromURI(Uri contentUri, Context context) {
		String[] proj = { MediaStore.Images.Media.DATA };
		// This method was deprecated in API level 11
		// Cursor cursor = managedQuery(contentUri, proj, null, null, null);

		CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj,
				null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static String formatPhoneNumber(Context context, String number) {
		number = number.trim();
		number = number.replace(" ", "");
		number = number.replace("-", "");
		number = number.replace("(", "");
		number = number.replace(")", "");
		if (number.contains("+")) {
			number = number.replace("+", "");
		} else if (number.startsWith("0")) {
			String temp = number.substring(0, 2);
			if (temp.equalsIgnoreCase("00")) {
				number = number.substring(2);
			} else {
				number = number.substring(1);
				number = AppSharedPreference.getInstance(context)
						.getUserCountryCode() + number;
			}

		} else {
			if(number.length()>10){
				return number;
			}else{
				number = AppSharedPreference.getInstance(context).getUserCountryCode() + number;
			}

		}
		return number;
	}

	public static void showNotification(Context context, String userJid, String message, String name, String pic,boolean isGroupChat) {
		Intent intent;
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Notification notification;
		if(isGroupChat){
			intent = new Intent(context, GroupChatActivity.class);
		}else{
			intent = new Intent(context, ChatActivity.class);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		String phoneNumber = ApplicationDAO.getInstance(context).getCiaoNumberFromCommUser(userJid);
		if (phoneNumber == null) {
			phoneNumber = ApplicationDAO.getInstance(context).getUserNameFromCommUser(userJid, phoneNumber);
		}
		intent.putExtra("jid", userJid);
		intent.putExtra("user_name", phoneNumber);
		intent.putExtra("user_pic", pic);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
			notification = new Notification(R.mipmap.ic_launcher, message,
					System.currentTimeMillis());
			notification.setLatestEventInfo(context, name, message, pIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(StringUtils.parseBareAddress(userJid)
					.hashCode(), notification);
		} else {
			Uri soundUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context);
			notification = builder.setContentIntent(pIntent)
					.setSmallIcon(R.mipmap.ic_launcher).setTicker(message)
					.setWhen(System.currentTimeMillis()).setAutoCancel(true)
					.setContentTitle(name).setSound(soundUri)
					.setContentText(message).build();
			notificationManager.notify(StringUtils.parseBareAddress(userJid)
					.hashCode(), notification);
		}
	}

	public static void showSMSNotification(Context context, String userJid,String message, String name, String pic) {
		Intent intent;
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Notification notification;

		intent = new Intent(context, NewSmsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		String[] array = userJid.split("@");
		if(name!=null && name.length() ==0){
			name = array[0];
		}
		intent.putExtra("_id", array[0]);
		intent.putExtra("_name", name);
		intent.putExtra("_pic", pic);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
			notification = new Notification(R.mipmap.ic_launcher, message,
					System.currentTimeMillis());
			notification.setLatestEventInfo(context, array[0], message, pIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(StringUtils.parseBareAddress(userJid)
					.hashCode(), notification);
		} else {
			Uri soundUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context);
			notification = builder.setContentIntent(pIntent)
					.setSmallIcon(R.mipmap.ic_launcher).setTicker(message)
					.setWhen(System.currentTimeMillis()).setAutoCancel(true)
					.setContentTitle(name).setSound(soundUri)
					.setContentText(message).build();
			notificationManager.notify(StringUtils.parseBareAddress(userJid)
					.hashCode(), notification);
		}
	}

	public static String formatPhoneNumberUsingParentheses(String phoneNumber) {
		String formatedNumber = phoneNumber;
		try{
			String part1 = phoneNumber.substring(0, 3);
			String part2 = phoneNumber.substring(3, 6);
			String part3 = phoneNumber.substring(6);
			formatedNumber = "(" + part1 + ")" + part2 + "-" + part3;
		}catch(StringIndexOutOfBoundsException e){

		}
		return formatedNumber.trim();
	}

	public static String formatCiaoNumberUsingParentheses(String phoneNumber) {
		String countryCode = phoneNumber.substring(0,phoneNumber.length()-10);
		countryCode = "+"+countryCode+" ";
		phoneNumber = phoneNumber.substring(phoneNumber.length()-10, phoneNumber.length());
		String formatedNumber = phoneNumber;
		try{
			String part1 = phoneNumber.substring(0, 3);
			String part2 = phoneNumber.substring(3, 6);
			String part3 = phoneNumber.substring(6);
			formatedNumber = "(" + part1 + ") " + part2 + "-" + part3;
		}catch(StringIndexOutOfBoundsException e){

		}
		return countryCode+formatedNumber.trim();
	}

	public static String formatNumberUsingParenthesesforCallLog(String phoneNumber,Context context) {
		try{
			String countryCode = phoneNumber.substring(0,phoneNumber.length()-10);
			if(countryCode.equalsIgnoreCase("+0")||countryCode.equalsIgnoreCase("0")){
				countryCode = AppSharedPreference.getInstance(context).getUserCountryCode();
			}
			countryCode = countryCode+" ";
			phoneNumber = phoneNumber.substring(phoneNumber.length()-10, phoneNumber.length());
			String formatedNumber = phoneNumber;

			String part1 = phoneNumber.substring(0, 3);
			String part2 = phoneNumber.substring(3, 6);
			String part3 = phoneNumber.substring(6);
			formatedNumber = "(" + part1 + ") " + part2 + "-" + part3;
			formatedNumber = countryCode+formatedNumber.trim();
			if(!formatedNumber.startsWith("+")){
				formatedNumber = "+"+formatedNumber;
			}
			return formatedNumber;
		}catch(StringIndexOutOfBoundsException e){
			return phoneNumber.trim();
		}

	}

	public static String getRealPathFromURI(Context context, Uri contentURI,
											String id) {
		String result;
		Cursor cursor = context.getContentResolver().query(contentURI, null,
				null, null, null);
		if (cursor == null) { // Source is Dropbox or other similar local file
			// path
			result = contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(id);
			result = cursor.getString(idx);
			cursor.close();
		}
		return result;
	}

	/*public static String getContactName(Context context, String phoneNumber) {

		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME },
				null, null, null);
		if (cursor == null) {
			return null;
		}
		String contactName = null;
		if (cursor.moveToFirst()) {
			contactName = cursor.getString(cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return contactName;
	}*/


	//This method is used to add credit to server by different ways like facebook ,twitter share,watching adds etc..
	public static void socialShare(Context context, String user_id, String network)
	{
		if(CheckConnection.isConnection(context))
		{
			try
			{
				JSONObject jsonObject= new JSONObject();
				jsonObject.put(String.valueOf(NetworkKeys.user_security),"abchjds1256a12dasdsad67672");
				jsonObject.put(String.valueOf(NetworkKeys.user_id),user_id);
				jsonObject.put(String.valueOf(NetworkKeys.network),network);

				new AddCreditAsyncTask(context,jsonObject).execute();

			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}else{
			Toast.makeText(context, context.getResources().getString(R.string.nointernetconnction), Toast.LENGTH_LONG).show();
		}
	}

	public static void playChatBubbleSound(Context context) {
		try {

			AssetFileDescriptor afd = context.getAssets().openFd("chat_alert.mp3");
			if (player == null) {
				player = new MediaPlayer();
				player.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						if(player!=null){
							player.stop();
							player.release();
							player = null;
						}

					}
				});
			}
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());
			player.prepare();
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
			player = null;
		}
	}

	public static void playBeep(Context context) {
		try {

			AssetFileDescriptor afd = context.getAssets().openFd("coins.mp3");
			if (player == null) {
				player = new MediaPlayer();
				player.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						player.stop();
						player.release();
						player = null;
					}
				});
			}
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());
			player.prepare();
			player.start();

		} catch (Exception e) {
			e.printStackTrace();
			player = null;
		}
	}
	public static BigDecimal round(String d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd;
	}


	// This method is used to update the remaining credit to server after call and sms
	public static void deductCredit(final Context mContext,final String deductType,final String credit){
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("user_security", mContext.getString(R.string.user_security_key));
					jsonObject.put("user_id", AppSharedPreference.getInstance(mContext).getUserID());
					jsonObject.put("deduct_type", deductType);
					jsonObject.put("credit", credit);
					String response = NetworkCall.getInstance(mContext).hitNetwork(AppNetworkConstants.DEDUCT_CREDIT, jsonObject);
					JSONObject responseJsonObject = new JSONObject(response);
					String errorCode = responseJsonObject.getString("error_code");
					if(errorCode.equalsIgnoreCase("0")){
						JSONObject resultJsonObject = responseJsonObject.getJSONObject("result");
						String totalCreditLeft = resultJsonObject.getString("total_credit");
						AppSharedPreference.getInstance(mContext).setTotalCredit(totalCreditLeft);
					}

					Log.e("Response = ", response);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		});
		thread.start();

	}

	public static void showCheckInNotification(Context context,String userID,String userName,String message) {
		Intent intent;
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Notification notification;
		intent = new Intent(context, SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
			notification = new Notification(R.mipmap.ic_launcher, message,System.currentTimeMillis());
			notification.setLatestEventInfo(context, userName, message, pIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(Integer.parseInt(userID), notification);
		} else {
			Uri soundUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context);
			notification = builder.setContentIntent(pIntent)
					.setSmallIcon(R.mipmap.ic_launcher).setTicker(message)
					.setWhen(System.currentTimeMillis()).setAutoCancel(true)
					.setContentTitle(userName).setSound(soundUri)
					.setContentText(message).build();
			notificationManager.notify(Integer.parseInt(userID), notification);
		}

	}

	public static void showFyberWallNotification(Context context,String message) {
		Intent intent;
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Notification notification;
		intent = new Intent(context, SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
			notification = new Notification(R.mipmap.ic_launcher, message,System.currentTimeMillis());
			notification.setLatestEventInfo(context, "", message, pIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(1, notification);
		} else {
			Uri soundUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context);
			notification = builder.setContentIntent(pIntent)
					.setSmallIcon(R.mipmap.ic_launcher).setTicker(message)
					.setWhen(System.currentTimeMillis()).setAutoCancel(true)
					.setContentTitle("Fyber Credit").setSound(soundUri)
					.setContentText(message).build();
			notificationManager.notify(1, notification);
		}

	}

	public static Integer getUserAgeFromDob(Context context ,int year, int monthOfYear, int dayOfMonth){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
		return age;
	}

	public static String parseFormattedNumber(String phoneNumber){
		phoneNumber = phoneNumber.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
		return phoneNumber;
	}
	public static String parseFormattedNumberFromCallLog(String phoneNumber){
		phoneNumber = phoneNumber.replace("(", "").replace(")", "").replace("-", "").replace(" ", "").replace("+", "");
		return phoneNumber;
	}

	public static Bitmap decodeFile(String path, int targetW, int targetH) {//you can provide file path here 
		int orientation;
		try {
			if (path == null) {
				return null;
			}
			// decode image size 
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 0;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale++;
			}

			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			//inJustDecodeBounds = true <-- will not load the bitmap into memory
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			// Determine how much to scale down the image
			int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

			// Decode the image file into a Bitmap sized to fill the View
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

			//   // decode with inSampleSize
			//   BitmapFactory.Options o2 = new BitmapFactory.Options();
			//   o2.inSampleSize = scale;
			//   Bitmap bm = BitmapFactory.decodeFile(path, o2);
			Bitmap bm = BitmapFactory.decodeFile(path, bmOptions);
			Bitmap bitmap = bm;

			ExifInterface exif = new ExifInterface(path);

			orientation = exif
					.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

			Log.e("ExifInteface .........", "rotation ="+orientation);

			//           exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

			Log.e("orientation", "" + orientation);
			Matrix m = new Matrix();

			if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
				m.postRotate(180);
				//               m.postScale((float) bm.getWidth(), (float) bm.getHeight());
				// if(m.preRotate(90)){
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				m.postRotate(90);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			}
			else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				m.postRotate(270);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			}
			else if (orientation == ExifInterface.ORIENTATION_UNDEFINED) {

				if(Build.MANUFACTURER.toString().equalsIgnoreCase("Samsung")){
					m.postRotate(270+180);
					Log.e("in orientation", "" + orientation);
					bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
							bm.getHeight(), m, true);
					return bitmap;
				}
			}
			return bitmap;
		} catch (Exception e) {
			return null;
		}

	}

	public static void getLastCallMinutes(Context mContext){
		//CallLimitPreference.getInstance(mContext).getLastCallDuration()/1000;
	}

	/*public static int getTotalCiaoOutSms(Context mContext,String countryCode){
		String totalCreditString = AppSharedPreference.getInstance(mContext).getTotalCredit();
		double totalCredit = Double.parseDouble(totalCreditString);
		double smsRate = Double.parseDouble(ApplicationDAO.getInstance(mContext).getSMSRateForSelectedCountry("+"+countryCode));
		int numberOfSms = (int)(totalCredit/(smsRate*100));
		return numberOfSms;
	}*/

	// This method is used to get number of available sms out for remaining credits.
	public static int getTotalCiaoOutSms(Context mContext,String smsRates){
		String totalCreditString = AppSharedPreference.getInstance(mContext).getTotalCredit();
		double totalCredit = Double.parseDouble(totalCreditString);
		double smsRate = Double.parseDouble(smsRates);
		int numberOfSms = (int)(totalCredit/(smsRate*200));
		return numberOfSms;
	}

	/*public static int getTotalCiaoOutCall(Context mContext,String countryCode){
		String totalCreditString = AppSharedPreference.getInstance(mContext).getTotalCredit();
		double totalCredit = Double.parseDouble(totalCreditString);
		double callRate = Double.parseDouble(ApplicationDAO.getInstance(mContext).getCallRateForSelectedCountry("+"+countryCode));
		int numberOfCalls = (int)(totalCredit/(callRate*100));
		return numberOfCalls;
	}*/


	// This method is used to get number of available call for remaining credits.
	public static int getTotalCiaoOutCall(Context mContext,String mobileCallRates){
		String totalCreditString = AppSharedPreference.getInstance(mContext).getTotalCredit();
		double totalCredit = Double.parseDouble(totalCreditString);
		double callRate = Double.parseDouble(mobileCallRates);
		int numberOfCalls = (int)(totalCredit/(callRate*200));
		return numberOfCalls;
	}

	public static int getCallDurationInMinutes(long callDurationInSec){
		double callDurationINMin = 0;
		callDurationINMin = callDurationInSec/60;
		if((callDurationInSec%60)>0){
			callDurationINMin++;
		}
		return (int)callDurationINMin;
	}

	public static String getCountryCodeFromNumber(String number){
		if(number.length()>10){
			return number.substring(0, number.length()-10);
		}else{
			return "+1";
		}


	}

	/*public static void handleCreditForCall(Context context,String countryCode){
		if(AppSharedPreference.getInstance(context).getLastCallDuration()>1000){
			double callRate = Double.parseDouble(ApplicationDAO.getInstance(context).getCallRateForSelectedCountry(countryCode));
			long callDurationInSec = AppSharedPreference.getInstance(context).getLastCallDuration()/1000;
			int callDurationInMinute = AppUtils.getCallDurationInMinutes(callDurationInSec);
			double numberOfCallIn1Dollor = 1/callRate;
			double creditPerCallMinute = 200/numberOfCallIn1Dollor;
			double creditTobeDeducted =creditPerCallMinute*callDurationInMinute;
			int creditDeducted = (int)Math.ceil(creditTobeDeducted);
			int totalCredit = Integer.parseInt(AppSharedPreference.getInstance(context).getTotalCredit());
			totalCredit = totalCredit - creditDeducted;
			AppSharedPreference.getInstance(context).setTotalCredit(Integer.toString(totalCredit));
			AppUtils.deductCredit(context, AppConstants.DEDUCTION_TYPE_CALL, Integer.toString(creditDeducted));
			AppSharedPreference.getInstance(context).setLastCallDuration(0);
		}
	}*/

	// This method is used to deduct the credit for sms out
	public static void handleCreditForCall(Context context,String callRates){
		if(AppSharedPreference.getInstance(context).getLastCallDuration()>1000){
			double callRate = Double.parseDouble(callRates);
			long callDurationInSec = AppSharedPreference.getInstance(context).getLastCallDuration()/1000;
			int callDurationInMinute = AppUtils.getCallDurationInMinutes(callDurationInSec);
			double numberOfCallIn1Dollor = 1/callRate;
			double creditPerCallMinute = 200/numberOfCallIn1Dollor;
			double creditTobeDeducted =creditPerCallMinute*callDurationInMinute;
			int creditDeducted = (int)Math.ceil(creditTobeDeducted);
			int totalCredit = Integer.parseInt(AppSharedPreference.getInstance(context).getTotalCredit());

			int remainingCredit = totalCredit - creditDeducted;
			if(remainingCredit<=0){
				remainingCredit = 0;
				creditDeducted = totalCredit;
			}
			AppSharedPreference.getInstance(context).setTotalCredit(Integer.toString(remainingCredit));
			AppUtils.deductCredit(context, AppConstants.DEDUCTION_TYPE_CALL, Integer.toString(creditDeducted));
			AppSharedPreference.getInstance(context).setLastCallDuration(0);
			if(context instanceof CallActivity){
				((CallActivity)context).updateCredit(totalCredit);
			}

		}
	}

	/*public static void handleCreditForSMS(Context context,String countryCode,int messageCount) {
		// TODO Auto-generated method stub
		double smsRate = Double.parseDouble(ApplicationDAO.getInstance(context).getSMSRateForSelectedCountry(countryCode));
		double numberOfSMSIn1Dollor = (1/smsRate);
		double creditPerSMS = 200/numberOfSMSIn1Dollor;//1 $ 200 credit.
		int creditDeducted = (int)Math.ceil(creditPerSMS);
		int totalCredit = Integer.parseInt(AppSharedPreference.getInstance(context).getTotalCredit());
		totalCredit = totalCredit - creditDeducted;
		AppSharedPreference.getInstance(context).setTotalCredit(Integer.toString(totalCredit));
		AppUtils.deductCredit(context, AppConstants.DEDUCTION_TYPE_SMS, Integer.toString(creditDeducted));
	}*/

	// This method is used to deduct the credit for sms out
	public static void handleCreditForSMS(Context context,String smsRates,int messageCount) {
		// TODO Auto-generated method stub
		if(smsRates!=null){
			double smsRate = Double.parseDouble(smsRates);
			double numberOfSMSIn1Dollor = (1/smsRate);
			double creditPerSMS = 200/numberOfSMSIn1Dollor;//1 $ 200 credit.
			int creditDeducted = (int)Math.ceil(creditPerSMS);
			int totalCredit = Integer.parseInt(AppSharedPreference.getInstance(context).getTotalCredit());
			int remainingCredit = totalCredit - creditDeducted;
			if(remainingCredit<=0){
				remainingCredit = 0;
				creditDeducted = totalCredit;
			}
			AppSharedPreference.getInstance(context).setTotalCredit(Integer.toString(remainingCredit));
			AppUtils.deductCredit(context, AppConstants.DEDUCTION_TYPE_SMS, Integer.toString(creditDeducted));
		}

	}

	/*
	 * This method is used calculate the sms rate on the basis of country
	 * @params smsRates - sms rates for destination country
	 */
	public static int getCreditPerSms(String smsRates){
		if(smsRates!=null){
			double smsRate = Double.parseDouble(smsRates);
			double numberOfSMSIn1Dollor = (1/smsRate);
			double creditPerSMS = 200/numberOfSMSIn1Dollor;//1 $ 200 credit.
			int creditDeducted = (int)Math.ceil(creditPerSMS);
			return creditDeducted;
		}else{
			return 0;
		}
	}

	/*public static long getAvailableCallLimit(Context context,String countryCode){
		int totalCredit = Integer.parseInt(AppSharedPreference.getInstance(context).getTotalCredit());
		double callRate = Double.parseDouble(ApplicationDAO.getInstance(context).getCallRateForSelectedCountry(countryCode));
		double numberOfCallIn1Dollor = 1/callRate;
		double creditPerCallMinute = 200/numberOfCallIn1Dollor;
		int totalCallMinute = (int)(totalCredit/creditPerCallMinute);
		long callLimit = totalCallMinute*60;
		return callLimit ;
	}*/

	/*
	 * This method is used to calculate call limit based on user's remaining credits
	 */
	public static long getAvailableCallLimit(Context context,String callRates){
		int totalCredit = Integer.parseInt(AppSharedPreference.getInstance(context).getTotalCredit());
		double callRate = Double.parseDouble(callRates);
		double numberOfCallIn1Dollor = 1/callRate;
		double creditPerCallMinute = 200/numberOfCallIn1Dollor;
		int totalCallMinute = (int)(totalCredit/creditPerCallMinute);
		long callLimit = totalCallMinute*60;
		return callLimit ;
	}

	/*public static void smsCallPrices(final Context context)
	{
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					JSONObject jsonRequest = new JSONObject();
					jsonRequest.put("user_security_key",context.getString(R.string.user_security_key));
					String response = NetworkCall.getInstance(context).hitNetwork("http://ciao.appventurez.com/api/credit/get-sms-call-rates");
					JSONObject jsonObject = new JSONObject(response);
					String errorCode = jsonObject.getString("error_code");
					if(errorCode.equalsIgnoreCase("0")){
						JSONObject resultJsonObjet = jsonObject.getJSONObject("result");
						Iterator<String> iter = resultJsonObjet.keys();
						while (iter.hasNext()){
							String key = iter.next();
							try {
								JSONObject value = resultJsonObjet.getJSONObject(key);
								String countryCode = value.getString("prefix");
								String smsRates = value.getString("sms_price");
								String callRates = value.getString("call_price");
								if(smsRates.length()==0){
									smsRates = callRates;
								}
								ApplicationDAO.getInstance(context).saveCiaoRatesInLocalDb("+"+countryCode,smsRates,callRates);
							} catch (JSONException e) {
								// Something went wrong!
								AppSharedPreference.getInstance(context).setCiaoRatesSynced(false);
							}
						}
						AppSharedPreference.getInstance(context).setCiaoRatesSynced(true);
					}else{

					}


				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
		thread.start();
	}*/

	/*public static int getNumberOfMissedCall(Context context){
		String[] projection = new String[]{android.provider.CallLog.Calls.NUMBER,
				android.provider.CallLog.Calls.TYPE,
				android.provider.CallLog.Calls.DURATION,
				android.provider.CallLog.Calls.CACHED_NAME,
				android.provider.CallLog.Calls._ID};

        String where = android.provider.CallLog.Calls.TYPE+"="+android.provider.CallLog.Calls.MISSED_TYPE+" AND " + android.provider.CallLog.Calls.NEW + "=1" ;         
        Cursor c = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,projection,where, null, null);
        c.moveToFirst();    
        if(c.getCount() > 0){
        	return c.getCount();
        }else{
        	return 0;	
        }

	}

	 */

}