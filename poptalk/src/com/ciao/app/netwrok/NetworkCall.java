package com.ciao.app.netwrok;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.datamodel.BaseResponseBean;
import com.ciao.app.datamodel.NumberResponseBean;
import com.ciao.app.datamodel.SignUpBean;

/**
 * Created by rajat on 3/2/15.
 */
public class NetworkCall {
	public static NetworkCall networkCall;
	private Dialog mProgressDialog;
	public boolean isShowingDialog = false;
	private static ParserClass parserClass;

	public static NetworkCall getInstance(Context context) {
		if (networkCall == null) {
			networkCall = new NetworkCall(context);
		}
		parserClass = ParserClass.getInstance(context);
		return networkCall;
	}

	public NetworkCall(Context context) {
	}

	/**
	 * this method is use to hit web service
	 *
	 * @param url
	 * @param jsonObject return String
	 */

	public String hitNetwork(String url, JSONObject jsonObject) {
		String responseEntity = null;
		url = url.trim();
		url = url.replace(" ", "%20");
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 0);
		HttpConnectionParams.setSoTimeout(client.getParams(), 0);
		HttpResponse response;
		try {
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(jsonObject.toString());
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(entity);
			response = client.execute(post);
			/*Checking response */
			if (response != null) {
				InputStream inputStream = response.getEntity().getContent(); //Get the data in the entity
				String response_string = convertStreamToString(inputStream);
				return response_string;
			} else {
				return null;
			}

		} catch (UnsupportedEncodingException e) {
			responseEntity = "UnsupportedEncodingException";
			e.printStackTrace();
		} catch (ClientProtocolException e1) {
			responseEntity = "ClientProtocolException";
			e1.printStackTrace();
		} catch (IOException e) {
			responseEntity = "IOException";
			e.printStackTrace();
		}

