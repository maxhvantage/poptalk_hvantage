package com.ciao.app.views.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ciao.app.adapters.PurchaseItemAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.datamodel.PurchaseItem;
import com.ciao.app.inappbilling.InAppConstants;
import com.ciao.app.inappbilling.InAppPurchaseActivity;
import com.ciao.app.inappbilling.SkuIds;
import com.ciao.app.inappbilling.utils.IabHelper;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.CreditUpdateTaskLoader;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajat on 9/2/15.
 * This activity show the ui where user can purchase credit packages.
 * Right now all the packages are dummy.
 * We have to create packages on google play developer console and enter items sku_ids here
 */
public class BuyItemsActivity extends Activity {
    private ListView purchaseItemLV;
    private List<PurchaseItem> purchaseItemsList;
    private static final int IN_APP_PURCHASE = 1;
    private TextView headerTV;
    private TextView totalCreditTV;
    private String screenFromCode = "0";


    private LoaderManager.LoaderCallbacks<String> creditUpdateCallback;
    private RequestBean mRequestBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_item);
        purchaseItemsList = new ArrayList<PurchaseItem>();
        purchaseItemLV = (ListView)findViewById(R.id.lv_purchase_item);
        headerTV = (TextView)findViewById(R.id.tv_buy_item_header);
        totalCreditTV = (TextView)findViewById(R.id.tv_total_credits);
        totalCreditTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());
        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle =  intent.getExtras();
            if(bundle!=null){
                screenFromCode =  bundle.getString("screen_id");
            }
        }
        if(screenFromCode.equalsIgnoreCase("0")){
            headerTV.setText(getString(R.string.txt_purchase_item));
            purchaseItemsList.add(new PurchaseItem("Virtual Number + 100 credits","$5.99"));
            purchaseItemsList.add(new PurchaseItem("Virtual Number + 250 credits","$7.99"));
            purchaseItemsList.add(new PurchaseItem("Virtual Number + 500 credits","$9.99"));
        }else {
            headerTV.setText(getString(R.string.txt_credit_item));
            purchaseItemsList.add(new PurchaseItem("Buy 350 Credits","$1.99"));
            purchaseItemsList.add(new PurchaseItem("Buy 1000 Credits","$4.99"));
            purchaseItemsList.add(new PurchaseItem("Buy 2500 Credits","$9.99"));
        }
        purchaseItemLV.setAdapter(new PurchaseItemAdapter(this, R.layout.row_buy_items, purchaseItemsList));
        // init the callback function of the credit update.
        initLoader();
    }

    public void goToPreviousScreen(View view){
        finish();
    }

    public void buyItem(int position){
        String Sku = SkuIds.SKU_INAPP_CALL_TEST;
        Log.d("Product","position is "+position);
        Log.d("APICall","BuyItem executed");
        switch(position){
            case 0:
                //This is test product
                //Sku = SkuIds.SKU_INAPP_10;
                //This is the production product
                Sku = SkuIds.SKU_INAPP_CALL_350;
                break;
            case 1:
                Sku = SkuIds.SKU_INAPP_CALL_1000;
                break;
            case 2:
                Sku = SkuIds.SKU_INAPP_CALL_2500;
                break;
        }
        Intent mIntent=new Intent(this, InAppPurchaseActivity.class);
        mIntent.putExtra(InAppConstants.INAPP_SKU_ID, Sku);
        mIntent.putExtra(InAppConstants.INAPP_SKU_TYPE, IabHelper.ITEM_TYPE_INAPP);
        startActivityForResult(mIntent, IN_APP_PURCHASE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("APICall", "response_code: " + data.getExtras().getInt("response_code"));
        Log.d("APICall", "onActivityResult on BuyItemsActivity executed with requestCode: " + requestCode + " - resultCode: " + resultCode + " - data: " + data.getExtras().getString(InAppConstants.INAPP_PURCHASE_RESULT));


        //Toast.makeText(BuyItemsActivity.this, "onActivityResult response code is "+requestCode,Toast.LENGTH_SHORT).show();

        switch (requestCode) {
            case IN_APP_PURCHASE:
                if(resultCode==Activity.RESULT_OK){
                    String mInapSkuType=data.getExtras().getString(InAppConstants.INAPP_SKU_TYPE);
                    String mInapSkuId=data.getExtras().getString(InAppConstants.INAPP_SKU_ID);
                    String mPurchaseResult = data.getExtras().getString(InAppConstants.INAPP_PURCHASE_RESULT);

                    int value=data.getExtras().getInt("response_code");

                    //Toast.makeText(BuyItemsActivity.this, "onActivityResult request code is "+requestCode+" - and value is "+value,Toast.LENGTH_LONG).show();
                    switch (value) {
                        case InAppConstants.RESULT_PRODUCT_CONSUME_SUCCESSFULLY:
                            responseAlertDialog("You have successfully consume "+mInapSkuId+" product.");
                            break;
                        case InAppConstants.RESULT_PRODUCT_PURCHASE_CONSUME_SUCCESSFULLY:

//                            responseAlertDialog("You have successfully purchase "+mInapSkuId+" product.");

                            try {
                                JSONObject objPurchase = new JSONObject(mPurchaseResult);
                                updateCreditPurchase(objPurchase);
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

                    //Toast.makeText(BuyItemsActivity.this, "creditUpdateCallback on try "+data,Toast.LENGTH_LONG).show();
                    android.util.Log.e("Return Value :", data);
                    try {
                        JSONObject serverResponseJsonObject = new JSONObject(data);
                        Log.d("APICall", "OnLoadFinished creditUpdateCallBack -- "+serverResponseJsonObject.toString());
                        boolean valid_flag = serverResponseJsonObject.getBoolean("valid_purchase");
                        Log.d("APICall : ","update credit purchase on creditUpdateCallback, valid is or not : "+String.valueOf(valid_flag));

                        //Toast.makeText(BuyItemsActivity.this, "creditUpdateCallback is valid "+valid_flag,Toast.LENGTH_LONG).show();
                        if (valid_flag) {
                            Toast.makeText(BuyItemsActivity.this, "creditUpdateCallback updated credits",Toast.LENGTH_LONG).show();
                            AppSharedPreference.getInstance(BuyItemsActivity.this).setTotalCredit(serverResponseJsonObject.getString("current_credits"));
                            totalCreditTV.setText(AppSharedPreference.getInstance(BuyItemsActivity.this).getTotalCredit());

//                            AppUtils.showTost(mRequestBean.getActivity(), "Update the credit : " + serverResponseJsonObject.getString("current_credits"));
                            AppSharedPreference.getInstance(BuyItemsActivity.this).setAdLockScreenVisibility(true);
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

        Log.d("APICall", "updateCreditPurchase executed with response "+purchaseResult.toString());
        Log.d("Purchase sucessed.", "Purchase executed : " + purchaseResult.toString());

        if (!AppSharedPreference.getInstance(BuyItemsActivity.this).getIsConnectedToInternet()){
            DialogUtils.showInternetAlertDialog(BuyItemsActivity.this);
            return;
        }

        //Toast.makeText(BuyItemsActivity.this, "updateCreditPurchase purchaseResult is "+purchaseResult.toString(),Toast.LENGTH_LONG).show();

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("user_id", AppSharedPreference.getInstance(this).getUserID());
            jsonObject.put("packageName", purchaseResult.getString("packageName"));
            jsonObject.put("orderId", purchaseResult.getString("orderId"));
            jsonObject.put("productId", purchaseResult.getString("productId"));
            //jsonObject.put("purchaseTime", purchaseResult.getString("purchaseTime"));
            //jsonObject.put("purchaseState", purchaseResult.getString("purchaseState"));
            jsonObject.put("purchaseToken", purchaseResult.getString("purchaseToken"));


            Log.e("purchased Result : ", jsonObject.toString());


            mRequestBean = new RequestBean();
            mRequestBean.setActivity(this);
            mRequestBean.setLoader(true);
            mRequestBean.setJsonObject(jsonObject);

            //Toast.makeText(BuyItemsActivity.this, "updateCreditPurchase on try",Toast.LENGTH_LONG).show();
            getLoaderManager().restartLoader(0, null, creditUpdateCallback);
        } catch (JSONException e) {
            //Toast.makeText(BuyItemsActivity.this, "updateCreditPurchase on catch with error: "+e.toString(),Toast.LENGTH_LONG).show();
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

}
