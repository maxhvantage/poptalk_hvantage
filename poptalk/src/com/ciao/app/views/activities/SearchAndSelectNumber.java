package com.ciao.app.views.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.adapters.CountryNumbersListAdapter;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.datamodel.NumberResponseBean;
import com.ciao.app.datamodel.NumbersListBean;
import com.ciao.app.netwrok.backgroundtasks.NumberSearchAsyncTask;
import com.ciao.app.utils.AppUtils;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

//This Activity implement the Ajax like search so that user can  purchase a number of their interest.
public class SearchAndSelectNumber extends ListActivity{

	private EditText mNumberSearchET;
	private String numberSelected;
	private String searchKey="";
	private String numbersearchUrl;
	private List<NameValuePair> numberSearchParams;
	CountryNumbersListAdapter adapter;
	ArrayList<NumbersListBean> arraylist;
	private String selected_no;
	private boolean clearList = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_and_select_pop_number);
		arraylist = new ArrayList<NumbersListBean>();
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		adapter=new CountryNumbersListAdapter(this,arraylist);
		setListAdapter(adapter);
		callNumberSearch(searchKey);
		mNumberSearchET = (EditText)findViewById(R.id.et_search_number);
		mNumberSearchET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length()>=3){
					callNumberSearch(s.toString().trim());
					clearList = true;
				}else if (clearList) {
					callNumberSearch("");
					clearList = false;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * Method used to hit api for number search
	 */
	private void callNumberSearch(String searchKey) {

		if (!AppSharedPreference.getInstance(SearchAndSelectNumber.this).getIsConnectedToInternet()){
			DialogUtils.showInternetAlertDialog(SearchAndSelectNumber.this);
			return;
		}

		numbersearchUrl = AppNetworkConstants.NUMBER_SEARCH_URL;
		numberSearchParams = new ArrayList<NameValuePair>();
		numberSearchParams.add(new BasicNameValuePair("country","US"));
		numberSearchParams.add(new BasicNameValuePair("pattern","1"+searchKey));
		try {
			new NumberSearchAsyncTask(this, numberSearchParams, numbersearchUrl).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method call after successful response and parsing
	 */
	public void success(NumberResponseBean responseBean) {
		//bind Number
		List<String> list = responseBean.getBeanlist();
		arraylist.clear();
		if (list != null && list.size() > 0) {
			Iterator iterator=list.iterator();
			while (iterator.hasNext())
			{
				String number= (String) iterator.next();
				number = AppUtils.formatCiaoNumberUsingParentheses(number);
				arraylist.add(new NumbersListBean(number));
			}
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Method call after error in response and parsing
	 */
	public void error(String error) {
		//AppUtils.showTost(SearchAndSelectNumber.this, error);
		//AppUtils.showTost(BuyNumberActivity.this, responseBean.getmExceptionName());
		//AppSharedPreference.getInstance(BuyNumberActivity.this).setUserCiaoNumber(numberSelected);
		arraylist.clear();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		selected_no=arraylist.get(position).getNumber();
		adapter.setSelectedIndex(position);
		adapter.notifyDataSetChanged();

		Intent intent = new Intent();
		intent.putExtra("selected_no", selected_no);
		setResult(1001, intent);
		finish();
	}

	public void goToPreviousScreen(View view){
		finish();
	}
}
