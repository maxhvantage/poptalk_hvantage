package com.ciao.app.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.poptalk.app.R;
import com.ciao.app.adapters.CountryListAdapter;
import com.ciao.app.datamodel.CountryListBean;



public class CountryListActivity extends Activity {
	ImageView iv_back_from_country_list;
	private List<CountryListBean> arraylist=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		setContentView(R.layout.activity_country_list);

		ListView listView = (ListView) findViewById(R.id.lv_country_list);
		iv_back_from_country_list= (ImageView) findViewById(R.id.iv_back_from_country_list);
		arraylist = new ArrayList<CountryListBean>();

		arraylist.add(new CountryListBean("USA","US","1", R.drawable.flag_usa));
		arraylist.add(new CountryListBean("Brazil","BR","55", R.drawable.flag_brazil));
		arraylist.add(new CountryListBean("Mexico","MX","52", R.drawable.flag_mexico));
		listView.setAdapter(new CountryListAdapter(this, arraylist));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String countryCode= arraylist.get(position).getCountryCode();
				String countryPrefix= arraylist.get(position).getCountryPrefix();
				Intent intent=new Intent(CountryListActivity.this,BuyNumberCountryWise.class);
				intent.putExtra("COUNTRY_CODE",countryCode);
				intent.putExtra("COUNTRY_PREFIX",countryPrefix);
				startActivity(intent);

			}
		});



	}
	public void goToPreviousScreen(View view)
	{
		finish();
	}
}