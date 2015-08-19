package com.ciao.app.views.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ciao.app.adapters.SMSAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.netwrok.backgroundtasks.GetCallLimitAndCharge;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.ciao.inapp.message.SMSResultReciever;
import com.ciao.inapp.message.SendSMSIntentService;
import com.csipsimple.ui.prefs.CallLimitPreference;
import com.csipsimple.utils.CallBaseActivity;
import com.poptalk.app.R;


/*
 * This screen show the sms window ui where user can select sms to selected contact fro list or user can simply enter the recipient number along wiht country code.
 */
public class NewSmsActivity extends CallBaseActivity implements SMSResultReciever.Receiver,LoaderManager.LoaderCallbacks<Cursor>{
	private EditText toEt;
	private ImageView addFromContactIV,callIV;
	private RelativeLayout toContactRl;
	private TextView userNameTV,userContactTV;
	private String userName,userPic,userNumber;
	private SMSResultReciever mSmsResultReceiver;
	private String toNumber,fromNumber,messageBody,userId;
	private EditText mSmsBoxET;
	private ListView smsLV;
	private SMSAdapter mSMSAdapter;
	private LinearLayout countryLL;
	private TextView countryNameTV,countryCodeTV;
	private String countryCode = "+1";
	private boolean addedFromContact = false;
	private String callRates,smsRates;
	private boolean isNumber;

