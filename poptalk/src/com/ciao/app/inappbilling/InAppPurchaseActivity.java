

package com.ciao.app.inappbilling;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Window;

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.inappbilling.utils.IabHelper;
import com.ciao.app.inappbilling.utils.IabResult;
import com.ciao.app.inappbilling.utils.Inventory;
import com.ciao.app.inappbilling.utils.Purchase;
import com.ciao.app.netwrok.RequestBean;
import com.csipsimple.utils.Log;
import com.poptalk.app.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Activity is used for show the UI for in-app purchase and manage all the callback. 
 * @author Vivek Kumar(Appstudioz)
 *
 */
public class InAppPurchaseActivity extends Activity implements IabHelper.OnIabSetupFinishedListener,IabHelper.QueryInventoryFinishedListener,IabHelper.OnConsumeFinishedListener,IabHelper.OnIabPurchaseFinishedListener {

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;
	// The helper object
	private IabHelper mHelper;

	/** * Define the inapp product type like InApp or Subs	 */
	private String mInAppSKUType;
	/**	 * Define the product id	 */
	private String mSkuId;

	/** This is used for sending unique data at time of purchase and verify when data is purchase for security reason **/
	private String payload = "";
	/** Used for check the consume iteration count if once it is failed than check again  **/
	private int consumeIteration=0;
	/** This is used for managing the status of product purchase, When we move to consume the product which we have already owned <br/> if mPurchaseStatus=0 :- Only consuming product(Which already owned not purchase new product) <br/> else mPurchaseStatus=1:- First purchased product and than consume **/
	private int mPurchaseStatus=0;

	private Purchase mPurchaseResult;
	private RequestBean mRequestBean;
	private LoaderManager.LoaderCallbacks<String> creditUpdateCallback;

