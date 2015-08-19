package com.ciao.app.views.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;


import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.inappbilling.InAppConstants;
import com.ciao.app.inappbilling.InAppPurchaseActivity;
import com.ciao.app.inappbilling.Logger;
import com.ciao.app.inappbilling.SkuIds;
import com.ciao.app.inappbilling.utils.IabHelper;
import com.ciao.app.inappbilling.utils.Purchase;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.CreditUpdateTaskLoader;
import com.ciao.app.utils.DialogUtils;
import com.csipsimple.utils.Log;
import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.BaseResponseBean;
import com.ciao.app.netwrok.backgroundtasks.BuyNumberAsyncTask;
import com.ciao.app.utils.AppUtils;
import com.csipsimple.ui.SipHome;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.TextView.*;

public class PurchasePhoneNumberActivity extends SipHome {
	TextView tv_selected_no,tv_hint_suggestion_for_purchase;
	private String ciaoNumber;
	private String countryCode;
	private String numberBuyUrl = AppNetworkConstants.BUY_NUMBER_URL;
	private static final int IN_APP_PURCHASE = 1;

	private LoaderManager.LoaderCallbacks<String> creditUpdateCallback;
	private RequestBean mRequestBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_phone_number);
		tv_selected_no= (TextView) findViewById(R.id.tv_selected_no);
		tv_hint_suggestion_for_purchase= (TextView) findViewById(R.id.tv_hint_suggestion_for_purchase);
		Intent in=getIntent();
		String text = "You will get this number for free if you purchase <font color=\"#0084ff\">500 </font> calling credits</font>";
		tv_hint_suggestion_for_purchase.setText(Html.fromHtml(text), BufferType.SPANNABLE);
		String text2 = "For only <font color=\"#0084ff\">100 </font> credits</font>";
		ciaoNumber =in.getStringExtra("selected_no");
		countryCode = in.getStringExtra("country_code");
		tv_selected_no.setText(ciaoNumber);

		// init the callback function of the credit update.
		initLoader();

	}


	public void goToPreviousScreen(View view)
	{
		finish();
	}

	public void purchaseAnotherCiaoNumber(View view){
		String valueStr = AppSharedPreference.getInstance(this).getTotalCredit();

		int totalcredit = Integer.parseInt(valueStr);

		if (totalcredit < 1000)
		{
			creditAlertDlg().show();
		}
		else
		{
			ciaoNumber = AppUtils.parseFormattedNumber(ciaoNumber);
			ciaoNumber = ciaoNumber.replace("+", "");
			new BuyNumberAsyncTask(this, ciaoNumber,countryCode,numberBuyUrl).execute();
		}

	}

	public void  purchaseCredits (View view)
	{

		Intent mIntent=new Intent(this, InAppPurchaseActivity.class);
		mIntent.putExtra(InAppConstants.INAPP_SKU_ID, SkuIds.SKU_INAPP_CALL_1000);
		mIntent.putExtra(InAppConstants.INAPP_SKU_TYPE, IabHelper.ITEM_TYPE_INAPP);
		startActivityForResult(mIntent, IN_APP_PURCHASE);
	}

	/**
	 * Method call after successful response and parsing
	 */
	public void onSuccess(BaseResponseBean responseBean) {
		ApplicationDAO.getInstance(this).saveMyCioaNumberToDb(ciaoNumber);
		gotoCallScreen();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		switch (requestCode) {
			case IN_APP_PURCHASE:
				if(resultCode==Activity.RESULT_OK){
					String mInapSkuType=data.getExtras().getString(InAppConstants.INAPP_SKU_TYPE);
					String mInapSkuId=data.getExtras().getString(InAppConstants.INAPP_SKU_ID);
					String mPurchaseResult = data.getExtras().getString(InAppConstants.INAPP_PURCHASE_RESULT);

					int value=data.getExtras().getInt("response_code");

					switch (value) {
						case InAppConstants.RESULT_PRODUCT_CONSUME_SUCCESSFULLY:
							responseAlertDialog("You have successfully consume "+mInapSkuId+" product.");
							break;
						case InAppConstants.RESULT_PRODUCT_PURCHASE_CONSUME_SUCCESSFULLY:
//							responseAlertDialog("You have successfully purchase "+mInapSkuId+" product.");
//							AppSharedPreference.getInstance(this).setAdLockScreenVisibility(true);

							try {
								JSONObject objPuchase = new JSONObject(mPurchaseResult);
								updateCreditPurchase(objPuchase);
							} catch (JSONException e) {
								e.printStackTrace();
							}

							break;
						case InAppConstants.RESULT_PROPUR_SUCC_CONSUME_FAIL:
							responseAlertDialog("You have failed to consume "+mInapSkuId+" product.");
							break;
						case InAppConstants.RESULT_SUBS_CONTINUE:
							responseAlertDialog("Your subsription is continue for id "+mInapSkuId+" product.");
							break;
						case InAppConstants.ERROR_DEVICE_NOT_SUPPORT_SUBS:
							responseAlertDialog("Your devices does not support subscription.");
							break;
						case InAppConstants.ERROR_BASE_64_KEY_NOT_SETUP:
							responseAlertDialog("Please setup your app Base 64 KEY for InApp Purchase.");
							break;
						case InAppConstants.ERROR_PACKAGE_NAME:
							responseAlertDialog("Your application package name can not start with com.example");
							break;
						case InAppConstants.ERROR_DEVICE_NOT_SUPPORT_INAPP:
							responseAlertDialog("Your devices does not support inapp purchase.");
							break;
						case InAppConstants.ERROR_PRODUCT_PURCHASE:
							responseAlertDialog("Error is occured in purchasing.");
							break;
						default:
							responseAlertDialog("Error is occured "+value);
							break;
					}
				}
				break;
			default:
				break;
		}
	}


	/*
	Init callback function for credit update.
 */
	private void initLoader() {

		creditUpdateCallback = new LoaderManager.LoaderCallbacks<String>() {
			@Override
			public Loader<String> onCreateLoader(int id, Bundle args) {
				return new CreditUpdateTaskLoader(mRequestBean);
			}

			@Override
			public void onLoadFinished(Loader<String> loader, String data) {
				if (loader instanceof CreditUpdateTaskLoader) {
					((CreditUpdateTaskLoader) loader).hideLoaderDialog();

					try {
						JSONObject serverResponseJsonObject = new JSONObject(data);
						Log.d("ServerResponseJsonObject", serverResponseJsonObject.toString());
						boolean valid_flag = serverResponseJsonObject.getBoolean("valid_purchase");
						Log.d("Purchase Credit : ","update credit purchase on creditUpdateCallback, valid is or not : "+String.valueOf(valid_flag));
						if (valid_flag) {
							AppSharedPreference.getInstance(PurchasePhoneNumberActivity.this).setTotalCredit(serverResponseJsonObject.getString("current_credits"));
							AppUtils.showTost(mRequestBean.getActivity(), "Update the credit : " + serverResponseJsonObject.getString("current_credits"));

						} else {
							//Login failed
							AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("current_credits"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onLoaderReset(Loader<String> loader) {

			}
		};
	}

	// Get user name and password from respective field and hit api to validate user credentials
	private void updateCreditPurchase(JSONObject purchaseResult) {

		Log.d("Purchase sucessed.", "Purchase executed");

		if (!AppSharedPreference.getInstance(PurchasePhoneNumberActivity.this).getIsConnectedToInternet()){
			DialogUtils.showInternetAlertDialog(PurchasePhoneNumberActivity.this);
			return;
		}

		JSONObject jsonObject = new JSONObject();

		try {

			jsonObject.put("user_id", AppSharedPreference.getInstance(this).getUserID());
			jsonObject.put("packageName", purchaseResult.getString("packageName"));
			jsonObject.put("orderId", purchaseResult.getString("orderId"));
			jsonObject.put("productId", purchaseResult.getString("productId"));
			jsonObject.put("developerPayload", purchaseResult.getString("developerPayload"));
			jsonObject.put("purchaseTime", purchaseResult.getString("purchaseTime"));
			jsonObject.put("purchaseState", purchaseResult.getString("purchaseState"));
			jsonObject.put("purchaseToken", purchaseResult.getString("purchaseToken"));


			Log.e("purchased Result : ", jsonObject.toString());

			mRequestBean = new RequestBean();
			mRequestBean.setActivity(this);
			mRequestBean.setLoader(true);
			mRequestBean.setJsonObject(jsonObject);

			getLoaderManager().restartLoader(0, null, creditUpdateCallback);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show the InApp purchase status dialog
	 * @param message
	 * @param type
	 */
	private void responseAlertDialog(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setCancelable(false);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			finish();
			}
		});
		bld.create().show();
	}

	/**
	 * Method call after error in response and parsing
	 */
	public void error(String error) {
		AppUtils.showTost(this, error);
	}

	private void gotoCallScreen() {

		Intent intent = new Intent(this, CallActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);

	}


	private AlertDialog creditAlertDlg()
	{
		final AlertDialog.Builder buyCreditDlg = new AlertDialog.Builder(this, R.style.ThemeDialogCustom);
		buyCreditDlg.setIcon(R.drawable.logo_2);
		buyCreditDlg.setTitle("Insufficient credits");
		buyCreditDlg.setMessage("We are sorry but you don't have enough credits to buy a phone number! Would you like to buy more credits now?");

		buyCreditDlg.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(PurchasePhoneNumberActivity.this, BuyItemsActivity.class);
				intent.putExtra("screen_id", "1");
				startActivity(intent);
			}
		});
		buyCreditDlg.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		AlertDialog dlg = buyCreditDlg.create();
		return  dlg;
	}


}
