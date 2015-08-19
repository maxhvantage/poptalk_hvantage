package com.ciao.app.views.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.netwrok.backgroundtasks.ChekinAsyncTask;
import com.ciao.app.netwrok.backgroundtasks.CreditUpdateTaskLoader;
import com.ciao.app.utils.AnimationUtils;
import com.ciao.app.utils.AppUtils;
import com.poptalk.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.networkKeys.NetworkKeys;
import in.lockerapplication.networkcall.CheckConnection;


public class CheckinActivity extends Activity {

    private TextView totalCreditTV;


    private int mYear,mMonth,mDay;
    private String d1,d2;
    public static CheckinActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        instance = this;
        totalCreditTV = (TextView)findViewById(R.id.tv_total_credits);
        totalCreditTV.setText(AppSharedPreference.getInstance(this).getTotalCredit());

        Calendar cal = Calendar.getInstance();
        mYear=cal.get(Calendar.YEAR);
        mMonth=cal.get(Calendar.MONTH);
        mDay=cal.get(Calendar.DAY_OF_MONTH);

        d1=String.valueOf(new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));
        d2=AppSharedPreference.getInstance(instance).getChekinDate();

        if(d1.equalsIgnoreCase("") || d2.equalsIgnoreCase("")){
            ((ImageView)findViewById(R.id.iv_checkin)).setTag("grayImage");
            ((ImageView)findViewById(R.id.iv_checkin)).setBackgroundResource(R.drawable.gray_location);
        }else{
            if(setDateTo(d1,d2)){
                ((ImageView)findViewById(R.id.iv_checkin)).setTag("grayImage");
                ((ImageView)findViewById(R.id.iv_checkin)).setBackgroundResource(R.drawable.gray_location);
            }else{
                ((ImageView)findViewById(R.id.iv_checkin)).setTag("redImage");
                ((ImageView)findViewById(R.id.iv_checkin)).setBackgroundResource(R.drawable.red_location);
            }
        }

        ((Button)findViewById(R.id.btn_checkin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((ImageView) findViewById(R.id.iv_checkin)).getTag().equals("grayImage")) {
                    callChekin(instance, CheckinActivity.this, AppSharedPreference.getInstance(instance).getUserID());
                } else {
                    Toast.makeText(instance, "You have already checked In for the day!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void goToPreviousScreen(View view){
        finish();
    }

    public void animateCoin(View view){
        AnimationUtils.rotateCreditCoin((ImageView) view);
        AppUtils.playBeep(this);
    }

//    /*
//Init callback function for credit update.
//*/
//    private void initLoader() {
//
//        creditUpdateCallback = new LoaderManager.LoaderCallbacks<String>() {
//            @Override
//            public Loader<String> onCreateLoader(int id, Bundle args) {
//                return new CreditUpdateTaskLoader(mRequestBean);
//            }
//
//            @Override
//            public void onLoadFinished(Loader<String> loader, String data) {
//                if (loader instanceof CreditUpdateTaskLoader) {
//                    ((CreditUpdateTaskLoader) loader).hideLoaderDialog();
//
//                    //Toast.makeText(BuyItemsActivity.this, "creditUpdateCallback on try "+data,Toast.LENGTH_LONG).show();
//                    android.util.Log.e("Return Value :", data);
//                    try {
//                        JSONObject serverResponseJsonObject = new JSONObject(data);
//                        Log.d("APICall", "OnLoadFinished creditUpdateCallBack -- " + serverResponseJsonObject.toString());
//                        boolean valid_flag = serverResponseJsonObject.getBoolean("valid_purchase");
//                        Log.d("APICall : ","update credit purchase on creditUpdateCallback, valid is or not : "+String.valueOf(valid_flag));
//
//                        //Toast.makeText(BuyItemsActivity.this, "creditUpdateCallback is valid "+valid_flag,Toast.LENGTH_LONG).show();
//                        if (valid_flag) {
//                            Toast.makeText(BuyItemsActivity.this, "creditUpdateCallback updated credits",Toast.LENGTH_LONG).show();
//                            AppSharedPreference.getInstance(BuyItemsActivity.this).setTotalCredit(serverResponseJsonObject.getString("current_credits"));
//                            totalCreditTV.setText(AppSharedPreference.getInstance(BuyItemsActivity.this).getTotalCredit());
//
////                            AppUtils.showTost(mRequestBean.getActivity(), "Update the credit : " + serverResponseJsonObject.getString("current_credits"));
//                            AppSharedPreference.getInstance(BuyItemsActivity.this).setAdLockScreenVisibility(true);
//                        } else {
//                            //Login failed
//                            AppUtils.showTost(mRequestBean.getActivity(), serverResponseJsonObject.getString("current_credits"));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onLoaderReset(Loader<String> loader) {
//
//            }
//        };
//    }

    public void callChekin(Context context,Activity frag, String user_id)
    {
        if(CheckConnection.isConnection(context))
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(String.valueOf(NetworkKeys.user_security), "abchjds1256a12dasdsad67672");
                jsonObject.put(String.valueOf(NetworkKeys.user_id), user_id);

                new ChekinAsyncTask(context, frag, jsonObject).execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
        else{
            Toast.makeText(context, context.getResources().getString(R.string.nointernetconnction), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method called on successful**/
    public void onSuccess(CreditResponseBean responseBean){
        ((ImageView)findViewById(R.id.iv_checkin)).setTag("redImage");
        ((ImageView)findViewById(R.id.iv_checkin)).setBackgroundResource(R.drawable.red_location);
        AppSharedPreference.getInstance(instance).setChekinDate(d1);
    }

    public void onError(){
        Toast.makeText(instance, "Error", Toast.LENGTH_LONG).show();
    }

    private Boolean setDateTo(String d1, String d2){
        Boolean isNetxtDay = false;
        try{

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            System.out.println(sdf.format(date1));
            System.out.println(sdf.format(date2));

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date1);
            cal2.setTime(date2);

            if(cal1.after(cal2)){
                System.out.println("Date1 is after Date2");
                isNetxtDay=true;
            }

            if(cal1.before(cal2)){
                System.out.println("Date1 is before Date2");
                isNetxtDay=false;
            }

            if(cal1.equals(cal2)){
                System.out.println("Date1 is equal Date2");
                isNetxtDay=false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return isNetxtDay;
    }

}