	private int requestCode=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_sms);
		Intent intent = getIntent();

		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
		userNameTV = (TextView)findViewById(R.id.tv_message_contact_name);
		mSmsBoxET = (EditText)findViewById(R.id.et_sms_txt);
		smsLV = (ListView)findViewById(R.id.lv_sms);
		toEt = (EditText)findViewById(R.id.et_to);
		addFromContactIV = (ImageView)findViewById(R.id.iv_add_from_list);
		callIV = (ImageView)findViewById(R.id.iv_call);
		toContactRl = (RelativeLayout)findViewById(R.id.rl_add_contct);
		mSmsBoxET.setTypeface(type);
		mSmsResultReceiver = new SMSResultReciever(new Handler());
		mSmsResultReceiver.setReceiver(this);
		userContactTV = (TextView)findViewById(R.id.tv_message_contact_number);

		countryCodeTV = (TextView)findViewById(R.id.tv_country_code);
		countryNameTV = (TextView)findViewById(R.id.tv_country_name);

		toEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					String number = toEt.getText().toString().trim();
					number = number.replace("+", "");
					if (number.length() > 10) {
						AppUtils.showTost(NewSmsActivity.this, "Please enter a valid number");
					} else {
						setTextToHeader(countryCode + number);
					}

				}
				return false;
			}


		});
		toEt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (!addedFromContact) {
						String number = toEt.getText().toString().trim();
						number = number.replace("+", "");
						if (number.length() > 10) {
							AppUtils.showTost(NewSmsActivity.this, "Please enter a valid number");
						} else {
							setTextToHeader(countryCode + number);
						}
					}

				} else {
					addedFromContact = false;
				}


			}
		});
		addFromContactIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewSmsActivity.this, InterstialContactListActivity.class);
				intent.putExtra("operation", AppConstants.CONTACT_MESSAGE);
				startActivityForResult(intent, 1001);
			}
		});
		if(intent!=null){
			Bundle bundle =  intent.getExtras();
			if(bundle!=null){
				userId =  bundle.getString("_id");
				userName = bundle.getString("_name");
				userPic = bundle.getString("_pic");
				userNumber = ApplicationDAO.getInstance(this).getUserPhoneNumber(userId);
				if(userNumber!=null){
					userContactTV.setText(AppUtils.formatNumberUsingParenthesesforCallLog(userNumber, this));	
					userContactTV.setVisibility(View.VISIBLE);	
				}else{
					userContactTV.setVisibility(View.GONE);		
				}
				userNameTV.setText(userName);
				countryCode = AppUtils.getCountryCodeFromNumber(userNumber);
				countryCode = "+"+countryCode;
				callIV.setVisibility(View.VISIBLE);
				ApplicationDAO.getInstance(NewSmsActivity.this).updateUnreadSMSCount(userNumber);
				if(userId.length()<10){
					isNumber = false;
				}else{
					isNumber = true;
				}
			}
		}
		toNumber = userNumber;

		if(toNumber!=null){
			toContactRl.setVisibility(View.GONE);
			mSMSAdapter = new SMSAdapter(this, null, toNumber);
			smsLV.setAdapter(mSMSAdapter);
			getLoaderManager().restartLoader(0, null, this);

			if (!AppSharedPreference.getInstance(NewSmsActivity.this).getIsConnectedToInternet()){
				DialogUtils.showInternetAlertDialog(NewSmsActivity.this);
				return;
			}

			new GetCallLimitAndCharge(this, getCountryCode(toNumber), NewSmsActivity.this, toNumber).execute();
		}

		countryLL = (LinearLayout)findViewById(R.id.ll_country);
		countryLL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewSmsActivity.this,CountrySelectionActivity.class);
				startActivityForResult(intent, requestCode);
			}
		});
	}



	private void setTextToHeader(String number) {
		userId =ApplicationDAO.getInstance(this).getUserIdFromNumber(number);
		userName = ApplicationDAO.getInstance(this).getUserNameFromUserId(userId);
		if(userId!=null){
			isNumber = false;
		}else{
			userId = number;
			isNumber = true;
		}
		if(userName!=null){
			userNameTV.setText(userName);
			userContactTV.setText(AppUtils.formatNumberUsingParenthesesforCallLog(number, this));
			userContactTV.setVisibility(View.VISIBLE);	
		}else{
			userId = number;
			userNameTV.setText(number);
		}
		toContactRl.setVisibility(View.GONE);
		callIV.setVisibility(View.VISIBLE);
		toNumber = userNumber =number;
		mSMSAdapter = new SMSAdapter(this, null, toNumber);
		smsLV.setAdapter(mSMSAdapter);
		getLoaderManager().restartLoader(0, null, this);
		new GetCallLimitAndCharge(this, getCountryCode(toNumber), NewSmsActivity.this, toNumber).execute();

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 1) {
			//Country selected from Country selection screen.
			countryCode = data.getStringExtra("_countrycode");
			countryCodeTV.setText(data.getStringExtra("_countrycode"));
			countryNameTV.setText(data.getStringExtra("_countryname"));
		}
		if(requestCode==1001) {
			if(resultCode==RESULT_OK){
				if(data!=null){

					userId=data.getStringExtra("_id");
					userName =data.getStringExtra("_name");
					userPic=data.getStringExtra("_pic");
					toNumber = ApplicationDAO.getInstance(this).getUserCiaoRegisteredPhoneNumber(userId);
					if(toNumber.equalsIgnoreCase(userId)){
						toNumber = ApplicationDAO.getInstance(this).getUserPhoneNumber(userId);
					}
					userNameTV.setText(userName);
					addedFromContact = true;
					toEt.setText(toNumber);
					toContactRl.setVisibility(View.GONE);
					userContactTV.setText(AppUtils.formatNumberUsingParenthesesforCallLog(toNumber, this));
					callIV.setVisibility(View.VISIBLE);
					userContactTV.setVisibility(View.VISIBLE);
					mSMSAdapter = new SMSAdapter(this, null, toNumber);
					smsLV.setAdapter(mSMSAdapter);
					getLoaderManager().restartLoader(0, null, this);
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppSharedPreference.getInstance(this).setSMSSceenVisbility(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppSharedPreference.getInstance(this).setSMSSceenVisbility(true);

		fromNumber = AppSharedPreference.getInstance(this).getUserCiaoNumber();
		//AppUtils.handleCreditForCall(this, countryCode);
		if(AppSharedPreference.getInstance(this).getIsCaioOutCall()){
			if(callRates!=null){
				AppUtils.handleCreditForCall(this, callRates);
			}
		}
	}
	public void goToPreviousScreen(View view){
		finish();
	}
	/*
	 * Send sms on click 
	 */
	public void sendSMS(View view){
		if(NetworkStatus.isConected(this)){
			messageBody= mSmsBoxET.getText().toString();
			if(AppSharedPreference.getInstance(NewSmsActivity.this).getUserCiaoNumber()==null) {
				startActivity(new Intent(NewSmsActivity.this,BuyNumberActivity.class));
			} else if(messageBody.length()>0){
				mSmsBoxET.setText("");
				Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SendSMSIntentService.class);
				intent.putExtra("receiver", mSmsResultReceiver);
				intent.putExtra("to", toNumber);
				intent.putExtra("from", fromNumber);
				intent.putExtra("text", messageBody);

				startService(intent);
				long time = System.currentTimeMillis();
				ApplicationDAO.getInstance(this).saveSmsInDb(toNumber, messageBody, AppConstants.SMS_SENT,AppConstants.SMS_SENT,false,time);
				AppUtils.playChatBubbleSound(this);
			}else{
				AppUtils.showTost(this, "Please enter your message first.");
			}
		}else{
			AppUtils.showTost(this, "Please check your internet connection.");
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String appUserID = AppSharedPreference.getInstance(this).getUserID();
		return new CursorLoader(this, Uri.parse(AppDatabaseConstants.CONTENT_URI_SMS_DETAIL),null,AppDatabaseConstants.USER_CHAT_ID + " = ? AND "+AppDatabaseConstants.KEY_APP_USER_ID+" = ? ", new String[]{toNumber,appUserID},null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mSMSAdapter.swapCursor(data);
		smsLV.post(new Runnable() {
			@Override
			public void run() {
				smsLV.setSelection(mSMSAdapter.getCount());
			}
		});

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mSMSAdapter.swapCursor(null);

	}
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {

		switch (resultCode) {
		case SendSMSIntentService.STATUS_RUNNING:
			setProgressBarIndeterminateVisibility(true);
			break;
		case SendSMSIntentService.STATUS_ERROR:
			//AppUtils.showTost(this, resultData.getString("result"));
			break;
		case SendSMSIntentService.STATUS_FINISHED:
			//setProgressBarIndeterminateVisibility(true);
			AppUtils.handleCreditForSMS(this, smsRates,Integer.parseInt(resultData.getString("result")));
			break;
		default:
			break;
		}

	}

	public void setCallLimit(long availbleCallSec) {
		CallLimitPreference.getInstance(this).setCallLimit(availbleCallSec);
	}

	public void makeCall(View view){
		if(NetworkStatus.isConected(this)){
			AppSharedPreference.getInstance(this).setIsCaioOutCall(true);
			new GetCallLimitAndCharge(this, getCountryCode(userNumber), NewSmsActivity.this, userNumber).execute();
			placeCallWithOption(null, false, userNumber);

		}else{
			AppUtils.showTost(this, "Please check you internet connection");	
		}
	}

	public void setCallLimitForDialedNumber(String callRates,String smsRates) {
		this.callRates = callRates;
		this.smsRates = smsRates;
		if(callRates!=null){
			setCallLimit(AppUtils.getAvailableCallLimit(this, callRates));
		}
	}
	private String getCountryCode(String userNumber ) {
		return AppUtils.getCountryCodeFromNumber(userNumber);
	}
	public void goToContactDetailScreen(View view){
        if(userId!=null){
        	Intent intent = new Intent(this, ContactDetailsActivity.class);
    		intent.putExtra("from_sms", true);
    		intent.putExtra("is_number", isNumber);
    		intent.putExtra("_id",userId);
    		intent.putExtra("_name",userName);
    		intent.putExtra("_pic",userPic);
    		startActivity(intent);	
        }
		
	}

}
