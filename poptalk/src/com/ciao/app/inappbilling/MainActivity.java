package com.ciao.app.inappbilling;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.poptalk.app.R;
import com.ciao.app.inappbilling.utils.IabHelper;

/**
 * Created by rajat on 9/2/15.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button1:
            {
                Intent mIntent=new Intent(MainActivity.this, InAppPurchaseActivity.class);
                mIntent.putExtra(InAppConstants.INAPP_SKU_ID, SkuIds.SKU_INAPP_5);
                mIntent.putExtra(InAppConstants.INAPP_SKU_TYPE, IabHelper.ITEM_TYPE_INAPP);
                startActivityForResult(mIntent, 101);
            }
            break;
            case R.id.button2:
            {
                Intent mIntent=new Intent(MainActivity.this, InAppPurchaseActivity.class);
                mIntent.putExtra(InAppConstants.INAPP_SKU_ID, SkuIds.SKU_INAPP_10);
                mIntent.putExtra(InAppConstants.INAPP_SKU_TYPE,IabHelper.ITEM_TYPE_INAPP);
                startActivityForResult(mIntent, 101);
            }
            break;
            case R.id.button3:
            {
                Intent mIntent=new Intent(MainActivity.this, InAppPurchaseActivity.class);
                mIntent.putExtra(InAppConstants.INAPP_SKU_ID, SkuIds.SKU_SUBS_MONTHLY);
                mIntent.putExtra(InAppConstants.INAPP_SKU_TYPE,IabHelper.ITEM_TYPE_SUBS);
                startActivityForResult(mIntent, 101);
            }
            break;
            case R.id.button4:
            {
                Intent mIntent=new Intent(MainActivity.this, InAppPurchaseActivity.class);
                mIntent.putExtra(InAppConstants.INAPP_SKU_ID, SkuIds.SKU_SUBS_ONE_DAY);
                mIntent.putExtra(InAppConstants.INAPP_SKU_TYPE,IabHelper.ITEM_TYPE_SUBS);
                startActivityForResult(mIntent, 101);
            }
            break;

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==101){
                String mInapSkuType=data.getExtras().getString(InAppConstants.INAPP_SKU_TYPE);
                String mInapSkuId=data.getExtras().getString(InAppConstants.INAPP_SKU_ID);
                int value=data.getExtras().getInt("response_code");
                switch (value) {
                    case InAppConstants.RESULT_PRODUCT_CONSUME_SUCCESSFULLY:
                        responseAlertDialog("You have successfully consume "+mInapSkuId+" product.");
                        break;
                    case InAppConstants.RESULT_PRODUCT_PURCHASE_CONSUME_SUCCESSFULLY:
                        responseAlertDialog("You have successfully purchase "+mInapSkuId+" product.");
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

            }
        });
        bld.create().show();
    }

}