	private String confirmUrl = AppNetworkConstants.UPDEDE_CREDIT_FROM_INAPP;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_inapppurchase);
		mSkuId=getIntent().getExtras().getString(InAppConstants.INAPP_SKU_ID);
		mInAppSKUType=getIntent().getExtras().getString(InAppConstants.INAPP_SKU_TYPE);

		Log.d("APICall", "onCreate executed");
		if (TextUtils.isEmpty(AppProperties.BASE_64_KEY)) {
			setupResult(InAppConstants.ERROR_BASE_64_KEY_NOT_SETUP);
		}
		if (getPackageName().startsWith("com.ciao")) {
			setupResult(InAppConstants.ERROR_PACKAGE_NAME);
		}
		setupInAppPurhcase();
	}

	/**
	 * This method is used for setup InApp Setup on Device and check device is support InApp or not. 
	 */
	private void setupInAppPurhcase(){
		Log.d("APICall", "setupInAppPurhcase executed");
		mHelper = new IabHelper(this, AppProperties.BASE_64_KEY);
		// enable debug logging (for a production application, you should set this to false).
		mHelper.enableDebugLogging(true);
		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		mHelper.startSetup(this);
	}

	@Override
	public void onIabSetupFinished(IabResult result) {
		Logger.error("Setup finished.");
		if (!result.isSuccess()) {
			// Oh noes, there was a problem.
			setupResult(InAppConstants.ERROR_DEVICE_NOT_SUPPORT_INAPP);
		}else{
			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null) return;
			// IAB is fully set up. Now, let's get an inventory of stuff we own.
			Logger.error("Setup successful. Querying inventory.");
		
			mHelper.queryInventoryAsync(InAppPurchaseActivity.this);
		}
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
		Log.d("APICall", "onQueryInventoryFinished executed");
		if (mHelper == null) return;
		// Is it a failure?
		if (result.isFailure()) {
			setupResult(InAppConstants.ERROR_FAILED_QUERY_INVENTRY);
			return;
		}else{
			// Checking the InApp product type for consume(Only for inapp product not for subscription product) or not
			if(mInAppSKUType.equalsIgnoreCase(IabHelper.ITEM_TYPE_INAPP)){
				Purchase skuPurchase = inventory.getPurchase(mSkuId);
				if (skuPurchase != null && verifyDeveloperPayload(skuPurchase)) {
					// You have purchase this mSkuId but you did not consume it, if you need to purchase this item(mSkuId) for this you need to consume first after that you will purchase again.
					writeLogInSdcard(true,"\n\n\n\n  Product Which are not consume before:- \n"+ mSkuId+"\n\n skuSubsPurchase:- "+skuPurchase);
					mHelper.consumeAsync(inventory.getPurchase(mSkuId), InAppPurchaseActivity.this);
					return;
				}else{
					//Go For Purchase 
					mHelper.launchPurchaseFlow(this, mSkuId, RC_REQUEST,
							InAppPurchaseActivity.this, payload);
				}
			}else{ // For SubScription:--
				if (!mHelper.subscriptionsSupported()) {
					setupResult(InAppConstants.ERROR_DEVICE_NOT_SUPPORT_SUBS);
					return;
				}

				/* for security, generate your payload here for verification. See the comments on
				 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
				 *        an empty string, but on a production app you should carefully generate this. */
				Purchase skuSubsPurchase = inventory.getPurchase(mSkuId);
				writeLogInSdcard(true,"\n\n\n\n  skuSubsPurchase:- \n"+ mSkuId+"\n\n skuSubsPurchase:- "+skuSubsPurchase);
				if (skuSubsPurchase != null && verifyDeveloperPayload(skuSubsPurchase)) {
					//TODO Return back to activity for 
					setupResult(InAppConstants.RESULT_SUBS_CONTINUE);
				}else{
					//Go For Purchase 
					mHelper.launchPurchaseFlow(this,
							mSkuId, IabHelper.ITEM_TYPE_SUBS,
							RC_REQUEST, InAppPurchaseActivity.this, payload);
				}
			}
		}

		///Remove
		if (inventory.hasPurchase(mSkuId)) {
			mHelper.consumeAsync(inventory.getPurchase(mSkuId), null);
		}
	}

	/**
	 * Method is used for sending the status of the inapp purchase to caller activity
	 * @param type
	 */
	private void setupResult(final int type){
		Intent mIntent=new Intent();
		Bundle mBundle=new Bundle();
		mBundle.putInt("response_code", type);
		mBundle.putString(InAppConstants.INAPP_SKU_ID, mSkuId);
		mBundle.putString(InAppConstants.INAPP_SKU_TYPE, mInAppSKUType);
		if(type==InAppConstants.RESULT_PRODUCT_PURCHASE_CONSUME_SUCCESSFULLY) {
			mBundle.putString(InAppConstants.INAPP_PURCHASE_RESULT, mPurchaseResult.getOriginalJson().toString());
		}
		mIntent.putExtras(mBundle);
		setResult(RESULT_OK,mIntent);
		finish();
	}

	
	/**
	 *  Verifies the developer payload of a purchase.
	 * <br/><br/>
	 * verify that the developer payload of the purchase is correct. It will be
	 * the same one that you sent when initiating the purchase.
	 *<br/><br/>
	 * WARNING: Locally generating a random string when starting a purchase and
	 * verifying it here might seem like a good approach, but this will fail in the
	 * case where the user purchases an item on one device and then uses your app on
	 * a different device, because on the other device you will not have access to the
	 * random string you originally generated.
	 *<br/>
	 * So a good developer payload has these characteristics:
	 *<br/>
	 * 1. If two different users purchase an item, the payload is different between them,
	 *    so that one user's purchase can't be replayed to another user.
	 *<br/>
	 * 2. The payload must be such that you can verify it even when the app wasn't the
	 *    one who initiated the purchase flow (so that items purchased by the user on
	 *    one device work on other devices owned by the user).
	 *<br/><br/>
	 * Using your own server to store and verify developer payloads across app
	 * installations is recommended.
	 * @param p
	 * @return boolean 
	 */
	private boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();
		if(payload.equals(this.payload)){
			return true;
		}
		return false;
	}

	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
		Logger.error("Consumption finished. Purchase: " + purchase + ", result: " + result);
		writeLogInSdcard(true, "\n\n\n\n onConsumeFinished:- \n" + result.toString() + "\n\n Purchase:- " + purchase);

        mPurchaseResult = purchase;
		// if we were disposed of in the meantime, quit.
		if (mHelper == null) return;

		if (result.isSuccess()) {
			// successfully consumed, so we apply the effects of the item in our
			//TODO Send the callback for calling activity/fragment

			if(mPurchaseStatus==0){
				setupResult(InAppConstants.RESULT_PRODUCT_CONSUME_SUCCESSFULLY);
			}else{
				setupResult(InAppConstants.RESULT_PRODUCT_PURCHASE_CONSUME_SUCCESSFULLY);
			}
		}
		else {
			if(consumeIteration==0){
				consumeIteration++;
				mHelper.consumeAsync(purchase, InAppPurchaseActivity.this);
			}else{
				//Send the call back when Item is not consume
				setupResult(InAppConstants.RESULT_PROPUR_SUCC_CONSUME_FAIL);
			}
		}
	}

	/*
	Init callback function for credit update.
 *
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
							AppSharedPreference.getInstance(InAppPurchaseActivity.this).setTotalCredit(serverResponseJsonObject.getString("current_credits"));
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
	}*/
	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
		writeLogInSdcard(true,"OnIabPurchaseFinish:- \n"+ result.toString()+"\n\n Purchase:- "+purchase);
		// if we were disposed of in the meantime, quit.
		if (mHelper == null) return;

		if (result.isFailure()) {
			setupResult(InAppConstants.ERROR_PRODUCT_PURCHASE);
			return;
		}
		if (!verifyDeveloperPayload(purchase)) {
			setupResult(InAppConstants.ERROR_FAILED_AUTHENTICATION_PRODUCT_PURCHASE);
			return;
		}

		Logger.info("Purchase successful.");


		if(purchase.getItemType().equalsIgnoreCase(IabHelper.ITEM_TYPE_INAPP)){
			mPurchaseStatus++;
			mHelper.consumeAsync(purchase, InAppPurchaseActivity.this);

			/*Intent mIntent=new Intent();
			Bundle mBundle=new Bundle();
			if(mPurchaseStatus==0){
				mBundle.putInt("response_code", InAppConstants.RESULT_PRODUCT_CONSUME_SUCCESSFULLY);
			}else{
				mBundle.putInt("response_code", InAppConstants.RESULT_PRODUCT_PURCHASE_SUCCESSFULLY);
			}
			mBundle.putString(InAppConstants.INAPP_SKU_ID, mSkuId);
			mBundle.putString(InAppConstants.INAPP_SKU_TYPE,mInAppSKUType);
			mIntent.putExtras(mBundle);
			setResult(RESULT_OK,mIntent);
			finish();*/
		}else{  //TODO Subs Item Purchased
			setupResult(InAppConstants.RESULT_SUBS_CONTINUE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.error("onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if (mHelper == null) return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
		else {
			Logger.error("onActivityResult handled by IABUtil.");
		}
	}

	private void writeLogInSdcard(final boolean isAppend,final String text){
		new Thread(){
			public void run() {
				try {
					File myFile = new File(Environment.getExternalStorageDirectory()+"/inappPurchase.txt");
					//BufferedWriter for performance, true to set append to file flag 
					BufferedWriter buf = new BufferedWriter(new FileWriter(myFile, true)); 
					buf.append(text);
					buf.newLine();
					buf.close();
				} 
				catch (IOException e)
				{ 
					// TODO Auto-generated catch block 
					e.printStackTrace();
				} 

			}
		}.start();

	}

	@Override
	public void onBackPressed() {
	}
}
