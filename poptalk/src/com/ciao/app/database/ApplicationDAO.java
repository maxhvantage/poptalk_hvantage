package com.ciao.app.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.constants.XmppConstants;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.datamodel.MediaBean;
import com.ciao.app.datamodel.MemberBean;
import com.ciao.app.datamodel.PhoneNumberBean;
import com.ciao.app.utils.AppUtils;

/**
 * Created by rajat on 11/2/15.
 */
/**
 * @author rajat
 *
 */
public class ApplicationDAO {

	private static ApplicationDAO applicationDAO;
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;
	private Context context;

	public ApplicationDAO(Context context) {
		databaseHelper = new DatabaseHelper(context);
		database = databaseHelper.getWritableDatabase();
		this.context = context;
	}

	public static ApplicationDAO getInstance(Context context) {
		if (applicationDAO == null) {
			applicationDAO = new ApplicationDAO(context);
		}
		return applicationDAO;
	}

	// close database
	public void closeDb() {
		database.close();

	}
	/*
	 * This method is used to save contact in our app using the phone default contact db
	 * 
	 */
	public synchronized void createCiaoContact(CiaoContactBean ciaoContactBean) {

		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(AppDatabaseConstants.KEY_ID,
					ciaoContactBean.getId());
			contentValues.put(AppDatabaseConstants.KEY_NAME,
					ciaoContactBean.getName());
			contentValues.put(AppDatabaseConstants.KEY_CONTACT_PIC,
					ciaoContactBean.getImagePath());
			contentValues.put(AppDatabaseConstants.KEY_CIAO_USER,
					ciaoContactBean.getIsCiaoUser());
			contentValues.put(AppDatabaseConstants.KEY_FAVORITE_CONTACT,
					ciaoContactBean.getIsFavContact());
			contentValues.put(AppDatabaseConstants.KEY_CIAO_NUMBER, "");
			contentValues.put(AppDatabaseConstants.KEY_COMM_USER, ciaoContactBean.getCommUser());
			context.getContentResolver()
					.insert(Uri
									.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),
							contentValues);

