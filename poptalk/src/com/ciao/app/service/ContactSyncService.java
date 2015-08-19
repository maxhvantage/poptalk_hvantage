package com.ciao.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.broadcastreceiver.ContactSyncBroadCastReceiver;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.constants.XmppConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.datamodel.EmailBean;
import com.ciao.app.datamodel.PhoneNumberBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.utils.AppUtils;

/**
 * Created by rajat on 11/2/15.
 * This class is used sync the contact db of app with phone book database
 */
public class ContactSyncService extends Service {
	public static int STATUS = 0;
	public static final String KEY_STATUS = "status";
	private static final String TAG = ContactSyncService.class.getName();
	private List<String> phoneNumbersList,ciaoNumbersList;
	private HashMap<String, List<String>> phoneToCiaoNumberMap;
	private boolean isValidNumber;



	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/*if((!AppSharedPreference.getInstance(this).getAppContactSynced())&&(!AppSharedPreference.getInstance(this).getAppContactSyncing())){
            getPhoneBookContacts();
        }*/

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, false, new PhoneBookObserver(this));
		if((!AppSharedPreference.getInstance(this).getAppContactSynced())&&(!AppSharedPreference.getInstance(this).getAppContactSyncing())){
			getPhoneBookContacts();
		}
		return Service.START_STICKY;
	}

	private class ContactLoader extends AsyncTask<Void, Void, Void> {
		private ContentResolver contentResolver;

		@Override
		protected void onPreExecute() {
			STATUS = 0;
			contentResolver = getContentResolver();
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ContactSyncBroadCastReceiver.PROCESS_RESPONSE);
			broadcastIntent.putExtra(KEY_STATUS, STATUS);
			sendBroadcast(broadcastIntent);
			AppSharedPreference.getInstance(ContactSyncService.this).setAppContactSynced(false);
			AppSharedPreference.getInstance(ContactSyncService.this).setAppContactSyncing(true);

			//CallActivity.showProgressDialog();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ApplicationDAO.getInstance(getApplicationContext()).clearDataBase();
			CiaoContactBean ciaoContactBean;
			phoneNumbersList = new ArrayList<String>();

			Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						//Query phone here.
						ciaoContactBean = new CiaoContactBean();
						ciaoContactBean.setId(id);
						ciaoContactBean.setName(name.trim());
						ciaoContactBean.setIsCiaoUser("N");
						ciaoContactBean.setIsFavContact("N");
						Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
						if (pCur.getCount() > 0) {
							List<PhoneNumberBean> numberBeanList = new ArrayList<PhoneNumberBean>();
							while (pCur.moveToNext()) {
								// This would allow you get several phone numbers
								String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								if ((!phone.matches(".*[a-zA-Z]+.*"))&&phone.length() >= 10) {
									isValidNumber = true;
									phone = AppUtils.formatPhoneNumber(ContactSyncService.this,phone);
									String phoneType = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));
									String image = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
									ciaoContactBean.setImagePath(image);
									PhoneNumberBean phoneNumberBean = new PhoneNumberBean();
									phoneNumberBean.setId(id);
									phoneNumberBean.setPhoneNumber(phone);
									phoneNumberBean.setType(phoneType);
									phoneNumberBean.setIsCiaoUser("N");
									numberBeanList.add(phoneNumberBean);
									phoneNumbersList.add(phone);
								}else {
									isValidNumber = false;
								}

							}

							ciaoContactBean.setUserPhoneList(numberBeanList);
						}
						pCur.close();
						if(isValidNumber){
							//Save contact in ciao app db
							ApplicationDAO.getInstance(getApplicationContext()).createCiaoContact(ciaoContactBean);
						}

					}

				}
			}
			cursor.close();
			new CheckCiaoUser().execute();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);



		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	//This method  is use to read the phone database
	public void getPhoneBookContacts() {
		new ContactLoader().execute();
	}

	//Monitor phone book database and  get notify if any modification in phone contacts database
	class PhoneBookObserver extends ContentObserver {
		public PhoneBookObserver(Context context) {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if(!AppSharedPreference.getInstance(ContactSyncService.this).getAppContactSyncing()){
				getPhoneBookContacts();
			}

		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			super.onChange(selfChange, uri);
			if(!selfChange){
				if(!AppSharedPreference.getInstance(ContactSyncService.this).getAppContactSyncing()){
					getPhoneBookContacts();
				}
			}


		}

		/* @Override
        public boolean deliverSelfNotifications() {
        	// TODO Auto-generated method stub
        	return true;
        }*/

	}


	// Check how many contacts in our phone is having Ciao app

	class CheckCiaoUser extends AsyncTask<Void,Void,Void> {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (int i=0;i<phoneNumbersList.size();i++){
				jsonArray.put(phoneNumbersList.get(i));
			}
			try {
				jsonObject.put("user_security",getApplicationContext().getString(R.string.user_security_key));
				jsonObject.put("user_device_token",AppSharedPreference.getInstance(getApplicationContext()).getDeivceToken());
				jsonObject.put("userId",AppSharedPreference.getInstance(ContactSyncService.this).getUserID());
				jsonObject.put("phoneSet",jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String response = NetworkCall.getInstance(ContactSyncService.this).hitNetwork(AppNetworkConstants.CHECK_CIAO_CONTACT_URL,jsonObject);
			try {
				JSONObject responseObject = new JSONObject(response);
				String errorCode = responseObject.getString("error_code");
				if (errorCode.equalsIgnoreCase("0")) {
					phoneNumbersList.clear();
					phoneToCiaoNumberMap = new HashMap<String, List<String>>();
					JSONArray ciaoUserJson = responseObject.getJSONArray("result");
					for(int i = 0;i<ciaoUserJson.length();i++){
						JSONObject caioContact = ciaoUserJson.getJSONObject(i);
						ciaoNumbersList = new ArrayList<String>();
						String phoneNumber = caioContact.getString("phone");
						JSONArray ciaoNumberArray = caioContact.getJSONArray("ciao_number");
						String commUser = caioContact.getString("comm_user");
						for(int j=0;j<ciaoNumberArray.length();j++){
							String ciaoPhoneNumber = ciaoNumberArray.getString(j);
							ciaoNumbersList.add(ciaoPhoneNumber);
						}
						phoneNumbersList.add(phoneNumber);
						phoneToCiaoNumberMap.put(phoneNumber+":"+commUser, ciaoNumbersList);
					}
					if(phoneNumbersList.size()>0){
						ApplicationDAO.getInstance(ContactSyncService.this).updateCioaAppUsers(phoneToCiaoNumberMap);
					}
				}
				STATUS = 1;
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction(ContactSyncBroadCastReceiver.PROCESS_RESPONSE);
				broadcastIntent.putExtra(KEY_STATUS, STATUS);
				sendBroadcast(broadcastIntent);
				//CallActivity.hideProgressDialog();
				AppSharedPreference.getInstance(ContactSyncService.this).setAppContactSynced(true);
				AppSharedPreference.getInstance(ContactSyncService.this).setAppContactSyncing(false);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
	}


}