		return responseEntity;
	}

	public String hitNetwork(String url) {
		String responseEntity = null;
		url = url.trim();
		Log.e("url", url);
		url = url.replace(" ", "%20");
		HttpClient client = new DefaultHttpClient();
		int timeoutConnection = 15000;
		HttpConnectionParams.setConnectionTimeout(client.getParams(), timeoutConnection);
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(client.getParams(), timeoutSocket); //Timeout Limit
		HttpResponse response;
		try {
			HttpGet httpget = new HttpGet(url);
			response = client.execute(httpget);
			/*Checking response */
			if (response != null) {
				InputStream inputStream = response.getEntity().getContent(); //Get the data in the entity
				String response_string = convertStreamToString(inputStream);
				return response_string;
			} else {
				return null;
			}

		} catch (UnsupportedEncodingException e) {
			responseEntity = "UnsupportedEncodingException";
			e.printStackTrace();
		} catch (ClientProtocolException e1) {
			responseEntity = "ClientProtocolException";
			e1.printStackTrace();
		} catch (IOException e) {
			responseEntity = "IOException";
			e.printStackTrace();
		} catch (ParseException e) {
			responseEntity = "ParseException";
			e.printStackTrace();
		}
		return responseEntity;
	}

	/**
	 * this method is use to hit web service
	 *
	 * @param url
	 * @param multipart return String
	 */


	public String hitNetwork(List<NameValuePair> pairs, String url) {
		Log.i("", "Post url---> " + url);
		Log.i("", "Post Params---> " + pairs.toString());
		String responseEntity = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000); //10000
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(pairs));
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");


			HttpResponse response = client.execute(post);
			if (response != null) {
				InputStream inputStream = response.getEntity().getContent(); //Get the data in the entity
				String response_string = convertStreamToString(inputStream);
				return response_string;	
			}
			else{
				return null;
			}

		} catch (Throwable e) {
			responseEntity = null;
			e.printStackTrace();
		}
		return responseEntity;
	}

	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			return "IOException";
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return sb.toString();
	}

	/**
	 * This method is use to show progress dialog before hitting web service
	 *
	 * @param activity
	 * @param loadingMessage
	 * @param isCancelable
	 * @return void
	 */
	public void showProgressDialog(Activity activity, String loadingMessage, boolean isCancelable) {
		mProgressDialog = new Dialog(activity);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog.setContentView(R.layout.dialog_progress_indicator);
		((TextView) mProgressDialog.findViewById(R.id.tv_text)).setText(loadingMessage);
		mProgressDialog.setCancelable(isCancelable);
		mProgressDialog.show();
		isShowingDialog = true;
	}


	/**
	 * This method is use to dismiss progress dialog after web service task completes
	 *
	 * @param
	 * @return void
	 */
	public void dismissDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			isShowingDialog = false;
		}

	}


	public String hitSignUpService(SignUpBean signUpBean, RequestBean requestBean) {
		String responseString = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(AppNetworkConstants.SIGNUP_URL);
		MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			multipartEntity.addPart("user_security", new StringBody(requestBean.getContext().getString(R.string.user_security_key)));
			multipartEntity.addPart("user_device_token", new StringBody(AppSharedPreference.getInstance(requestBean.getContext()).getDeivceToken()));
			if(signUpBean.getUserID() != null) {
				multipartEntity.addPart("user_id", new StringBody(signUpBean.getUserID()));
			}
			if(signUpBean.getUserFirstName() != null) {
				multipartEntity.addPart("first_name", new StringBody(signUpBean.getUserFirstName()));
			}
			if(signUpBean.getEmail() != null) {
			multipartEntity.addPart("email", new StringBody(signUpBean.getEmail()));
			}
			if(signUpBean.getPassword() != null) {
			multipartEntity.addPart("password", new StringBody(signUpBean.getPassword()));
			}
			if(signUpBean.getReferalCode() != null) {
			multipartEntity.addPart("referral_code", new StringBody(signUpBean.getReferalCode()));
			}
			if(signUpBean.getGender() != null) {
			multipartEntity.addPart("gender", new StringBody(signUpBean.getGender()));
			}
			if(signUpBean.getCountryCode() != null) {
			multipartEntity.addPart("country_id", new StringBody(signUpBean.getCountryCode()));
			}
			if(signUpBean.getCountryCode() != null) {
			multipartEntity.addPart("state_id", new StringBody(signUpBean.getCountryCode()));
			}
			if(signUpBean.getPhoneNumber() != null) {
			multipartEntity.addPart("phone", new StringBody(signUpBean.getPhoneNumber()));
			}
			if(signUpBean.getDob() != null) {
			multipartEntity.addPart("date_of_birth", new StringBody(signUpBean.getDob()));
			}
			if (signUpBean.getProfilePicUrl() != null) {
				String pathOfImagesToUpload = signUpBean.getProfilePicUrl();
				//https://graph.facebook.com/816125705102482/picture?type=large
				if (pathOfImagesToUpload.contains(".png") || pathOfImagesToUpload.contains(".PNG")) {
					ContentBody image = new FileBody(new File(pathOfImagesToUpload), "image/png");
					multipartEntity.addPart("profile_image", image);
				} else if (pathOfImagesToUpload.contains(".jpg") || pathOfImagesToUpload.contains(".JPG")) {
					ContentBody image = new FileBody(new File(pathOfImagesToUpload), "image/jpg");
					multipartEntity.addPart("profile_image", image);
				} else if (pathOfImagesToUpload.contains("https://")) {
					multipartEntity.addPart("fb_profile_image", new StringBody(pathOfImagesToUpload));
				}
			}
			/*else{
               ContentBody image = new FileBody( new File(pathOfImagesToUpload), "image/jpg");
               multipartEntity.addPart("profile_image", image);
           }*/
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.setEntity(multipartEntity);

		try {
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				responseString = EntityUtils.toString(resEntity);
				Log.e("Response String", responseString + " ------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseString;
	}

	public String hitProfilePicUploaderService(String picUrl, Context context) {
		String responseString = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(AppNetworkConstants.UPDATE_PROFILE_PIC);
		MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			multipartEntity.addPart("user_security", new StringBody(context.getString(R.string.user_security_key)));
			multipartEntity.addPart("user_device_token", new StringBody(context.getString(R.string.user_device_token)));
			multipartEntity.addPart("user_id", new StringBody(AppSharedPreference.getInstance(context).getUserID()));
			String pathOfImagesToUpload = picUrl;

			//https://graph.facebook.com/816125705102482/picture?type=large
			if (pathOfImagesToUpload.contains(".png") || pathOfImagesToUpload.contains(".PNG")) {
				ContentBody image = new FileBody(new File(pathOfImagesToUpload), "image/png");
				multipartEntity.addPart("profile_image", image);
			} else if (pathOfImagesToUpload.contains(".jpg") || pathOfImagesToUpload.contains(".JPG")) {
				ContentBody image = new FileBody(new File(pathOfImagesToUpload), "image/jpg");
				multipartEntity.addPart("profile_image", image);
			} else if (pathOfImagesToUpload.contains("https://")) {
				multipartEntity.addPart("fb_profile_image", new StringBody(pathOfImagesToUpload));
			}
			/*else{
                ContentBody image = new FileBody( new File(pathOfImagesToUpload), "image/jpg");
                multipartEntity.addPart("profile_image", image);
            }*/
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.setEntity(multipartEntity);

		try {
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				responseString = EntityUtils.toString(resEntity);
				Log.e("Response String", responseString + " ------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseString;
	}

	public BaseResponseBean numberBuy(String url, String number,String countryCode, String userId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("msisdn",number));
        params.add(new BasicNameValuePair("country",countryCode));
		params.add(new BasicNameValuePair("user_id", userId));
		String response = hitNetwork(params, url);

		return parserClass.ParseBuyNumber(response);
	}

	public NumberResponseBean networkHitSearchNumber(List<NameValuePair> numberSearchPairs,String url) {
		String resdata = hitNetwork(numberSearchPairs,url);
		return parserClass.ParseNumber(resdata);
	}


	public BaseResponseBean networkHitCall(String url) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String resdata = hitNetwork(params, url);
		return parserClass.ParseCall(resdata);
	}

	public JSONObject uploadChatMediaFile(String url, MultipartEntity reqData) {
		JSONObject responseJsonObject = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			post.setEntity(reqData);
			HttpResponse response = client.execute(post);
			int responseCode = response.getStatusLine().getStatusCode();
			switch (responseCode) {
			case HttpURLConnection.HTTP_OK:
				InputStream inputStream = response.getEntity().getContent();
				String str = convertStreamToString(inputStream);
				responseJsonObject = new JSONObject(str);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
		return responseJsonObject;
	}

	public String registerNumberOnAteriskServer(String number) {

		
		String responseEntity = null;

		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 0);
		HttpConnectionParams.setSoTimeout(client.getParams(), 0);
		HttpResponse response;
		try {
			HttpPost post = new HttpPost(AppNetworkConstants.REGISTER_ON_ASTERISK);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("id", number));
			nameValuePairs.add(new BasicNameValuePair("password", number));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = client.execute(post);
			/* Checking response */
			if (response != null) {
				InputStream inputStream = response.getEntity().getContent();
				String response_string = convertStreamToString(inputStream);
				return response_string;
			} else {
				return null;
			}

		} catch (UnsupportedEncodingException e) {
			responseEntity = "UnsupportedEncodingException";
			e.printStackTrace();
		} catch (ClientProtocolException e1) {
			responseEntity = "ClientProtocolException";
			e1.printStackTrace();
		} catch (IOException e) {
			responseEntity = "IOException";
			e.printStackTrace();
		}

		return responseEntity;
	}

	public  String hitCreateGroupService(){
		return null;
	}

	public String hitCreateGroupService(Context mContext, String groupId,String groupName, String groupIcon, List<String> contactJabberID) {
		String responseString = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(AppNetworkConstants.CREATE_GROUP);
		MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			multipartEntity.addPart("user_security", new StringBody(mContext.getString(R.string.user_security_key)));
			multipartEntity.addPart("user_device_token", new StringBody(AppSharedPreference.getInstance(mContext).getDeivceToken()));
			multipartEntity.addPart("owner_id", new StringBody(AppSharedPreference.getInstance(mContext).getUserCountryCode()+AppSharedPreference.getInstance(mContext).getUserPhoneNumber()));
			multipartEntity.addPart("group_jid", new StringBody((groupId)));
			multipartEntity.addPart("group_name", new StringBody(groupName));
			for (String member : contactJabberID) {
				multipartEntity.addPart("member_list[]", new StringBody(member));
			}
			String pathOfImagesToUpload = groupIcon;
			if (pathOfImagesToUpload.contains(".png") || pathOfImagesToUpload.contains(".PNG")) {
				ContentBody image = new FileBody(new File(pathOfImagesToUpload), "image/png");
				multipartEntity.addPart("group_image", image);
			} else if (pathOfImagesToUpload.contains(".jpg") || pathOfImagesToUpload.contains(".JPG")) {
				ContentBody image = new FileBody(new File(pathOfImagesToUpload), "image/jpg");
				multipartEntity.addPart("group_image", image);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.setEntity(multipartEntity);

		try {
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				responseString = EntityUtils.toString(resEntity);
				Log.e("Response String", responseString + " ------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseString;

	}

	public String hitGroupImageUploaderService(String picUrl,String groupJID,Context context) {
		String responseString = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(AppNetworkConstants.UPDATE_GROUP_PIC);
		MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			multipartEntity.addPart("user_security", new StringBody(context.getString(R.string.user_security_key)));
			multipartEntity.addPart("user_device_token", new StringBody(context.getString(R.string.user_device_token)));
			multipartEntity.addPart("owner_id", new StringBody(AppSharedPreference.getInstance(context).getUserCountryCode()+AppSharedPreference.getInstance(context).getUserPhoneNumber()));
			multipartEntity.addPart("group_jid", new StringBody(groupJID));
			
			String pathOfImagesToUpload = picUrl;

			//https://graph.facebook.com/816125705102482/picture?type=large
			if (pathOfImagesToUpload.contains(".png") || pathOfImagesToUpload.contains(".PNG")) {
				ContentBody image = new FileBody(new File(pathOfImagesToUpload), "image/png");
				multipartEntity.addPart("group_image", image);
			} else if (pathOfImagesToUpload.contains(".jpg") || pathOfImagesToUpload.contains(".JPG")) {
				ContentBody image = new FileBody(new File(pathOfImagesToUpload), "image/jpg");
				multipartEntity.addPart("group_image", image);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.setEntity(multipartEntity);

		try {
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				responseString = EntityUtils.toString(resEntity);
				Log.e("Response String", responseString + " ------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseString;
	}

}
