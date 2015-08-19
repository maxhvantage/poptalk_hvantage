package com.ciao.app.views.activities;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.adapters.CountryNumbersListAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.datamodel.BaseResponseBean;
import com.ciao.app.datamodel.NumberResponseBean;
import com.ciao.app.datamodel.NumbersListBean;
import com.ciao.app.netwrok.backgroundtasks.NumberSearchAsyncTask;
import com.ciao.app.utils.AppUtils;
import com.poptalk.app.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//This screen will display the list of number for a selected country.

public class BuyNumberCountryWise extends ListActivity implements View.OnClickListener {
    private String numberSelected;
    private String numbersearchUrl = AppNetworkConstants.NUMBER_SEARCH_URL;
    ListView listView;
    Button btn_continue;
    ArrayList<NumbersListBean> arraylist;
    String selected_no;
    CountryNumbersListAdapter adapter;
    private String numberBuyUrl = AppNetworkConstants.BUY_NUMBER_URL;
    private List<String> countryList = new ArrayList<String>();
    private String countryCode;
    private String countryPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_another_number);

        btn_continue= (Button) findViewById(R.id.bt_continue_call);
        countryCode=getIntent().getStringExtra("COUNTRY_CODE");
        countryPrefix=getIntent().getStringExtra("COUNTRY_PREFIX");
        callNumberSearch();
        btn_continue.setOnClickListener(this);

    }

    /**
     * Method used to hit api for number search
     */
    private void callNumberSearch() {

        if (!AppSharedPreference.getInstance(BuyNumberCountryWise.this).getIsConnectedToInternet()){
            DialogUtils.showInternetAlertDialog(BuyNumberCountryWise.this);
            return;
        }

        try {
            List<NameValuePair> searchParams = new ArrayList<NameValuePair>();
            searchParams.add(new BasicNameValuePair("country",countryCode));
            searchParams.add(new BasicNameValuePair("pattern",countryPrefix));
            new NumberSearchAsyncTask(this, searchParams,numbersearchUrl).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToPreviousScreen(View view)
    {
        finish();
    }
    @Override
    public void onClick(View v)
    {
        Intent in=new Intent(this,PurchasePhoneNumberActivity.class);
        in.putExtra("selected_no",selected_no);
        in.putExtra("country_code", countryCode);
        startActivity(in);
    }

    /**
     * Method call after successful response and parsing
     */
    public void success(NumberResponseBean responseBean) {
        //bind Number
        List<String> list = responseBean.getBeanlist();


        arraylist = new ArrayList<NumbersListBean>();
        if (list != null && list.size() > 0) {
            Iterator iterator=list.iterator();
            while (iterator.hasNext())
            {
                String number= (String) iterator.next();
                number = AppUtils.formatCiaoNumberUsingParentheses(number);
                arraylist.add(new NumbersListBean(number));

            }
            adapter=new CountryNumbersListAdapter(this,arraylist);
            setListAdapter(adapter);


        }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        selected_no=arraylist.get(position).getNumber();
        adapter.setSelectedIndex(position);
        adapter.notifyDataSetChanged();
    }


    /**
     * Method call after successful response and parsing
     */
    public void onSuccess(BaseResponseBean responseBean) {
        //AppUtils.showTost(BuyNumberActivity.this, responseBean.getmExceptionName());
        AppSharedPreference.getInstance(BuyNumberCountryWise.this).setUserCiaoNumber(numberSelected);
        gotoCallScreen();
    }


    /**
     * Method call after error in response and parsing
     */
    public void error(String error) {
//        AppUtils.showTost(BuyNumberActivity.this, error);
//    	 AppUtils.showTost(BuyNumberActivity.this, responseBean.getmExceptionName());
        // AppSharedPreference.getInstance(BuyNumberActivity.this).setUserCiaoNumber(numberSelected);
         gotoCallScreen();
    }

    private void gotoCallScreen() {
       // Intent intent = new Intent(this, CallActivity.class);
        Intent intent = new Intent(this, CountryListActivity.class);
        startActivity(intent);
        finish();

    }


}
