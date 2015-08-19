package com.ciao.app.views.activities;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.adapters.CallLogsBean;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.PhoneNumberBean;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.netwrok.backgroundtasks.GetCallLimitAndCharge;
import com.ciao.app.utils.AppUtils;
import com.csipsimple.utils.CallBaseActivity;

/**
 * Created by rajat on 31/1/15.
 * This srceen show the details of a contacts.
 */
public class ContactDetailsActivity extends CallBaseActivity implements OnClickListener {
	private String contactId, name, pic;
	private List<String> phoneList = new ArrayList<String>();
	private List<PhoneNumberBean> userPhoneList = new ArrayList<PhoneNumberBean>();
	private String countryCode = "+1";
	private TextView inviteTV;
//	private Spinner contactsSpnr;
//	public ContactSpinnerAdapter mContactSpinnerAdapter;
	private RelativeLayout popTalkWidgetRL,normalWidgetRL;
	private int popTalkCall,popChat,sms,call;
//	private PhoneNumberBean phoneNumberBean;
	private String userJID;
	private boolean isFromCallLog = false;
	private String callRates;
	private boolean fromSMSScreen =false;
	private boolean isNumber =false;

	private List<PhoneNumberBean> getPhoneNumberBean(CallLogsBean bean,String isCiaoUser) {

		PhoneNumberBean bean2 = new PhoneNumberBean();
		bean2.setId("");
		bean2.setIsCiaoUser(isCiaoUser);
		bean2.setPhoneNumber(AppUtils.parseFormattedNumberFromCallLog(bean.getContactNumber()));
		bean2.setType("");
		ArrayList<PhoneNumberBean> arrayList = new ArrayList<PhoneNumberBean>();
		arrayList.add(bean2);
		userPhoneList.add(bean2);
		return arrayList;
	}
	/*private List<PhoneNumberBean> getPhoneNumberBean(CallLogsBean bean) {

		PhoneNumberBean bean2 = new PhoneNumberBean();
		bean2.setId("");
		bean2.setIsCiaoUser("N");
		bean2.setPhoneNumber(AppUtils.parseFormattedNumberFromCallLog(bean.getContactNumber()));
		bean2.setType("");
		ArrayList<PhoneNumberBean> arrayList = new ArrayList<PhoneNumberBean>();
		arrayList.add(bean2);
		userPhoneList.add(bean2);
		return arrayList;
	}*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_detail_new);
		isFromCallLog = false;
		inviteTV = (TextView)findViewById(R.id.tv_invite_to_poptalk);
		inviteTV.setOnClickListener(this);

		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			CallLogsBean bean = bundle.getParcelable("bean");
			if (bean != null) {
				name = bean.getContactName();
				String phoneNumber = AppUtils.parseFormattedNumberFromCallLog(bean.getContactNumber());
				contactId = ApplicationDAO.getInstance(this).getUserIdFromNumber(phoneNumber);
				if(contactId==null){
					new CheckCiaoNumber(this, phoneNumber, bean).execute();
					getPhoneNumberBean(bean, "N");
				}else{
					name = ApplicationDAO.getInstance(this).getUserNameFromUserId(contactId);
					pic = ApplicationDAO.getInstance(this).getUserPicFromNumber(phoneNumber);
				}

			} else if (bundle != null) {
				contactId = bundle.getString("_id");
				name = bundle.getString("_name");
				pic = bundle.getString("_pic");
				fromSMSScreen = bundle.getBoolean("from_sms");
				isNumber= bundle.getBoolean("is_number");
			}
			((TextView) findViewById(R.id.tv_contact_name)).setText(name);
			if (pic != null) {
				try {
					Bitmap bmp=BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(pic)));
					((ImageView) findViewById(R.id.iv_user_profile_pic)).setImageBitmap(bmp);  

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				((ImageView) findViewById(R.id.iv_user_profile_pic)).setImageResource(R.drawable.user_avtar);
			}

		}
		if (contactId != null) {
			if(fromSMSScreen){
				if(isNumber){
					String tempContactId = ApplicationDAO.getInstance(this).getUserIdFromNumber(contactId);
					if(tempContactId!=null && (!tempContactId.equalsIgnoreCase(contactId))){
						getContactsDetails(tempContactId);
					}else{
						PhoneNumberBean phoneNumberBean = new PhoneNumberBean();
						phoneNumberBean.setPhoneNumber(contactId);
						phoneNumberBean.setType("N");
						phoneNumberBean.setIsCiaoUser("N");
						phoneNumberBean.setId(contactId);
						userPhoneList.add(phoneNumberBean);
					}
				}else{
					getContactsDetails(contactId);
				}
				
			}else {
				getContactsDetails(contactId);	
			}

		}

		TableLayout tablePhone = (TableLayout) findViewById(R.id.tb_phone);
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		TableRow.LayoutParams textParams =
				new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT, 4f);
		textParams.setMargins(20,20,20,20);
		TableRow.LayoutParams imageParams =
				new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT, 1f);

		for (final PhoneNumberBean phoneNumberBean : userPhoneList) {
			TableRow phoneRow = new TableRow(this);
			phoneRow.setLayoutParams(rowParams);

			LinearLayout phoneNumberLayout = new LinearLayout(this);
			phoneNumberLayout.setOrientation(LinearLayout.VERTICAL);

			TextView phoneNumberText = new TextView(this);
			phoneNumberText.setText(AppUtils.formatNumberUsingParenthesesforCallLog(phoneNumberBean.getPhoneNumber(), this));
			phoneNumberText.setTextSize(24);
			phoneNumberLayout.addView(phoneNumberText);

			phoneRow.addView(phoneNumberLayout, 0, textParams);

			if ("N".equals(phoneNumberBean.getIsCiaoUser())) {
				ImageView imageMessage = new ImageView(this);
				imageMessage.setImageResource(R.drawable.nopoptalk_sms);
				imageMessage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String userID = phoneNumberBean.getPhoneNumber();
						Intent smsIntent = new Intent(ContactDetailsActivity.this,NewSmsActivity.class);
						smsIntent.putExtra("_id", userID);
						smsIntent.putExtra("_name", name);
						smsIntent.putExtra("_pic", pic);
						startActivity(smsIntent);
					}
				});
				phoneRow.addView(imageMessage, 1, imageParams);

				ImageView callMessage = new ImageView(this);
				callMessage.setImageResource(R.drawable.nopoptalk_phone);
				callMessage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(NetworkStatus.isConected(ContactDetailsActivity.this)){
							Log.e("Phone ", phoneNumberBean.getPhoneNumber());
							if((phoneNumberBean.getType()).equalsIgnoreCase("C")){
								AppSharedPreference.getInstance(ContactDetailsActivity.this).setIsCaioOutCall(true);
								placeCallWithOption(null, false, phoneNumberBean.getPhoneNumber());
								AppSharedPreference.getInstance(ContactDetailsActivity.this).setIsCaioOutCall(false);
							}else{
								AppSharedPreference.getInstance(ContactDetailsActivity.this).setIsCaioOutCall(true);
								countryCode = "+"+AppUtils.getCountryCodeFromNumber(phoneNumberBean.getPhoneNumber());
								new GetCallLimitAndCharge(ContactDetailsActivity.this, getCountryCode(phoneNumberBean), ContactDetailsActivity.this, phoneNumberBean.getPhoneNumber()).execute();
								placeCallWithOption(null, false, phoneNumberBean.getPhoneNumber());
							}

						}else{
							AppUtils.showTost(ContactDetailsActivity.this,"Please check you internet connection");
						}
					}
				});
				phoneRow.addView(callMessage, 2, imageParams);
			} else {
				ImageView imageMessage = new ImageView(this);
				imageMessage.setImageResource(R.drawable.yespoptalk_sms);
				imageMessage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					if(!isFromCallLog){
						userJID = ApplicationDAO.getInstance(ContactDetailsActivity.this).getUserIdFromNumber(phoneNumberBean.getPhoneNumber());
						if(userJID!=null){
							userJID = ApplicationDAO.getInstance(ContactDetailsActivity.this).getCommUserFromUserId(userJID);

							if (userJID == null) {
								userJID = phoneNumberBean.getPhoneNumber();
							}
						}
					}
					Intent intent = new Intent(ContactDetailsActivity.this,ChatActivity.class);
					intent.putExtra("jid", userJID);
					intent.putExtra("user_name", phoneNumberBean.getPhoneNumber());
					intent.putExtra("user_pic", pic);
					startActivity(intent);
					}
				});
				phoneRow.addView(imageMessage, 1, imageParams);

				ImageView callMessage = new ImageView(this);
				callMessage.setImageResource(R.drawable.yespoptalk_phone);
				callMessage.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(NetworkStatus.isConected(ContactDetailsActivity.this)){
							Log.e("Phone ", phoneNumberBean.getPhoneNumber());
							if((phoneNumberBean.getType()).equalsIgnoreCase("C")){
								AppSharedPreference.getInstance(ContactDetailsActivity.this).setIsCaioOutCall(false);
								placeCallWithOption(null, false, phoneNumberBean.getPhoneNumber());
							}else{
								AppSharedPreference.getInstance(ContactDetailsActivity.this).setIsCaioOutCall(true);
								countryCode = "+"+AppUtils.getCountryCodeFromNumber(phoneNumberBean.getPhoneNumber());
								new GetCallLimitAndCharge(ContactDetailsActivity.this, getCountryCode(phoneNumberBean), ContactDetailsActivity.this, phoneNumberBean.getPhoneNumber()).execute();
								placeCallWithOption(null, false, phoneNumberBean.getPhoneNumber());
							}

						}else{
							AppUtils.showTost(ContactDetailsActivity.this,"Please check you internet connection");
						}
					}
				});
				phoneRow.addView(callMessage, 2, imageParams);

				TextView phoneTypeText = new TextView(this);
				phoneTypeText.setText("PopTalk Number");
				phoneNumberLayout.addView(phoneTypeText);
			}
			tablePhone.addView(phoneRow);
		}
	}

	// This method use to get the phone numbers and email id of a user.
	private void getContactsDetails(String contactId) {

		Cursor cursor = getContentResolver().query(
				Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING),
				null, AppDatabaseConstants.KEY_ID + " = ?",
				new String[] { contactId }, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String isCiaoUser = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CIAO_USER));
				String isFavoriteContact = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_FAVORITE_CONTACT));
				Log.e("isCiaoUser", isCiaoUser);
				if (isCiaoUser.equalsIgnoreCase("Y")) {
					showUIForCiaoUser();
				} else {
					showUIForNonCiaoUser();
				}
				if(isFavoriteContact.equalsIgnoreCase("Y"))
				{
					//showUIForAddFavorite();

				}else{
					//showUIForRemoveFavorite();
				}
			}
		}

		Cursor pCursor = getContentResolver().query(
				Uri.parse(AppDatabaseConstants.PHONE_CONTENT_URI_STRING), null,
				AppDatabaseConstants.KEY_ID + " = ?",
				new String[] { contactId }, null);
		if (pCursor.getCount() > 0) {

			while (pCursor.moveToNext()) {
				String phone = pCursor.getString(pCursor
						.getColumnIndex(AppDatabaseConstants.KEY_PHONE_NUMBER));
				String phoneType = pCursor.getString(pCursor
						.getColumnIndex(AppDatabaseConstants.KEY_PHONE_TYPE));
				String isCiaoNumber = pCursor.getString(pCursor
						.getColumnIndex(AppDatabaseConstants.KEY_CIAO_USER));
				PhoneNumberBean phoneNumberBean = new PhoneNumberBean();
				phoneNumberBean.setPhoneNumber(phone);
				phoneNumberBean.setType(phoneType);
				phoneNumberBean.setIsCiaoUser(isCiaoNumber);
				phoneNumberBean.setId(contactId);
				userPhoneList.add(phoneNumberBean);
			}
			//inflatePhoneView(userPhoneList);
		}

	}

	/*private void inflatePhoneView(List<PhoneNumberBean> userPhoneList) {
		LinearLayout phoneViewRootLL = (LinearLayout) findViewById(R.id.ll_phones);
		phoneViewRootLL.removeAllViews();
		LayoutInflater inflater;
		phoneList.clear();
		for (int i = 0; i < userPhoneList.size(); i++) {
			inflater = LayoutInflater.from(this);
			PhoneNumberBean phoneNumberBean = userPhoneList.get(i);
			RelativeLayout phoneRowLayout = (RelativeLayout) inflater.inflate(R.layout.row_phone_view, null);
			final TextView phoneNumberTV = ((TextView) phoneRowLayout.findViewById(R.id.tv_contact_number));
			final ImageView messageIV = (ImageView) phoneRowLayout.findViewById(R.id.iv_work_contact1);
			final ImageView callIV = (ImageView) phoneRowLayout.findViewById(R.id.iv_call_green_1);
			messageIV.setTag(phoneNumberBean);
			callIV.setTag(phoneNumberBean);
			phoneNumberTV.setTag(phoneNumberBean);
			phoneNumberTV.setText(AppUtils.formatCiaoNumberUsingParentheses(phoneNumberBean.getPhoneNumber()));
			phoneList.add(phoneNumberBean.getPhoneNumber());
			if((phoneNumberBean.getType()).equalsIgnoreCase("C")){
				//messageIV.setVisibility(View.VISIBLE);
				((TextView) phoneRowLayout.findViewById(R.id.tv_phone_type)).setText("PopTalk Number");
			}else{
				messageIV.setVisibility(View.INVISIBLE);
			}
			if (isCiao) {
				messageIV.setVisibility(View.VISIBLE);
			} else {
				messageIV.setVisibility(View.GONE);
			}
			messageIV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PhoneNumberBean bean =(PhoneNumberBean)messageIV.getTag();
					String userJID = bean.getPhoneNumber();
					Intent intent = new Intent(ContactDetailsActivity.this,ChatActivity.class);
					intent.putExtra("jid", userJID);
					intent.putExtra("user_name", name);
					intent.putExtra("user_pic", pic);
					startActivity(intent);
				}
			});
			callIV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(NetworkStatus.isConected(ContactDetailsActivity.this)){
						PhoneNumberBean bean =(PhoneNumberBean)callIV.getTag();
						Log.e("Phone ", bean.getPhoneNumber());
						if((bean.getType()).equalsIgnoreCase("C")){
							placeCallWithOption(null, false, bean.getPhoneNumber());
						}else{
							countryCode = "+"+AppUtils.getCountryCodeFromNumber(bean.getPhoneNumber());
							setCallLimit(AppUtils.getAvailableCallLimit(ContactDetailsActivity.this, countryCode));
							placeCallWithOption(null, false, "345" + bean.getPhoneNumber());
						}

					}else{
						AppUtils.showTost(ContactDetailsActivity.this,"Please check you internet connection");	
					}

				}
			});
			phoneNumberTV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(NetworkStatus.isConected(ContactDetailsActivity.this)){
						PhoneNumberBean bean =(PhoneNumberBean)phoneNumberTV.getTag();
						if((bean.getType()).equalsIgnoreCase("C")){
							placeCallWithOption(null, false, bean.getPhoneNumber());
						}else{
							countryCode = "+"+AppUtils.getCountryCodeFromNumber(bean.getPhoneNumber());
							setCallLimit(AppUtils.getAvailableCallLimit(ContactDetailsActivity.this, countryCode));
							placeCallWithOption(null, false, "345" + bean.getPhoneNumber());
						}

					}else{
						AppUtils.showTost(ContactDetailsActivity.this,"Please check you internet connection");	
					}

				}
			});

			phoneViewRootLL.addView(phoneRowLayout);
		}
		//new AddToRoster().execute();
	}*/
	class AddToRoster extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				XMPPConnection xmppConnection = XMPPChatService.getConnection();
				Roster roster = xmppConnection.getRoster();
				if (xmppConnection != null && xmppConnection.isConnected()) {
					String userJid = userJID + "@videocasterapp.net";
					try {
						roster.createEntry(userJid, name, null);
						Presence presence = new Presence(
								Presence.Type.subscribed);
						presence.setTo(userJid);
						xmppConnection.sendPacket(presence);
						RosterPacket r = new RosterPacket();
						r.setType(IQ.Type.SET);
						RosterPacket.Item ri = new RosterPacket.Item(
								userJid, null);
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
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private void showUIForNonCiaoUser() {
		inviteTV.setVisibility(View.VISIBLE);
	}

	private void showUIForCiaoUser() {
		inviteTV.setVisibility(View.GONE);
	}

	public void backFromProfileAcitivity(View view) {
		finish();
	}

	/**
	 * This method is used to send invitation via message
	 */
	public void sendInvitation(String friendNumber) {

		friendNumber = friendNumber.replaceAll(" ", "");
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.setType("vnd.android-dir/mms-sms");
		smsIntent.putExtra("address", friendNumber);
		smsIntent.putExtra("sms_body","Join PopTalk to talk or message anyone or any phone in the world for free!");
		startActivity(smsIntent);

	}


	@Override
	public void onClick(View view) {

		switch(view.getId())
		{
		case R.id.tv_invite_to_poptalk:
			if(userPhoneList.size()>0){
				if(!(userPhoneList.get(0).getType()).equalsIgnoreCase("C"))
				{
					String phoneNumber = userPhoneList.get(0).getPhoneNumber();
					phoneNumber = phoneNumber.substring(phoneNumber.length()-10);
					sendInvitation(phoneNumber);
				}	
			}
			break;
		}
	}

	private String getCountryCode(PhoneNumberBean phoneNumberBean ) {
		return AppUtils.getCountryCodeFromNumber(phoneNumberBean.getPhoneNumber());
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(AppSharedPreference.getInstance(this).getIsCaioOutCall()){
			if(callRates!=null){
				AppUtils.handleCreditForCall(this, callRates);
			}
		}
	}

	private class CheckCiaoNumber extends AsyncTask<Void, Void, Void>{
		private Context context;
		private String phoneNumber;
		private CallLogsBean bean;
		private String errorCode;
		public  CheckCiaoNumber(Context context,String phoneNumber,CallLogsBean bean) {
			this.context = context;
			this.phoneNumber = phoneNumber;
			this.bean = bean;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}	
		@Override
		protected Void doInBackground(Void... params) {
			try {
				JSONObject jsonObject= new JSONObject();
				jsonObject.put("user_security", context.getString(R.string.user_security_key));
				jsonObject.put("ciao_number", phoneNumber);
				String response = NetworkCall.getInstance(context).hitNetwork(AppNetworkConstants.CHECK_CIAO_NUMBER, jsonObject);
				JSONObject responseJsonObject = new JSONObject(response);
				errorCode = responseJsonObject.getString("error_code");
				JSONObject resultJsonObject = responseJsonObject.getJSONObject("result");
				userJID =resultJsonObject.getString("user_jabber_id");
				new AddToRoster().execute();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(errorCode.equalsIgnoreCase("1")){
				userPhoneList=getPhoneNumberBean(bean,"Y");
				inviteTV.setVisibility(View.GONE);

			}else{
				userPhoneList = getPhoneNumberBean(bean,"N");
				inviteTV.setVisibility(View.VISIBLE);
			}
			isFromCallLog = true;
		}

	}

	public void setCallLimitForDialedNumber(String callRates) {
		this.callRates = callRates;
		setCallLimit(AppUtils.getAvailableCallLimit(this, callRates));	
	}


}
