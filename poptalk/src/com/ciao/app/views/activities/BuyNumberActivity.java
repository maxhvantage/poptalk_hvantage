package com.ciao.app.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.BaseResponseBean;
import com.ciao.app.datamodel.NumberResponseBean;
import com.ciao.app.netwrok.backgroundtasks.BuyNumberAsyncTask;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajat on 23/2/15.
 * This screen will show the ui where user can secure his poptalk number for free.
 */
public class BuyNumberActivity extends Activity {

	private String numberSelected;
	private String numberBuyUrl = AppNetworkConstants.BUY_NUMBER_URL;//"/api/auth/nexmo-number-buy"
	private List<String> countryList = new ArrayList<String>();
	private TextView selectedNumberTV,tncTV;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_number);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		selectedNumberTV = (TextView)findViewById(R.id.tv_number);
		/*tncTV = (TextView)findViewById(R.id.tv_tnc);
		tncTV.setText(Html.fromHtml("<a href=\"\">I agree to the Terms and Conditions.</a>"));
		tncTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(BuyNumberActivity.this,TermsAndConditionActivity.class));
			}
		});
		/*callNumberSearch();

		countryList.add("US");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countryList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 */
		((Button) findViewById(R.id.btn_buy_number)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			if (numberSelected != null) {
				numberSelected = AppUtils.parseFormattedNumber(numberSelected);
				numberSelected = numberSelected.replace("+", "");
				callBuyNumber();
			} else {
				AppUtils.showTost(BuyNumberActivity.this, "Select a number before claim.");
			}

			}
		});

		/*((Spinner) findViewById(R.id.sp_available_number)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
				// TODO Auto-generated method stub
				numberSelected = parent.getItemAtPosition(pos).toString();
				//AppUtils.showTost(BuyNumberActivity.this, numberSelected);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/
	}

	/**
	 * Method used to hit api for Buy Number
	 */
	private void callBuyNumber() {

		// if network is not enable. go to the phone setting for the network enable.
		if (!AppSharedPreference.getInstance(BuyNumberActivity.this).getIsConnectedToInternet()){
			DialogUtils.showInternetAlertDialog(BuyNumberActivity.this);
			return;
		}

		try {

			numberSelected = AppUtils.parseFormattedNumber(numberSelected);
			numberSelected = numberSelected.replace("+", "");
			new BuyNumberAsyncTask(this, numberSelected, numberBuyUrl).execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method call after successful response and parsing
	 */
	public void success(NumberResponseBean responseBean) {
		//bind Number
		/*List<String> list = responseBean.getBeanlist();
		if (list != null && list.size() > 0) {
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BuyNumberActivity.this, android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			((Spinner) findViewById(R.id.sp_available_number)).setAdapter(dataAdapter);
		}*/
	}


	/**
	 * Method call after successful response and parsing
	 */
	public void onSuccess(BaseResponseBean responseBean) {
		//AppUtils.showTost(BuyNumberActivity.this, responseBean.getmExceptionName());
		ApplicationDAO.getInstance(BuyNumberActivity.this).saveMyCioaNumberToDb(numberSelected);
		AppSharedPreference.getInstance(BuyNumberActivity.this).setUserCiaoNumber(numberSelected);
		finish();
	}


	/**
	 * Method call after error in response and parsing
	 */
	public void error(String error) {
		//AppUtils.showTost(BuyNumberActivity.this, error);
		//    	 AppUtils.showTost(BuyNumberActivity.this, responseBean.getmExceptionName());
		//       AppSharedPreference.getInstance(BuyNumberActivity.this).setUserCiaoNumber(numberSelected);

	}

//	private void gotoCallScreen() {
//		Intent intent = new Intent(this, CallActivity.class);
//		startActivity(intent);
//		Intent backIntent = new Intent();
//		setResult(RESULT_OK, backIntent);
//		finish();
//
//	}

	// Navigate to screen where user can search number of their interest 
	public void gotoSelectNumberScreen(View view){
		Intent intent = new Intent(this,SearchAndSelectNumber.class);
		startActivityForResult(intent, 1001);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1001){
			if(data!=null){
				numberSelected = data.getExtras().getString("selected_no");
				if(numberSelected!=null){
					String tempFormat = numberSelected.substring(numberSelected.length()-14, numberSelected.length());
					selectedNumberTV.setText(tempFormat);
				}	
			}
			
			
		}
	}
	
	 public void goToPreviousScreen(View view){
		 finish();
	 }
}
