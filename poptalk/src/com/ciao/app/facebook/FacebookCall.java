package com.ciao.app.facebook;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.datamodel.SignUpBean;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.RegistrationActivity;
import com.ciao.app.views.activities.ShareToEarnCreditActivity;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

/**
 * Created by rajat on 4/2/15.
 */
public class FacebookCall {
	private  final List<String> PUBLISH_PERMISSIONS = Arrays.asList("public_profile", "email");
	private  final List<String> POST_PERMISSTIONS = Arrays.asList("publish_stream", "email", "public_profile", "user_birthday");
	private static FacebookCall instance = null;
	private Activity mActivity;
	private Activity mContextAct;
	private final List<String> READ_PERMISSIONS = Arrays.asList("email","user_birthday","user_location");
	private Session facebookSession;
	private String message;
	private Fragment fragment;
	//GraphObject{graphObjectClass=GraphUser, state={"id":"816125705102482","first_name":"Rajat Kumar","timezone":5.5,"verified":true,"name":"Rajat Kumar Singh","locale":"en_US","link":"https:\/\/www.facebook.com\/app_scoped_user_id\/816125705102482\/","last_name":"Singh","gender":"male","updated_time":"2014-07-20T12:22:30+0000"}}
	// {"id":"816125705102482","first_name":"Rajat Kumar","birthday":"03\/20\/1991","timezone":5.5,"location":{"id":"130646063637019","name":"Noida, India"},"email":"singh0rajatkumar@gmail.com","verified":true,"name":"Rajat Kumar Singh","locale":"en_US","link":"https:\/\/www.facebook.com\/app_scoped_user_id\/816125705102482\/","last_name":"Singh","gender":"male","updated_time":"2014-07-20T12:22:30+0000"}
	private FacebookCall(FragmentActivity context)
	{
		mActivity=context;
	}
	/**
	 * Constructor
	 * @param Activity activity
	 */
	private FacebookCall(Activity activity, Fragment fragment)
	{
		this.mContextAct = activity;
		this.fragment = fragment;

	}
	private FacebookCall(Activity context)
	{
		mContextAct=context;
	}

	//	private FacebookLoginService(Fragment cm){
	//		this.mContext=null;
	//		this.mContext=cm;
	//	}
	public static FacebookCall getInstance(FragmentActivity activity) {
		if (instance == null )
		{
			instance = new FacebookCall(activity);

		}
		return instance;
	}

	public static FacebookCall getInstance(Activity activity) {
		if (instance == null )
		{
			instance = new FacebookCall(activity);

		}
		return instance;
	}

	public static FacebookCall getInstance(Activity activity,Fragment fragment) {
		if (instance == null )
		{
			instance = new FacebookCall(activity,fragment);
		}
		return instance;
	}

	public void setContext(Activity mConFragment){
		mContextAct=mConFragment;
	}

	public void setMessage(final String message){
		this.message=message;
	}

	public void facebookLogin(final Activity context){
		mActivity=context;
		Session.openActiveSession(context, true, new Session.StatusCallback() {
			@SuppressWarnings("deprecation")
			@Override
			public void call(Session session, SessionState state, Exception exception)
			{
				facebookSession = session;
				if(state.toString().equalsIgnoreCase("CLOSED_LOGIN_FAILED")){
					AppConstants.FB_LOGIN = false;
				}
				if(exception!=null && exception.getMessage().equalsIgnoreCase("testOperation canceled"))
				{
					AppConstants.FB_LOGIN = false;
				}
				else if (session.isOpened())
				{
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,Response response) {

							if (mActivity instanceof  RegistrationActivity)
							{
								if (requestPermissions(1)) {
									if (user != null) {
										Log.e("Graph user = ",user.toString());
										try {
											URL imgUrl = new URL("https://graph.facebook.com/" + user.getId() + "/picture?type=large");
											JSONObject json = user.getInnerJSONObject();
											Log.e("user data", "" + json);
											AppConstants.FB_LOGIN = true;
											SignUpBean signUpBean = new SignUpBean();
											signUpBean.setUserFirstName(json.getString("first_name")+" "+json.getString("last_name"));
											signUpBean.setEmail(json.getString("email"));
											signUpBean.setGender(json.getString("gender"));
											//           signUpBean.setDob(json.getString("birthday"));
											signUpBean.setProfilePicUrl(imgUrl.toString());
											((RegistrationActivity)mActivity).autoFillWithFacebook(signUpBean);

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}

							}
							else if(mActivity instanceof ShareToEarnCreditActivity)
							{
								if(requestPermissions(1)){
									postOnWall(mActivity);
								}
							}
						}
					});
				}
			}
		});
	}


    public boolean requestPermissions(int isShare)
    {
        List<String> permissions = facebookSession.getPermissions();

        if(isShare == 0)
        {
            if (!isSubsetOf(READ_PERMISSIONS, permissions))
            {
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                        mActivity, READ_PERMISSIONS).setRequestCode(11);
                facebookSession.requestNewReadPermissions(newPermissionsRequest);
                return false;
            }
            return true;
        }
        else if(isShare == 1)
        {
            if (!isSubsetOf(PUBLISH_PERMISSIONS, permissions))
            {
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                        mActivity, PUBLISH_PERMISSIONS).setRequestCode(11);
                facebookSession.requestNewPublishPermissions(newPermissionsRequest);
                return false;
            }

            return true;
        }
        else if(isShare == 2)
        {
            if (!isSubsetOf(POST_PERMISSTIONS, permissions))
            {
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                        mActivity, POST_PERMISSTIONS).setRequestCode(11);
                facebookSession.requestNewPublishPermissions(newPermissionsRequest);
                return false;
            }
            return true;
        }

        return true;
    }

	/*
	 * this method is use to grant permission for getting facebook data
	 * @param subset
	 * @param superset
	 * return boolean
	 */

	private boolean isSubsetOf(Collection<String> subset,Collection<String> superset)
	{
		for (String string : subset)
		{
			if(!superset.contains(string))
			{
				return false;
			}
		}
		return true;
	}

	public void fbLogout()
	{
		Session session = Session.getActiveSession();
		if (session != null )
			if(!session.isClosed())
			{
				session.closeAndClearTokenInformation();
			}
		instance=null;
	}

	public void postOnWall(Activity activity)
	{
		try
		{
			Session session = Session.getActiveSession();
			if (session != null && session.isOpened())
			{
				Bundle params = new Bundle();
				params.putString("name", "Join Poptalk Today and Save");
				params.putString("caption", "Free International Calls and SMS");
				params.putString("description", "Reduce your phone bill by renting our your lockscreen. Claim your 200 free credits today! ");
				//params.putString("picture","http://res.cloudinary.com/hrscywv4p/image/upload/c_limit,f_auto,h_3000,q_90,w_1200/v1/213578/unnamed_2_m1a1ne.png");
				params.putString("link", "http://joinpoptalk.com");

				WebDialog webDialog =(new WebDialog.FeedDialogBuilder(mActivity, session, params)).setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values, FacebookException error) {

						if(error ==null){
							Toast.makeText(mActivity, "posted successfully",Toast.LENGTH_LONG).show();
							AppUtils.socialShare(mActivity, AppSharedPreference.getInstance(mActivity).getUserID(), "facebook");
							
						}else if(error instanceof FacebookOperationCanceledException)
						{
							Toast.makeText(mActivity, "posted canceled",Toast.LENGTH_LONG).show();
						}else{

							Toast.makeText(mActivity, "error in posting status",Toast.LENGTH_LONG).show();

						}

					}
				}).setFrom(" ").build();

				webDialog.show();

			}else {
				facebookLogin(activity);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