			List<PhoneNumberBean> phoneNumberList = ciaoContactBean
					.getUserPhoneList();
			for (PhoneNumberBean phoneNumberBean : phoneNumberList) {
				ContentValues phoneValues = new ContentValues();
				phoneValues.put(AppDatabaseConstants.KEY_ID,
						ciaoContactBean.getId());
				phoneValues.put(AppDatabaseConstants.KEY_PHONE_NUMBER,
						phoneNumberBean.getPhoneNumber());
				phoneValues.put(AppDatabaseConstants.KEY_PHONE_TYPE,
						phoneNumberBean.getType());
				phoneValues.put(AppDatabaseConstants.KEY_CIAO_USER,
						phoneNumberBean.getIsCiaoUser());
				phoneValues.put(AppDatabaseConstants.KEY_COMM_USER, ciaoContactBean.getCommUser());
				context.getContentResolver()
						.insert(Uri
										.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING),
								phoneValues);

			}

			/*List<EmailBean> emailList = ciaoContactBean.getUserEmailBeanList();
			for (EmailBean emailBean : emailList) {
				ContentValues emailValues = new ContentValues();
				emailValues.put(AppDatabaseConstants.KEY_ID, emailBean.getId());
				emailValues.put(AppDatabaseConstants.KEY_EMAIL,
						emailBean.getEmail());
				emailValues.put(AppDatabaseConstants.KEY_EMAIL_TYPE,
						emailBean.getType());
				context.getContentResolver()
				.insert(Uri
						.parse(AppDatabaseConstants.EMAIL_CONTENT_URI_STRING),
						emailValues);
			}*/

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}
	/*
     * This method is used to clear the contacts,email and phoen table
     */
	public void clearDataBase() {
		database.delete(AppDatabaseConstants.TABLE_CONTACTS, null, null);
		database.delete(AppDatabaseConstants.TABLE_PHONE, null, null);
		database.delete(AppDatabaseConstants.TABLE_EMAIL, null, null);
	}

	/*
	 * This method is used to get all favorite contacts of user from app local db.
	 */
	public List<CiaoContactBean> getFavoriteContacts() {
		List<CiaoContactBean> ciaoContactBeans = new ArrayList<CiaoContactBean>();
		Cursor fCursor = context.getContentResolver().query(
				Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),
				null, AppDatabaseConstants.KEY_FAVORITE_CONTACT + " = ?",
				new String[] { "Y" }, null);
		if (fCursor.getCount() > 0) {
			CiaoContactBean ciaoContactBean;
			while (fCursor.moveToNext()) {
				ciaoContactBean = new CiaoContactBean();
				String id = fCursor.getString(fCursor
						.getColumnIndex(AppDatabaseConstants.KEY_ID));
				String name = fCursor.getString(fCursor
						.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				String pic = fCursor.getString(fCursor
						.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
				ciaoContactBean.setId(id);
				ciaoContactBean.setName(name);
				ciaoContactBean.setImagePath(pic);
				ciaoContactBeans.add(ciaoContactBean);
			}
		}
		return ciaoContactBeans;
	}

	public synchronized void createChatUser(Context context, String name,String userId, String availability, String phoneNumber) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.NAME, name);
		contentValues.put(AppDatabaseConstants.USER_ID, userId);
		contentValues.put(AppDatabaseConstants.STATUS, availability);
		contentValues.put(AppDatabaseConstants.KEY_PHONE_NUMBER, phoneNumber);
		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),contentValues);
	}


	/*
        * This method is used to store the information,when user initiate chat with a friend for first time.
        * @param - to - id of the user to whomsoever sending or from whomsoever receiving the message
        * @param - time - time stamp of message
        * @param - messageText - body of the message
        * @param -isReceived - true if received from from anyone ,false if sent to anyone
        */
	private void createNewChatUser(String to, long time, String messageText,boolean isReceived, String phoneNumber) {
		String appUserId =AppSharedPreference.getInstance(context).getUserID();
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserId);
		contentValues.put(AppDatabaseConstants.USER_ID, to);
		contentValues.put(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED, time);
		contentValues.put(AppDatabaseConstants.COLUMN_LAST_MESSAGE, messageText);
		contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE, 0);
		contentValues.put(AppDatabaseConstants.KEY_PHONE_NUMBER, phoneNumber);
		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),contentValues);
		context.getContentResolver().notifyChange(uri, null);
		if (!AppSharedPreference.getInstance(context).getChatSceenVisbility()&& isReceived) {
			getUserDataAndShowNotification(getUserNameFromCommUser(to, phoneNumber), messageText);
		}
		if(AppSharedPreference.getInstance(context).getChatSceenVisbility()){
			AppUtils.playChatBubbleSound(context);
		}


	}
	/*
	 * This method is used to update the last message in chat log table
	 */
	public void updateLastMessage(String to, long time, String messageText,int unreadMessage, boolean isReceived,boolean isGroupChat) {
		ContentValues contentValues = new ContentValues();
		String appUserId = AppSharedPreference.getInstance(context).getUserID();
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserId);
		contentValues.put(AppDatabaseConstants.USER_ID, to);
		contentValues.put(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED, time);
		contentValues.put(AppDatabaseConstants.COLUMN_LAST_MESSAGE, messageText);
		if (!AppSharedPreference.getInstance(context).getChatSceenVisbility()&& isReceived) {
			contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE,unreadMessage + 1);
			context.getContentResolver()
					.update(Uri
									.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),
							contentValues,
							AppDatabaseConstants.USER_ID + " = ? AND "+AppDatabaseConstants.KEY_APP_USER_ID+" = ? ",
							new String[] { to,appUserId });

		} else {
			contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE,0);
			context.getContentResolver().update(
					Uri	.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),
					contentValues,
					AppDatabaseConstants.USER_ID + " = ? AND "+AppDatabaseConstants.KEY_APP_USER_ID+" = ? ",
					new String[] { to,appUserId });
		}
		if(AppSharedPreference.getInstance(context).getChatSceenVisbility()){
			AppUtils.playChatBubbleSound(context);
		}if (!AppSharedPreference.getInstance(context).getChatSceenVisbility()&& isReceived) {
			if(isGroupChat){
				getGroupDataAndShowNotification(to, messageText);
			}else{
				getUserDataAndShowNotification(to, messageText);
			}

		}

	}

	/*
	 * This method is used to show notification about new received message via chat module.
	 */
	private void getGroupDataAndShowNotification(final String to, final String message) {
		final String[] array = to.split("@");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Cursor pCursor = context
						.getContentResolver()
						.query(Uri
										.parse(AppDatabaseConstants.GROUP_CHAT_CONTENT_URI_STRING),
								null,
								AppDatabaseConstants.COLUMN_GROUP_JID + " = ?",
								new String[] { array[0] }, null);
				if (pCursor != null) {
					if (pCursor.getCount() > 0) {
						pCursor.moveToNext();
						String id = pCursor.getString(pCursor.getColumnIndex(AppDatabaseConstants.COLUMN_GROUP_JID));
						String name = pCursor.getString(pCursor.getColumnIndex(AppDatabaseConstants.COLUMN_GROUP_NAME));
						String pic = pCursor.getString(pCursor.getColumnIndex(AppDatabaseConstants.COLUMN_GROUP_ICON));
						String messagebody;
						if(message.contains(".jpg")||message.contains(".png")){
							messagebody = "Image";
						}else{
							messagebody = message;
						}
						AppUtils.showNotification(context, to, messagebody,name, pic,true);

					}
				}
			}
		});
		thread.start();

	}

	/*
	 * This method is used to show notification about new received message via chat module.
	 */
	private void getUserDataAndShowNotification(final String to, final String message) {
		final String[] array = to.split("@");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Cursor pCursor = context
						.getContentResolver()
						.query(Uri
										.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING),
								null,
								AppDatabaseConstants.KEY_COMM_USER + " = ?",
								new String[] { array[0] }, null);
				if (pCursor != null) {
					if (pCursor.getCount() > 0) {
						pCursor.moveToNext();
						String id = pCursor.getString(pCursor
								.getColumnIndex(AppDatabaseConstants.KEY_ID));
						Cursor contactCursor1 = context
								.getContentResolver()
								.query(Uri
												.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),
										null,
										AppDatabaseConstants.KEY_ID + " = ?",
										new String[] { id }, null);
						if (contactCursor1 != null) {
							if (contactCursor1.getCount() > 0) {
								contactCursor1.moveToNext();
								String name = contactCursor1.getString(contactCursor1
										.getColumnIndex(AppDatabaseConstants.KEY_NAME));
								String pic = contactCursor1.getString(contactCursor1
										.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
								String messagebody;
								if(message.contains(".jpg")||message.contains(".png")){
									messagebody = "Image";
								}else{
									messagebody = message;
								}
								AppUtils.showNotification(context, to, messagebody,name, pic,false);
							}
						}

					}else{
						String messagebody;
						if(message.contains(".jpg")||message.contains(".png")){
							messagebody = "Image";
						}else{
							messagebody = message;
						}
						AppUtils.showNotification(context, to, messagebody,array[0], "",false);
					}
				}
			}
		});
		thread.start();

	}

	/*
	 * This method is used to update detail of friends in background to show details in Chat log tab
	 */
	private void updateUserDetails(final String to, final String message) {
		final String[] array = to.split("@");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Cursor pCursor = context
						.getContentResolver()
						.query(Uri
										.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING),
								null,
								AppDatabaseConstants.KEY_PHONE_NUMBER + " = ?",
								new String[] { array[0] }, null);
				if (pCursor != null) {
					if (pCursor.getCount() > 0) {
						pCursor.moveToNext();
						String id = pCursor.getString(pCursor
								.getColumnIndex(AppDatabaseConstants.KEY_ID));
						Cursor contactCursor1 = context
								.getContentResolver()
								.query(Uri
												.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),
										null,
										AppDatabaseConstants.KEY_ID + " = ?",
										new String[] { id }, null);
						if (contactCursor1 != null) {
							if (contactCursor1.getCount() > 0) {
								contactCursor1.moveToNext();
								String name = contactCursor1.getString(contactCursor1
										.getColumnIndex(AppDatabaseConstants.KEY_NAME));
								String pic = contactCursor1.getString(contactCursor1
										.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
								ContentValues contentValues = new ContentValues();
								// contentValues.put(AppDatabaseConstants.USER_ID,
								// to);
								contentValues.put(AppDatabaseConstants.NAME,
										name);
								contentValues.put(
										AppDatabaseConstants.KEY_CONTACT_PIC,
										pic);
								context.getContentResolver()
										.update(Uri
														.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),
												contentValues,
												AppDatabaseConstants.USER_ID
														+ " = ?",
												new String[] { to });
								if (!AppSharedPreference.getInstance(context)
										.getChatSceenVisbility()) {
									AppUtils.showNotification(context, to,message, name, pic,false);
								}

							}
						}

					}
				}
			}
		});
		thread.start();
	}

	/*
	 * This method is used to get all contacts from our app db.
	 */

	public List<CiaoContactBean> getAllContacts() {
		List<CiaoContactBean> ciaoContactBeans = new ArrayList<CiaoContactBean>();
		Cursor fCursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),
				null, AppDatabaseConstants.KEY_CIAO_USER + " = ?", new String[]{"Y"}, null);
		if (fCursor.getCount() > 0) {
			CiaoContactBean ciaoContactBean;
			while (fCursor.moveToNext()) {
				ciaoContactBean = new CiaoContactBean();
				String id = fCursor.getString(fCursor
						.getColumnIndex(AppDatabaseConstants.KEY_ID));
				String name = fCursor.getString(fCursor
						.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				String commUser = fCursor.getString(fCursor
						.getColumnIndex(AppDatabaseConstants.KEY_COMM_USER));
				ciaoContactBean.setId(id);
				ciaoContactBean.setName(name);
				ciaoContactBean.setCommUser(commUser);
				ciaoContactBeans.add(ciaoContactBean);
			}
		}
		return ciaoContactBeans;
	}

	/*
	 * This method is used to  get contacts id from there contact number
	 */
	public String getUserIdFromNumber(String number) {
		String _id = null;
		Cursor pCursor = context.getContentResolver().query(
				Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null,
				AppDatabaseConstants.KEY_PHONE_NUMBER + " = ?",
				new String[] { number }, null);
		if (pCursor != null) {
			if (pCursor.getCount() > 0) {
				pCursor.moveToNext();
				_id = pCursor.getString(pCursor
						.getColumnIndex(AppDatabaseConstants.KEY_ID));
			}
		}
		return _id;
	}

	/*
 * This method is used to  get the comm user from there contact number
 */
	public String getCommUserFromNumber(String number) {
		String _id = null;
		Cursor pCursor = context.getContentResolver().query(
				Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null,
				AppDatabaseConstants.KEY_PHONE_NUMBER + " like ? ",
				new String[] { "%"+number }, null);
		if (pCursor != null) {
			if (pCursor.getCount() > 0) {
				pCursor.moveToNext();
				_id = pCursor.getString(pCursor
						.getColumnIndex(AppDatabaseConstants.KEY_COMM_USER));
			}
		}
		return _id;
	}

	/*
 * This method is used to  get the comm user from there contact number
 */
	public String getCommUserFromUserId(String userId) {
		String _id = null;
		Cursor pCursor = context.getContentResolver().query(
				Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null,
				AppDatabaseConstants.KEY_ID + " = ? ",
				new String[] { userId }, null);
		if (pCursor != null) {
			if (pCursor.getCount() > 0) {
				pCursor.moveToNext();
				_id = pCursor.getString(pCursor
						.getColumnIndex(AppDatabaseConstants.KEY_COMM_USER));
			}
		}
		return _id;
	}

	/*
	 * This method is used to  get contacts id from there contact number
	 */
	public List<String> getUserIdsFromNumber(String number) {
		List<String> _ids = new ArrayList<String>();
		Cursor pCursor = context.getContentResolver().query(
				Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null,
				AppDatabaseConstants.KEY_PHONE_NUMBER + " = ?",
				new String[] { number }, null);
		if (pCursor != null) {
			if (pCursor.getCount() > 0) {
				while(pCursor.moveToNext()){
					String _id = pCursor.getString(pCursor.getColumnIndex(AppDatabaseConstants.KEY_ID));
					_ids.add(_id);
				}

			}
		}
		return _ids;
	}



	/*
	 * This method is used to update the list of ciao user from our contact db
	 */
	public synchronized void updateCioaAppUsers(final HashMap<String, List<String>> phoneToCioaNumberMap) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				Iterator<Entry<String, List<String>>> it = phoneToCioaNumberMap.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry pair = (Map.Entry)it.next();
					String phoneNumberCommUser = (String) pair.getKey();
					String[] splitPhoneNumber = phoneNumberCommUser.split(":");
					String phoneNumber = splitPhoneNumber[0];
					String commUser = splitPhoneNumber[1];
					List<String> ciaoNumberList = (List<String>)pair.getValue();
					Log.e("Phone Number = ", phoneNumber);
					List<String> _ids = getUserIdsFromNumber(phoneNumber);
					for(int i=0;i<_ids.size();i++){
						ContentValues contentValues = new ContentValues();
						contentValues.put(AppDatabaseConstants.KEY_CIAO_USER, "Y");
						int rowUpdated = context.getContentResolver().update(Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),contentValues,AppDatabaseConstants.KEY_ID + " = ?",new String[] { _ids.get(i) });
						if(rowUpdated>0){
							updateCiaoRegisteredNumber(phoneNumber, commUser);
							for(String ciaoNumber :ciaoNumberList){
								Log.e("Ciao Number = ", ciaoNumber);
								ContentValues phoneValues = new ContentValues();
								phoneValues.put(AppDatabaseConstants.KEY_ID,_ids.get(i));
								phoneValues.put(AppDatabaseConstants.KEY_PHONE_NUMBER,ciaoNumber);
								phoneValues.put(AppDatabaseConstants.KEY_PHONE_TYPE,"C");
								phoneValues.put(AppDatabaseConstants.KEY_CIAO_USER,"Y");
								phoneValues.put(AppDatabaseConstants.KEY_CIAO_REGISTERED, "N");
								phoneValues.put(AppDatabaseConstants.KEY_COMM_USER, commUser);

								Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING),phoneValues);
							}
						}
					}


				}
				if (AppSharedPreference.getInstance(context).getUserID() != "-1") {
					addUserContactsToOpenfireServer(XMPPChatService.getConnection());
				}


				/*for (int i = 0; i < phoneList.size(); i++) {
					String phoneNumber = phoneList.get(i);
					String _id = getUserIdFromNumber(phoneNumber);
					ContentValues contentValues = new ContentValues();
					contentValues.put(AppDatabaseConstants.KEY_CIAO_USER, "Y");
					contentValues.put(AppDatabaseConstants.KEY_CIAO_NUMBER, ciaoNumberList.get(i));
					context.getContentResolver().update(Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),contentValues,AppDatabaseConstants.KEY_ID + " = ?",new String[] { _id });
					 if(rowUpdate>0){
				    	ContentValues phoneValues = new ContentValues();
						phoneValues.put(AppDatabaseConstants.KEY_ID,_id);
						phoneValues.put(AppDatabaseConstants.KEY_PHONE_NUMBER,ciaoNumberList.get(i));
						phoneValues.put(AppDatabaseConstants.KEY_PHONE_TYPE,"C");
						phoneValues.put(AppDatabaseConstants.KEY_CIAO_USER,"Y");
						context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING),phoneValues);
				    }
				}*/
			}
		});
		thread.start();
	}
	/*
	 * This method is used to update the contacts whether they have Ciao installed or not after Contact sync.
	 */
	protected void updateCiaoRegisteredNumber(String phoneNumber, String commUser) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.KEY_CIAO_USER, "N");
		contentValues.put(AppDatabaseConstants.KEY_CIAO_REGISTERED, "Y");
		contentValues.put(AppDatabaseConstants.KEY_COMM_USER, commUser);
		int rowUpdated = context.getContentResolver().update(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING),contentValues,AppDatabaseConstants.KEY_PHONE_NUMBER + " = ?",new String[] { phoneNumber });
		if(rowUpdated>0){
			Log.e("Registered Ciao number", phoneNumber);
		}
	}

	/*
	 * This method is used to get the user phone from user contact id
	 */
	public String getUserPhoneNumber(String id) {
		Cursor pCursor = context
				.getContentResolver()
				.query(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING),
						null, AppDatabaseConstants.KEY_ID + " = ?",
						new String[] { id }, null);
		String phone = null;
		if (pCursor.getCount() > 0) {
			while (pCursor.moveToNext()) {
				phone = pCursor.getString(pCursor.getColumnIndex(AppDatabaseConstants.KEY_PHONE_NUMBER));
			}

		}
		if(phone==null){
			//Contact is not in our app db
			phone = id;
		}
		return phone;
	}

	/*
	 * This method return the user mobile number which is registered with caio.(Used for SMS verification)
	 */
	public String getUserCiaoRegisteredPhoneNumber(String id) {
		Cursor pCursor = context
				.getContentResolver()
				.query(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING),
						null, AppDatabaseConstants.KEY_ID + " = ? AND "+AppDatabaseConstants.KEY_CIAO_REGISTERED+ " = ? ",
						new String[] { id,"Y"}, null);
		String phone = null;
		if (pCursor.getCount() > 0) {
			while (pCursor.moveToNext()) {
				phone = pCursor.getString(pCursor.getColumnIndex(AppDatabaseConstants.KEY_PHONE_NUMBER));
			}

		}
		if(phone==null){
			//Contact is not in our app db
			phone = id;
		}
		return phone;
	}
	/*
	 * This method is used get list of contact according to search pattern.
	 */
	public synchronized List<CiaoContactBean>  getContactForAutocompleterView(String like,ArrayList<CiaoContactBean> selectedContactList){
		List<CiaoContactBean> matchedContacts = new ArrayList<CiaoContactBean>();
		ArrayList<String> ids = new ArrayList<String>();
		if(selectedContactList.size()>0){
			ids =getAllIdOfACiaoRegiesteredNumber(selectedContactList);
		}

		String query;
		Cursor contactCursor;
		String[] selectionArgs = new String[ids.size()+1];
		like = "%"+like+"%";
		selectionArgs[0] = like;
		if(ids.size()>0){
			String questionMarks;
			StringBuilder sb = new StringBuilder(ids.size() * 2 - 1);
			sb.append("?");
			for (int j = 1; j < ids.size(); j++) {
				sb.append(",?");
			}
			questionMarks = sb.toString();

			for(int i = 1;i<=ids.size();i++){
				selectionArgs[i]= ids.get(i-1);
			}
			query = "SELECT * FROM "+AppDatabaseConstants.TABLE_CONTACTS+" WHERE "
					+AppDatabaseConstants.KEY_NAME+" LIKE ? AND "
					+AppDatabaseConstants.KEY_ID+" NOT IN ("+questionMarks+")";
		}else{
			query = "SELECT * FROM "+AppDatabaseConstants.TABLE_CONTACTS+" WHERE "
					+AppDatabaseConstants.KEY_NAME+" LIKE ? ";
		}
		contactCursor = database.rawQuery(query, selectionArgs);
		if(contactCursor.getCount()>0){
			while(contactCursor.moveToNext()){
				String name = contactCursor.getString(contactCursor.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				String id = contactCursor.getString(contactCursor.getColumnIndex(AppDatabaseConstants.KEY_ID));
				String isCiaoUser = contactCursor.getString(contactCursor.getColumnIndex(AppDatabaseConstants.KEY_CIAO_USER));
				if(isCiaoUser!=null && isCiaoUser.equalsIgnoreCase("Y")){

					String ciaoRegisteredNumber = getUserCiaoRegisteredPhoneNumber(id);
					if(!ciaoRegisteredNumber.equalsIgnoreCase(AppSharedPreference.getInstance(context).getUserCountryCode()+AppSharedPreference.getInstance(context).getUserPhoneNumber())){
						CiaoContactBean contactBean = new CiaoContactBean();
						contactBean.setId(id);
						contactBean.setName(name);
						matchedContacts.add(contactBean);
					}
				}

			}

		}
		return matchedContacts;

	}
	/*
	 * This method is used to get the jabber id of all user of a newly created group.
	 */
	public synchronized List<String> getJabberIdToCreateGroup(final ArrayList<CiaoContactBean> selectedContactList){
		final List<String> jabberIDList = new ArrayList<String>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(CiaoContactBean contactBean:selectedContactList){
					String jid = ApplicationDAO.getInstance(context).getUserCiaoRegisteredPhoneNumber(contactBean.getId());
					if(!jabberIDList.contains(jid)){
						jabberIDList.add(jid);
					}

				}

			}
		}).start();

		return jabberIDList;
	}

	/*
	 * This method is used to save newly created group in our app db
	 */
	public void createGroupAndStoreInDb(String groupName,String groupId,String groupIcon) {
		String appUserId = AppSharedPreference.getInstance(context).getUserID();
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserId);
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_NAME, groupName);
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_JID,groupId);
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_ICON, groupIcon);
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_MEMBER, "Y");
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_CREATION_TIME, System.currentTimeMillis());
		context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.GROUP_CHAT_CONTENT_URI_STRING), contentValues);
	}

	/*
	 * This method is used to save all user from a group in db
	 */
	public void saveUsersOfGroupInDb(final String groupId,final List<String> contactJabberID) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				String appUserID =AppSharedPreference.getInstance(context).getUserID();
				for(int i=0;i<contactJabberID.size();i++){
					ContentValues contentValues = new ContentValues();
					contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserID);
					contentValues.put(AppDatabaseConstants.USER_ID, contactJabberID.get(i));
					contentValues.put(AppDatabaseConstants.COLUMN_GROUP_JID,groupId);
					contentValues.put(AppDatabaseConstants.COLUMN_GROUP_MEMBER, "Y");
					if(contactJabberID.get(i).equalsIgnoreCase(AppSharedPreference.getInstance(context).getUserCountryCode() + AppSharedPreference.getInstance(context).getUserPhoneNumber())){
						contentValues.put(AppDatabaseConstants.COLUMN_GROUP_ADMIN,"Y");
					}else{
						contentValues.put(AppDatabaseConstants.COLUMN_GROUP_ADMIN,"N");
					}
					Uri id =context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.GROUP_DETAIL_CONTENT_URI_STRING), contentValues);
				}

			}
		}).start();

	}

	public void saveUsersOfReceivedGroupInDb(final String groupId,final List<MemberBean> groupMemberList) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i=0;i<groupMemberList.size();i++){
					MemberBean memberBean = groupMemberList.get(i);
					ContentValues contentValues = new ContentValues();
					contentValues.put(AppDatabaseConstants.USER_ID, memberBean.getMemberJID());
					contentValues.put(AppDatabaseConstants.COLUMN_GROUP_JID,groupId);
					contentValues.put(AppDatabaseConstants.KEY_CONTACT_PIC, memberBean.getProfilePic());
					if(memberBean.isAdmin()){
						contentValues.put(AppDatabaseConstants.COLUMN_GROUP_ADMIN,"Y");
					}else{
						contentValues.put(AppDatabaseConstants.COLUMN_GROUP_ADMIN,"N");
					}
					context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.GROUP_DETAIL_CONTENT_URI_STRING), contentValues);
				}

			}
		}).start();

	}

	public void createNewGroupChat(String to,String time,String messageText,boolean isReceived,String groupName){
		ContentValues contentValues = new ContentValues();
		String appUserId = AppSharedPreference.getInstance(context).getUserID();
		contentValues.put(AppDatabaseConstants.USER_ID, to);
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserId);
		contentValues.put(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED, time);
		contentValues.put(AppDatabaseConstants.COLUMN_LAST_MESSAGE, messageText);
		contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE, 0);
		contentValues.put(AppDatabaseConstants.NAME,groupName);
		contentValues.put(AppDatabaseConstants.COLUMN_IS_GROUP_CHAT, "Y");
		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING), contentValues);
		context.getContentResolver().notifyChange(uri, null);
		if (!AppSharedPreference.getInstance(context).getChatSceenVisbility()&& isReceived) {
			getUserDataAndShowNotification(to, messageText);
		}
	}

	public List<String> getAllGroupJoined() {
		List<String> allJoinedGroup = new ArrayList<String>();
		String appUserId = AppSharedPreference.getInstance(context).getUserID();
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.GROUP_CHAT_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_APP_USER_ID + " =? AND " + AppDatabaseConstants.COLUMN_GROUP_MEMBER + " =? ", new String[]{appUserId, "Y"}, null);
		if(cursor!=null){
			if(cursor.getCount()>0){
				while (cursor.moveToNext()) {
					String groupJID = 	cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_GROUP_JID));
					allJoinedGroup.add(groupJID);

				}
			}
		}
		cursor.close();
		return allJoinedGroup;
	}



	public void saveGroupChatMessageInDb(Context context, String messageText,String groupJid,String sender, String messageId, int messageStatus,int conversationType, String messageType, String filePath,boolean isReceived){
		long time = System.currentTimeMillis();
		String appUserID = AppSharedPreference.getInstance(context).getUserID();
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserID);
		contentValues.put(AppDatabaseConstants.COLUMN_MESSAGE, messageText);
		contentValues.put(AppDatabaseConstants.COLUMN_MESSAGE_ID, messageId);
		contentValues.put(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED, time);
		contentValues.put(AppDatabaseConstants.USER_CHAT_ID, sender);
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_JID, groupJid);
		contentValues.put(AppDatabaseConstants.MESSAGE_STATUS, messageStatus);
		contentValues.put(AppDatabaseConstants.COLUMN_CONVERSATION_TYPE,conversationType);
		contentValues.put(AppDatabaseConstants.MESSAGE_TYPE, messageType);
		contentValues.put(AppDatabaseConstants.MESSAGE_FILE_PATH, filePath);

		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.GROUP_CHAT_DETAIL_CONTENT_URI_STRING),	contentValues);
		if (Integer.parseInt(uri.getLastPathSegment()) > 0) {
			Log.e("Photo uploaded - ", "-------------");
		}

	}

	public String getContactNameFromNumber(String phoneumber){
		phoneumber = phoneumber.split("@")[0];
		String id = getUserIdFromNumber(phoneumber);
		String name = phoneumber;
		if(id!=null){
			Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_ID +" = ? ", new String[]{id}, null);
			if(cursor!=null){
				cursor.moveToFirst();
				name =   cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_NAME));
			}
			cursor.close();
		}

		return name;
	}

	public String getGroupImage(String groupJid){
		String groupIcon = null;
		groupJid = groupJid.split("@")[0];
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.GROUP_CHAT_CONTENT_URI_STRING), null, AppDatabaseConstants.COLUMN_GROUP_JID +" = ?", new String[]{groupJid} , null);
		if(cursor!=null){
			cursor.moveToFirst();
			try {
				groupIcon = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_GROUP_ICON));
			} catch (CursorIndexOutOfBoundsException e) {
				// TODO: handle exception
			}

		}
		cursor.close();
		return groupIcon;
	}

	public String getCiaoNumberFromId(String id){
		String ciaoNumber = null;
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_ID + " = ? AND " + AppDatabaseConstants.KEY_PHONE_TYPE + " = ? ", new String[]{id, "C"}, null);
		if(cursor!=null) {
			if (cursor.getCount() > 0 && cursor.moveToNext()) {
				ciaoNumber = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_PHONE_NUMBER));
			}
		}
		cursor.close();
		return ciaoNumber;


	}

	public String getCiaoNumberFromCommUser(String commUserFull){
		String commUser = commUserFull.split("@")[0];
		String ciaoNumber = null;
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_COMM_USER + " = ? AND " + AppDatabaseConstants.KEY_PHONE_TYPE + " = ? ", new String[]{commUser, "C"}, null);
		if(cursor!=null) {
			if (cursor.getCount() > 0 && cursor.moveToNext()) {
				ciaoNumber = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_PHONE_NUMBER));
			}
		}
		cursor.close();
		return ciaoNumber;


	}
	/*
	 * This method is used to save chat form and to user in app db.
	 */

	public void saveChatMessageInDb(Context context, String messageText,String to, String phoneNumber, String messageId, int messageStatus,int conversationType, String messageType, String filePath,boolean isReceived) {
		long time = System.currentTimeMillis();
		String appUserId = AppSharedPreference.getInstance(context).getUserID();
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserId);
		contentValues.put(AppDatabaseConstants.COLUMN_MESSAGE, messageText);
		contentValues.put(AppDatabaseConstants.COLUMN_MESSAGE_ID, messageId);
		contentValues.put(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED, time);
		contentValues.put(AppDatabaseConstants.USER_CHAT_ID, to);
		contentValues.put(AppDatabaseConstants.MESSAGE_STATUS, messageStatus);
		contentValues.put(AppDatabaseConstants.COLUMN_CONVERSATION_TYPE,conversationType);
		contentValues.put(AppDatabaseConstants.MESSAGE_TYPE, messageType);
		contentValues.put(AppDatabaseConstants.MESSAGE_FILE_PATH, filePath);

		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.CHAT_USER_MESSAGE_CONTENT_URI_STRING),contentValues);
		if (Integer.parseInt(uri.getLastPathSegment()) > 0) {
			context.getContentResolver().notifyChange(uri, null);
			Cursor cursor = context
					.getContentResolver()
					.query(Uri
									.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),
							null, AppDatabaseConstants.USER_ID + " = ? AND " + AppDatabaseConstants.KEY_APP_USER_ID + " =? ",
							new String[]{to, appUserId}, null);
			if (cursor != null) {
				if (cursor.getCount() > 0 && cursor.moveToNext()) {
					// user already have chat thread
					int unreadMessages = cursor.getInt(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE));
					updateLastMessage(to, time, messageText, unreadMessages, isReceived, false);
				} else {
					// new chat user
					createNewChatUser(to, time, messageText, isReceived, phoneNumber);
					updateUserDetails(to, messageText);
				}

			}

		}

	}

	/*
	 * This method is used to save the sms sent via app in db
	 * @param toUserId  - ID of the user to whom sms will be sent.
	 * @param messageBody - Text of message.
	 * @param messageStatus - Message status like failed or sent
	 */

	public synchronized void saveSmsInDb(String toUserId,String messageBody,int messageStatus ,int conversationType,boolean isRecieved,long time){
		String appUserID = AppSharedPreference.getInstance(context).getUserID();
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserID);
		contentValues.put(AppDatabaseConstants.USER_CHAT_ID, toUserId);
		contentValues.put(AppDatabaseConstants.COLUMN_MESSAGE, messageBody);
		contentValues.put(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED, time);
		contentValues.put(AppDatabaseConstants.COLUMN_CONVERSATION_TYPE, conversationType);
		contentValues.put(AppDatabaseConstants.MESSAGE_STATUS, messageStatus);
		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.CONTENT_URI_SMS_DETAIL), contentValues);
		if (Integer.parseInt(uri.getLastPathSegment()) > 0){
			//Log.e("Message saved in db = ","------------------------------");
			context.getContentResolver().notifyChange(uri, null);
			Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.CONTENT_URI_SMS),null, AppDatabaseConstants.USER_ID + " = ? AND "+AppDatabaseConstants.KEY_APP_USER_ID+" = ? ",new String[] { toUserId,appUserID}, null);
			if(cursor!=null){
				if(cursor.getCount()>0 && cursor.moveToNext()){
					//User already exist
					int unreadMessages = cursor.getInt(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE));
					if(!AppSharedPreference.getInstance(context).getSMSSceenVisbility()){
						unreadMessages =unreadMessages+1;
					}
					updateLastSms(appUserID,toUserId, time, messageBody, unreadMessages,isRecieved);
				}else{
					//Create new user in sms log table
					createNewUserInSMSLogTable(toUserId,messageBody,time,isRecieved);
				}
			}
		}
	}

	/*
	 * This method is used to create a new user in sms log table if user does'nt exist in this table
	 * @param - toUserId - ID of the user to whom sms will be sent.
	 * @param - messageBody - Text of sms.
	 * @param - time - timestamp of sent sms
	 * @param - isRecieved - true if user received sms,false if user sent sms.
	 */
	public void createNewUserInSMSLogTable(String toUserId,String messageBody,long time,boolean isReceived){
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, AppSharedPreference.getInstance(context).getUserID());
		contentValues.put(AppDatabaseConstants.USER_ID, toUserId);
		contentValues.put(AppDatabaseConstants.NAME, getUserNameFromUserId(toUserId));
		contentValues.put(AppDatabaseConstants.KEY_CONTACT_PIC, getUserPicFromNumber(toUserId));
		contentValues.put(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED, time);
		contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE, 0);
		contentValues.put(AppDatabaseConstants.COLUMN_LAST_MESSAGE, messageBody);
		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.CONTENT_URI_SMS),contentValues);
		context.getContentResolver().notifyChange(uri, null);
		if (!AppSharedPreference.getInstance(context).getSMSSceenVisbility()&& isReceived) {
			getUserDataAndShowNotification(toUserId, messageBody);
		}

	}

	/*
	 * This method is used to get user name from user number
	 * @param - phoneNumber - number of user
	 * @return - userName - name of the user
	 */
	public String getUserNameFromUserId(String userId){
		String userName = null;
		if(userId!=null) {
			Cursor cursor = context.getContentResolver().query(
					Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING), null,
					AppDatabaseConstants.KEY_ID + " = ?",
					new String[]{userId}, null);
			if (cursor != null) {
				if (cursor.getCount() > 0 && cursor.moveToNext()) {
					userName = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				}
			}
		}
		return userName;
	}
	/*
	 * This method is used to get user name from comm user
	 * @param - coom_user - number of user
	 * @return - userName - name of the user
	 */
	public String getUserNameFromCommUser(String commUser, String phoneNumber){
		String userName = phoneNumber;
		if(phoneNumber == null){
			phoneNumber = getCiaoNumberFromCommUser(commUser);
		}
		String userId = getUserIdFromNumber(phoneNumber);
		if (userId != null) {
			Cursor cursor = context.getContentResolver().query(
					Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING), null,
					AppDatabaseConstants.KEY_ID + " = ?",
					new String[] { userId }, null);
			if(cursor!=null){
				if(cursor.getCount()>0 && cursor.moveToNext()){
					userName =   cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				}
			}
		}


		return userName;
	}

	/*
	 * This method is used to get user contact pic from user number
	 * @param - phoneNumber - number of user
	 * @return - userPic - contact pic of the user
	 */
	public String getUserPicFromNumber(String phoneNumber){
		String userPic = null;
		String userID  = getUserIdFromNumber(phoneNumber);
		if(userID!=null){
			Cursor cursor = context.getContentResolver().query(
					Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING), null,
					AppDatabaseConstants.KEY_ID + " = ?",
					new String[] { userID }, null);
			if(cursor!=null){
				if(cursor.getCount()>0 && cursor.moveToNext()){
					userPic =   cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
				}
			}
		}else{
			userPic = "";
		}

		return userPic;
	}
	/*
	 * This method is used to update the last message in sms Log table
	 */
	public void updateLastSms(String appUserID,String to, long time, String smsText,int unreadMessage,boolean isRecieved) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.USER_ID, to);
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID, appUserID);
		contentValues.put(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED, time);
		contentValues.put(AppDatabaseConstants.COLUMN_LAST_MESSAGE, smsText);
		/*if (!AppSharedPreference.getInstance(context).getSMSSceenVisbility()) {
			contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE,unreadMessage + 1);
			context.getContentResolver().update(Uri
					.parse(AppDatabaseConstants.CONTENT_URI_SMS),
					contentValues,
					AppDatabaseConstants.USER_ID + " = ?",
					new String[] { to });

		} else {
			contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE,
					unreadMessage + 1);
			context.getContentResolver().update(Uri	.parse(AppDatabaseConstants.CONTENT_URI_SMS),
					contentValues,
					AppDatabaseConstants.USER_ID + " = ?",
					new String[] { to });
		}*/
		contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE,unreadMessage);
		context.getContentResolver().update(Uri
						.parse(AppDatabaseConstants.CONTENT_URI_SMS),
				contentValues,
				AppDatabaseConstants.USER_ID + " = ? AND "+AppDatabaseConstants.KEY_APP_USER_ID+" = ? ",
				new String[] { to,appUserID });
		if((!AppSharedPreference.getInstance(context).getSMSSceenVisbility())&& isRecieved){
			getUserDataAndShowNotification(to, smsText);
		}
	}
	/*
     * This method is used to save current app user Ciao number is database.
     */
	public void saveMyCioaNumberToDb(String number){
		ContentValues contentValues = new ContentValues();
		String appUserId = AppSharedPreference.getInstance(context).getUserID();
		contentValues.put(AppDatabaseConstants.KEY_CIAO_NUMBER,number);
		contentValues.put(AppDatabaseConstants.KEY_APP_USER_ID,appUserId);
		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.CONTERT_URI_MY_CIAO), contentValues);
	}

	/*
	 * * This method is used to get users ciao numbers
	 */

	public List<String> getMyCiaoNumber()
	{
		List<String> ciaoNumber = new ArrayList<String>();
		String appUserId = AppSharedPreference.getInstance(context).getUserID();
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.CONTERT_URI_MY_CIAO),null,AppDatabaseConstants.KEY_APP_USER_ID+" =? ",new String[]{appUserId},null);
		if(cursor!=null)
		{
			if(cursor.getCount()>0)
			{
				while(cursor.moveToNext())
				{
					String myCiaoNumber = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CIAO_NUMBER));
					myCiaoNumber = AppUtils.formatCiaoNumberUsingParentheses(myCiaoNumber);
					ciaoNumber.add(myCiaoNumber);
				}
			}

		}
		return ciaoNumber;

	}
	/*
	 * This method is used to clear the database for the user previously logged in
	 */
	public void clearDatabaseonLogout(){
		databaseHelper.clearMyCiaoNumberDb(database);
	}

	/**
	 *  This method is used to get All Group Member List
	 *
	 */
	public List<MemberBean> getGroupMemeberList(String groupJID)
	{
		List<MemberBean> userIdList = new ArrayList<MemberBean>();
		if(groupJID!=null)
		{
			Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.GROUP_DETAIL_CONTENT_URI_STRING),	null, AppDatabaseConstants.COLUMN_GROUP_JID + " = ? ", new String[] {groupJID},null);
			if(cursor!=null){
				if(cursor.getCount()>0){
					while(cursor.moveToNext()){
						MemberBean memberBean = new MemberBean();
						String userJID = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.USER_ID));
						String isGroupAdmim = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_GROUP_ADMIN));
						String profilePic = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
						if(userJID.equalsIgnoreCase(AppSharedPreference.getInstance(context).getUserCountryCode()+AppSharedPreference.getInstance(context).getUserPhoneNumber())){
							memberBean.setName("You");
						}else{
							memberBean.setName(getUserNameFromUserId(userJID));
						}
						if(isGroupAdmim.equalsIgnoreCase("Y")){
							memberBean.setAdmin(true);
						}
						memberBean.setProfilePic(profilePic);
						memberBean.setMemberJID(userJID);
						userIdList.add(memberBean);
					}
				}
			}


		}

		return userIdList;

	}


	/**
	 * This method is used to show visibility of group media
	 *
	 */
	public Boolean isGroupMediaAvailable(String groupJID) {
		if (groupJID != null) {
			Cursor cursor = context
					.getContentResolver()
					.query(Uri
									.parse(AppDatabaseConstants.GROUP_CHAT_DETAIL_CONTENT_URI_STRING),
							null,
							AppDatabaseConstants.COLUMN_GROUP_JID + " = ? AND " + AppDatabaseConstants.MESSAGE_TYPE +" = ? OR "+ AppDatabaseConstants.MESSAGE_TYPE + " = ? ",
							new String[] { groupJID,XmppConstants.TYPE_IMAGE ,XmppConstants.TYPE_VIDEO}, null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					return true;
				}
			}
		}
		return false;
	}


	/*
	 * This method is used to get groupMedia
	 */
	public List<MediaBean> getGroupMedia(String groupJID)
	{
		List<MediaBean> mediaList = new ArrayList<MediaBean>();
		if(groupJID!=null)
		{
			Cursor cursor = context
					.getContentResolver()
					.query(Uri
									.parse(AppDatabaseConstants.GROUP_CHAT_DETAIL_CONTENT_URI_STRING),
							null,
							AppDatabaseConstants.COLUMN_GROUP_JID + " = ? AND " + AppDatabaseConstants.MESSAGE_TYPE +" = ? OR "+ AppDatabaseConstants.MESSAGE_TYPE + " = ? ",
							new String[] { groupJID,XmppConstants.TYPE_IMAGE ,XmppConstants.TYPE_VIDEO}, null);

			if(cursor!=null){
				if(cursor.getCount()>0)
				{
					while(cursor.moveToNext())
					{
						String mediaValue = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE));
						String mediaType = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.MESSAGE_TYPE));

						MediaBean mediaBean = new MediaBean();
						mediaBean.setMediaType(mediaType);
						mediaBean.setMediaFilePath(mediaValue);
						mediaList.add(mediaBean);

					}


				}
			}
		}
		return mediaList;


	}
	// not in used in new flow
	public void saveCiaoRatesInLocalDb(String countryCode, String smsRates,	String callRates) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.COLUMN_COUNTRY_CODE, countryCode);
		contentValues.put(AppDatabaseConstants.COLUMN_CALL_RATES, callRates);
		contentValues.put(AppDatabaseConstants.COLUMN_SMS_RATES, smsRates);
		Uri uri = context.getContentResolver().insert(Uri.parse(AppDatabaseConstants.CONTERT_URI_CIAO_RATES),contentValues);
		context.getContentResolver().notifyChange(uri, null);

	}
	// not in used in new flow
	public String getCallRateForSelectedCountry(String countryCode) {
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.CONTERT_URI_CIAO_RATES), null, AppDatabaseConstants.COLUMN_COUNTRY_CODE+" = ? ", new String[]{countryCode}, null);
		if(cursor!=null){
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				String callRates =  cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_CALL_RATES));
				return callRates;
			}
		}
		return "0.0";
	}
	// not in used in new flow
	public String getSMSRateForSelectedCountry(String countryCode) {
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.CONTERT_URI_CIAO_RATES), null, AppDatabaseConstants.COLUMN_COUNTRY_CODE+" = ? ", new String[]{countryCode}, null);
		if(cursor!=null){
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				String callRates =  cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_SMS_RATES));
				return callRates;
			}
		}
		return "0.0";
	}

	/*
	 * This method is used to get the group creation time
	 * @param - groupJid group id.
	 * @return - group creation time.
	 */
	public String getGroupCreationTime(String groupJid){
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.GROUP_CHAT_CONTENT_URI_STRING), null, AppDatabaseConstants.COLUMN_GROUP_JID+" = ? ", new String[]{groupJid}, null);
		if(cursor!=null){
			if(cursor.getCount()>0){
				cursor.moveToFirst();
				String timeString = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_GROUP_CREATION_TIME));
				return timeString;
			}
		}
		return null;
	}

	/*
	 * This method is used to add all friends who is using ciao to user roster so that they can chat with each other
	 */
	public  void addUserContactsToOpenfireServer(final XMPPConnection xmppConnection) {
		if(xmppConnection!=null){
			final Roster roster = xmppConnection.getRoster();
			final List<CiaoContactBean> ciaoContactBeans = ApplicationDAO.getInstance(context).getAllContacts();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < ciaoContactBeans.size(); i++) {
						CiaoContactBean ciaoContactBean = ciaoContactBeans.get(i);
						String id = ciaoContactBean.getId();
						//Log.e("Name = ",ciaoContactBean.getName());
						String userJid = ciaoContactBean.getCommUser() +"@"+ XmppConstants.HOST;
						try {
							roster.createEntry(userJid, ciaoContactBean.getName(), null);
							Presence presence = new Presence(Presence.Type.subscribed);
							presence.setTo(userJid);
							xmppConnection.sendPacket(presence);
							RosterPacket r = new RosterPacket();
							r.setType(IQ.Type.SET);
							RosterPacket.Item ri = new RosterPacket.Item(userJid, null);
							ri.setItemType(RosterPacket.ItemType.both);
							r.addRosterItem(ri);
							xmppConnection.sendPacket(r);
							Log.e("Jabber id -", userJid);

						} catch (XMPPException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			});
			thread.start();
		}

	}
	/*
     * This method is used to update the number of unread chat from a particular friend.
     */
	public void updateUnreadCount(String chatId){
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE,0);
		context.getContentResolver().update(Uri.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),contentValues,AppDatabaseConstants.USER_ID + " = ?",new String[] { chatId });
	}
	/*
     * This method is used to update the number of unread SMS from a particular friend.
     */
	public void updateUnreadSMSCount(String chatId){
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE,0);
		context.getContentResolver().update(Uri.parse(AppDatabaseConstants.CONTENT_URI_SMS),contentValues,AppDatabaseConstants.USER_ID + " = ?",new String[] { chatId });
	}
	/*
     * This method is used to get the number of unread chat from a particular friend.
     */
	public int getUnreadMessageCount(String to){
		Cursor cursor = context
				.getContentResolver()
				.query(Uri
								.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),
						null, AppDatabaseConstants.USER_ID + " = ? ",
						new String[] { to }, null);
		if (cursor != null) {
			if (cursor.getCount() > 0 && cursor.moveToNext()) {
				// user already have chat thread
				int unreadMessages = cursor.getInt(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE));
				return unreadMessages;
			}
		}
		return 0;
	}
	/*
     * This method is used to get ciao registered number of friend
     * @param  - id - id of the friend in contact db
     * @return - ciao registered number
     */
	public String getCiaoRegisteredNumber(String id){
		String number = null;
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_ID+" = ? AND "+AppDatabaseConstants.KEY_CIAO_REGISTERED+" = ? ", new String[]{id,"Y"}, null);
		if(cursor!=null){
			if(cursor.getCount()>0 && cursor.moveToNext()){
				number = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_PHONE_NUMBER));
			}
		}
		return number;
	}
	/*
     * This method is used to get jabber d of group admin
     * @param - groupJid;
     * @return - admin Id;
     */
	public String getGroupAdmin(String groupJid){
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.GROUP_DETAIL_CONTENT_URI_STRING), null, AppDatabaseConstants.COLUMN_GROUP_JID+" =? AND "+AppDatabaseConstants.COLUMN_GROUP_ADMIN+" = ? ", new String[]{groupJid,"Y"}, null);
		String adminJid = null;
		if(cursor!=null){
			if(cursor.getCount()>0 && cursor.moveToNext()){
				adminJid = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.USER_ID));
			}
		}
		return adminJid;
	}
	/*
     * This method is used to get the last time stamp of sms received by user(Ciao out)
     */
	public String getLastSMSTime() {
		String timeStamp = "";
		String selectQuery= "SELECT * FROM " + AppDatabaseConstants.TABLE_SMS_DETAILS+" ORDER BY "+AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED+" DESC LIMIT 1";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if(cursor!=null){
			if(cursor.moveToFirst()){
				timeStamp  =  cursor.getString( cursor.getColumnIndex(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED) );
			}

			cursor.close();
		}
		return timeStamp;
	}
	/*
     * This method is used to check whether incoming message already exist in the database
     */
	public synchronized boolean isMessageAlreadyExist(String messageId){
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.GROUP_CHAT_DETAIL_CONTENT_URI_STRING), null, AppDatabaseConstants.COLUMN_MESSAGE_ID+" = ? ", new String[]{messageId}, null);
		if(cursor!=null){
			if(cursor.getCount()>0){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	/*
     * This method is used to handle the case when someone saved the same mobile number as more than one contacts.
     */
	public ArrayList<String> getAllIdOfACiaoRegiesteredNumber(ArrayList<CiaoContactBean> ciaoContactBeans){
		ArrayList<String> ids = new ArrayList<String>();
		for(int i=0;i<ciaoContactBeans.size();i++){
			String ciaoRegisteredNumber =getCiaoRegisteredNumber(ciaoContactBeans.get(i).getId());

			Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_CIAO_REGISTERED+" =? AND "+AppDatabaseConstants.KEY_PHONE_NUMBER+" =? ", new String[]{"Y",ciaoRegisteredNumber}, null);
			if(cursor!=null){
				if(cursor.getCount()>0){
					while(cursor.moveToNext()){
						String id = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_ID));
						ids.add(id);
					}
				}
			}
		}

		return ids;
	}
	/*
	 * This method is used to update the group name in local db.
	 */
	public void updateGroupName(final String groupName,String groupJID){
		//Update Group Chat 
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_NAME,groupName);
		context.getContentResolver().update(Uri.parse(AppDatabaseConstants.GROUP_CHAT_CONTENT_URI_STRING),contentValues,AppDatabaseConstants.COLUMN_GROUP_JID + " = ?",new String[] { groupJID });
		//Update Chat log
		ContentValues contentValues1 = new ContentValues();
		contentValues1.put(AppDatabaseConstants.KEY_NAME,groupName);
		context.getContentResolver().update(Uri.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING),contentValues1,AppDatabaseConstants.USER_ID + " = ?",new String[] { groupJID });

	}

	/*
	 * This method is used to update the group image in local db.
	 */
	public void updateGroupImage(String groupJID,String messageBody) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_ICON,messageBody);
		context.getContentResolver().update(Uri.parse(AppDatabaseConstants.GROUP_CHAT_CONTENT_URI_STRING),contentValues,AppDatabaseConstants.COLUMN_GROUP_JID + " = ?",new String[] { groupJID });

	}
	/*
     * This method is used to check whether current user of the app is member of a group or not
     * @param -groupJid - jabber id of the group
     * @return - boolean
     */
	public boolean isMemberOfThisGroup(String groupJid){
		List<String> allGroupJoined = getAllGroupJoined();
		for(String groupJID:allGroupJoined){
			if(groupJID.equals(groupJid)){
				return true;
			}
		}
		return false;
	}

	/*public void leaveGroup(String groupJID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AppDatabaseConstants.COLUMN_GROUP_MEMBER,"N");
		context.getContentResolver().update(Uri.parse(AppDatabaseConstants.GROUP_CHAT_CONTENT_URI_STRING),contentValues,AppDatabaseConstants.COLUMN_GROUP_JID + " = ?",new String[] { groupJID });
		//updateLastMessage(groupJID, System.currentTimeMillis(), "You left ", 0, true,true);
	}*/

	/*
	 * This method is used to show the count of unread message in bottom tab 
	 */
	public int getContactsCountWithUnreadMessages(){
		int contactsWithUnreadMessage = 0;
		String appUserID = AppSharedPreference.getInstance(context).getUserID();
		Cursor cursor = context.getContentResolver().query(Uri.parse(AppDatabaseConstants.CHAT_USER_CONTENT_URI_STRING), null, AppDatabaseConstants.KEY_APP_USER_ID+" =? ", new String[] { appUserID }, null);
		if(cursor!=null){
			while (cursor.moveToNext()) {
				String unreadMessage = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE));
				int unreadCount = Integer.parseInt(unreadMessage);
				if(unreadCount>0){
					contactsWithUnreadMessage++;
				}
			}
		}
		return contactsWithUnreadMessage;
	}
}