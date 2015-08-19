package com.ciao.app.views.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.poptalk.app.R;
import com.ciao.app.adapters.AlphabetListAdapter;
import com.ciao.app.adapters.AlphabetListAdapter.CountryBean;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.datamodel.Glossary;
import com.ciao.app.views.activities.ContactListActivity.SideIndexGestureListener;

public class ContactListActivity  extends ListActivity implements OnItemClickListener {

	private AlphabetListAdapter adapter = new AlphabetListAdapter();
	private GestureDetector mGestureDetector;
	private List<Object[]> alphabet = new ArrayList<Object[]>();
	private HashMap<String, Integer> sections = new HashMap<String, Integer>();
	private int sideIndexHeight;
	private static float sideIndexX;
	private static float sideIndexY;
	private int indexListSize;
	private RelativeLayout mSectionToastLayout;
	private TextView mSectionToastText;
	private EditText mContactSearchET;
	private ImageView clearSearchIV;
	LinearLayout sideIndex;
	private InputStream inputStream = null;
	AssetManager manager;

	List<AlphabetListAdapter.Row> rows,oringenalList;


	class SideIndexGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			sideIndexX = sideIndexX - distanceX;
			sideIndexY = sideIndexY - distanceY;

			if (sideIndexX >= 0 && sideIndexY >= 0) {
				displayListItem();
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_selection);

		mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());

		mSectionToastLayout = (RelativeLayout)findViewById(R.id.section_toast_layout);
		mSectionToastText = (TextView)findViewById(R.id.section_toast_text);
		mContactSearchET = (EditText)findViewById(R.id.et_search_contact);
		clearSearchIV = (ImageView)findViewById(R.id.bnt_clear_search_et);


		List<CountryBean> countries = getCiaoOutContacts();
		Collections.sort(countries);
		rows = new ArrayList<AlphabetListAdapter.Row>();
		oringenalList = new ArrayList<AlphabetListAdapter.Row>();
		int start = 0;
		int end = 0;
		String previousLetter = null;
		Object[] tmpIndexItem = null;
		Pattern numberPattern = Pattern.compile("[0-9]");

		for (CountryBean country : countries) {
			String firstLetter = country.getCountryName().substring(0, 1);
			// Group numbers together in the scroller
			if (numberPattern.matcher(firstLetter).matches()) {
				firstLetter = "#";
			}

			// If we've changed to a new letter, add the previous letter to the alphabet scroller
			if (previousLetter != null && !firstLetter.equals(previousLetter)) {
				end = rows.size() - 1;
				tmpIndexItem = new Object[3];
				tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
				tmpIndexItem[1] = start;
				tmpIndexItem[2] = end;
				alphabet.add(tmpIndexItem);

				start = end + 1;
			}

			// Check if we need to add a header row
			if (!firstLetter.equals(previousLetter)) {
				rows.add(new AlphabetListAdapter.Section(firstLetter));
				sections.put(firstLetter, start);
			}

			// Add the country to the list
			rows.add(new CountryBean(country.getCountryName(),country.getCountryCode()));
			previousLetter = firstLetter;

		}

		if (previousLetter != null) {
			// Save the last letter
			tmpIndexItem = new Object[3];
			tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
			tmpIndexItem[1] = start;
			tmpIndexItem[2] = rows.size() - 1;
			alphabet.add(tmpIndexItem);
		}

		adapter.setRows(rows);
		setListAdapter(adapter);
		oringenalList = rows;

		updateList();
		ListView list = getListView();
		list.setOnItemClickListener(this);

		mContactSearchET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()==0)
				{
					adapter.setRows(oringenalList);
					setListAdapter(adapter);
				}else{
					adapter.getFilter().filter(s.toString());
					adapter.notifyDataSetChanged();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		clearSearchIV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mContactSearchET.setText("");

			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		} else {
			return false;
		}
	}


	public void updateList() {
		sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
		sideIndex.removeAllViews();
		indexListSize = alphabet.size();
		if (indexListSize < 1) {
			return;
		}

		int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
		int tmpIndexListSize = indexListSize;
		while (tmpIndexListSize > indexMaxSize) {
			tmpIndexListSize = tmpIndexListSize / 2;
		}
		double delta;
		if (tmpIndexListSize > 0) {
			delta = indexListSize / tmpIndexListSize;
		} else {
			delta = 1;
		}

		TextView tmpTV;
		for (double i = 1; i <= indexListSize; i = i + delta) {
			Object[] tmpIndexItem = alphabet.get((int) i - 1);
			String tmpLetter = tmpIndexItem[0].toString();

			tmpTV = new TextView(this);
			tmpTV.setText(tmpLetter);
			tmpTV.setGravity(Gravity.CENTER);
			tmpTV.setTextSize(11);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
			tmpTV.setLayoutParams(params);
			sideIndex.addView(tmpTV);
		}

		sideIndexHeight = sideIndex.getHeight();

		sideIndex.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// now you know coordinates of touch
				sideIndexX = event.getX();
				sideIndexY = event.getY();

				// and can display a proper item it country list
				displayListItem();
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					mSectionToastLayout.setVisibility(View.VISIBLE);
					return true;

				}else if(event.getAction()==MotionEvent.ACTION_MOVE){
					mSectionToastLayout.setVisibility(View.VISIBLE);

				}else{
					mSectionToastLayout.setVisibility(View.INVISIBLE);

				}
				return false;
			}
		});
	}

	public void displayListItem() {
		sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
		sideIndexHeight = sideIndex.getHeight();
		// compute number of pixels for every side index item
		double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

		// compute the item index for given event position belongs to
		int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

		// get the item (we can do it since we know item index)
		if (itemPosition < alphabet.size()) {
			Object[] indexItem = alphabet.get(itemPosition);
			int subitemPosition = sections.get(indexItem[0]);
			String key="";
			for(Map.Entry<String, Integer> entry : sections.entrySet()) {
				if (entry.getValue() == subitemPosition) {
					key = entry.getKey();
				}
			}

			mSectionToastLayout.setVisibility(View.VISIBLE);
			mSectionToastText.setText(key);
			getListView().setSelection(subitemPosition);

		}
	}


	public String loadTextFile(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		byte[] bytes = new byte[4096];
		int len = 0;
		while ((len = inputStream.read(bytes)) > 0)
			byteStream.write(bytes, 0, len);
		return new String(byteStream.toByteArray(), "UTF8");
	}

	/*private List<CountryBean> populateCountries() {
		List<CountryBean> countries = new ArrayList<CountryBean>();
		try{
			manager = getAssets();
			inputStream = manager.open("CountryCodes.json");
			String text = loadTextFile(inputStream);

			JSONArray jsonArray = new JSONArray(text);
			JSONObject jsonObject = null;


			for(int index = 0;index<=jsonArray.length();index++)
			{
				jsonObject = jsonArray.getJSONObject(index);
				AlphabetListAdapter.CountryBean countryBean = new CountryBean();
				String countryName = jsonObject.getString("name");

				countryBean.setCountryName(countryName);
				countryBean.setCountryCode(jsonObject.getString("dial_code"));

				countries.add(countryBean);
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return countries;
	}*/

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		TextView countryNameTV = (TextView)view.findViewById(R.id.country_name);
		if(countryNameTV!=null)
		{
			String countryName = countryNameTV.getText().toString();
			String countryCode = ((TextView)view.findViewById(R.id.country_code)).getText().toString();

			Intent intent = new Intent();
			intent.putExtra("_countrycode", countryCode);
			intent.putExtra("_countryname", countryName);
			setResult(1, intent);
			finish();
		}
	}

	public void goToPreviousScreen(View view){
		finish();
	}

	public List<CountryBean> getCiaoOutContacts(){
		List<CountryBean> countries = new ArrayList<CountryBean>();
		Uri uri = Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING);
		Cursor cursor = getContentResolver().query(uri, null, AppDatabaseConstants.KEY_CIAO_USER + " = ?",new String[] {"N"}, AppDatabaseConstants.KEY_NAME+ " ASC");
		if (cursor.moveToFirst()) {
			do {
				AlphabetListAdapter.CountryBean countryBean = new CountryBean();
				String name = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				String picUrl = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
				String id = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_ID));
				countryBean.setCountryName(name);
				countryBean.setCountryCode(id);
				countries.add(countryBean);
				
			} while (cursor.moveToNext());
		}
		return countries;
	}
}
