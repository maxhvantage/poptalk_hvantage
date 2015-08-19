package com.ciao.app.views.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.adapters.ContactListAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.Glossary;
import com.ciao.app.utils.AnimationUtils;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.ChatActivity;
import com.ciao.app.views.activities.ContactDetailsActivity;
/**
 * Created by rajat on 24/3/15.
 * This fragment display the list of users who are using poptalk
 */
public class CiaoUserContactListFragment extends Fragment implements OnItemClickListener, AnimationListener{
	private View mView;
	private Activity mActivity;
	protected static final String TAG = "MainActivity";
	private LinearLayout mIndexerLayout;
	private ListView mListView;
	private FrameLayout mTitleLayout;
	private TextView mTitleText;
	private RelativeLayout mSectionToastLayout;
	private TextView mSectionToastText;
	private ArrayList<Glossary> glossaries = new ArrayList<Glossary>();
	private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private AlphabetIndexer mIndexer;
	private ContactListAdapter mAdapter;
	private int lastSelectedPosition = -1;
	private EditText mContactSearchET;
	private ImageView clearSearchIV;
	private int contactOperation;
	private Cursor cursor;

	private ImageView mSyncIV;
	private Animation animation;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mActivity = getActivity();
		Bundle bundle = getArguments();
		contactOperation = bundle.getInt("operation");
		mView = inflater.inflate(R.layout.fragment_contact_list_interstial, container, false);
		intViews(mView);
		return mView;
	}


	private void intViews(View mView) {
		Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Regular.ttf");
		mIndexerLayout = (LinearLayout)mView.findViewById(R.id.indexer_layout);
		mListView = (ListView)mView.findViewById(R.id.contacts_list);
		mTitleLayout = (FrameLayout)mView.findViewById(R.id.title_layout);
		mTitleText = (TextView)mView.findViewById(R.id.title_text);
		mSectionToastLayout = (RelativeLayout)mView.findViewById(R.id.section_toast_layout);
		mSectionToastText = (TextView)mView.findViewById(R.id.section_toast_text);
		mContactSearchET = (EditText)mView.findViewById(R.id.et_search_contact);
		mContactSearchET.setTypeface(type);
		clearSearchIV = (ImageView)mView.findViewById(R.id.bnt_clear_search_et);
		mSyncIV = (ImageView)mView.findViewById(R.id.iv_sync_contacts);

		((RelativeLayout)mView.findViewById(R.id.rel_contacts_list)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("hi", "hi");
			}
		});

		for(int i = 0; i < alphabet.length(); i++) {
			TextView letterTextView = new TextView(mActivity);
			letterTextView.setText(alphabet.charAt(i)+"");
			letterTextView.setTextSize(8f);
			letterTextView.setGravity(Gravity.CENTER);
			LayoutParams params = new LinearLayout.LayoutParams(28, 0, 1.0f);
			letterTextView.setLayoutParams(params);
			letterTextView.setPadding(4, 0, 2, 0);
			mIndexerLayout.addView(letterTextView);
			mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
		}


		Uri uri = Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING);
		cursor = mActivity.getContentResolver().query(uri, null, AppDatabaseConstants.KEY_CIAO_USER + " = ?",new String[] {"Y"}, AppDatabaseConstants.KEY_NAME+ " ASC");
		if (cursor.moveToFirst()) {
			do {
				String name = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				String sortKey = getSortKey(name);
				String picUrl = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
				String id = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_ID));
				Glossary glossary = new Glossary();
				glossary.setName(name);
				glossary.setId(id);
				glossary.setSortKey(sortKey);
				glossary.setPicUrl(picUrl);
				glossaries.add(glossary);
			} while (cursor.moveToNext());
		}

		mAdapter = new ContactListAdapter(mActivity, glossaries);
		mIndexer = new AlphabetIndexer(cursor, 1, alphabet);
		mAdapter.setIndexer(mIndexer);

		if(glossaries != null && glossaries.size() > 0) {
			mListView.setAdapter(mAdapter);
			mListView.setOnScrollListener(mOnScrollListener);
			mIndexerLayout.setOnTouchListener(mOnTouchListener);
			mListView.setOnItemClickListener(this);
		}


		mContactSearchET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mAdapter.getFilter().filter(s.toString());
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		clearSearchIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContactSearchET.setText("");

			}
		});

	}

	private String getSortKey(String sortKeyString) {
		sortKeyString = sortKeyString.trim();
		String key = sortKeyString.substring(0, 1).toUpperCase();
		if (key.matches("[A-Z]")) {
			return key;
		}
		return "#";
	}
	// ScrollListener listener for Alphabet indexer
	private OnScrollListener mOnScrollListener = new OnScrollListener() {

		private int lastFirstVisibleItem = -1;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// firstVisibleItem corresponding to the index of AlphabetIndexer(eg, B-->Alphabet index is 2)
			int sectionIndex = mIndexer.getSectionForPosition(firstVisibleItem);
			//next section Index corresponding to the positon in the listview
			int nextSectionPosition = mIndexer.getPositionForSection(sectionIndex + 1);
			//Log.d(TAG, "onScroll()-->firstVisibleItem="+firstVisibleItem+", sectionIndex=" +sectionIndex+", nextSectionPosition="+nextSectionPosition);
			if(firstVisibleItem != lastFirstVisibleItem) {
				MarginLayoutParams params = (MarginLayoutParams) mTitleLayout.getLayoutParams();
				params.topMargin = 0;
				mTitleLayout.setLayoutParams(params);
				mTitleText.setText(String.valueOf(alphabet.charAt(sectionIndex)));
				//((TextView) mIndexerLayout.getChildAt(sectionIndex)).setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
				lastFirstVisibleItem = firstVisibleItem;
			}

			// update AlphabetIndexer background
			if(sectionIndex != lastSelectedPosition) {
				if(lastSelectedPosition != -1) {
					((TextView) mIndexerLayout.getChildAt(lastSelectedPosition)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
				}
				lastSelectedPosition = sectionIndex;
			}

			if(nextSectionPosition == firstVisibleItem + 1) {
				View childView = view.getChildAt(0);
				if(childView != null) {
					int sortKeyHeight = mTitleLayout.getHeight();
					int bottom = childView.getBottom();
					MarginLayoutParams params = (MarginLayoutParams) mTitleLayout.getLayoutParams();

					if(params.topMargin != 0) {
						params.topMargin = 0;
						mTitleLayout.setLayoutParams(params);
					}

				}
			}

		}

	};


	// touch listener for Alphabet indexer
	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			float alphabetHeight = mIndexerLayout.getHeight();
			float y = event.getY();
			int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
			if (sectionPosition < 0) {
				sectionPosition = 0;
			} else if (sectionPosition > 26) {
				sectionPosition = 26;
			}
			if(lastSelectedPosition != sectionPosition) {
				if(-1 != lastSelectedPosition){
					((TextView) mIndexerLayout.getChildAt(lastSelectedPosition)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
				}
				lastSelectedPosition = sectionPosition;
			}
			String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));
			int position = mIndexer.getPositionForSection(sectionPosition);
			TextView textView = (TextView) mIndexerLayout.getChildAt(sectionPosition);
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
					textView.setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
					mSectionToastLayout.setVisibility(View.VISIBLE);
					mSectionToastText.setText(sectionLetter);
					mListView.smoothScrollToPositionFromTop(position,0,1);
					break;
				case MotionEvent.ACTION_MOVE:
					mIndexerLayout.setBackgroundResource(R.drawable.letterslist_bg);
					textView.setBackgroundColor(getResources().getColor(R.color.letter_bg_color));
					mSectionToastLayout.setVisibility(View.VISIBLE);
					mSectionToastText.setText(sectionLetter);
					mListView.smoothScrollToPositionFromTop(position,0,1);
					break;
				case MotionEvent.ACTION_UP:
					mIndexerLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
					textView.setBackgroundColor(getResources().getColor(R.color.transparent));
					mSectionToastLayout.setVisibility(View.GONE);
				default:
					textView.setBackgroundColor(getResources().getColor(R.color.transparent));
					mSectionToastLayout.setVisibility(View.GONE);
					break;
			}
			return true;
		}

	};


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		TextView textView = (TextView)view.findViewById(R.id.contact_name);
		Glossary glossary = (Glossary)textView.getTag();
		Intent intent;
		switch (contactOperation){
			case AppConstants.CONTACT_DETAIL:
				intent = new Intent(mActivity, ContactDetailsActivity.class);
				intent.putExtra("_id",glossary.getId());
				intent.putExtra("_name",glossary.getName());
				intent.putExtra("_pic",glossary.getPicUrl());
				mActivity.startActivity(intent);
				break;
			case AppConstants.CONTACT_INVITE:
				break;
			case AppConstants.CONTACT_MESSAGE:
				intent = new Intent(mActivity, ChatActivity.class);
				String commUser = ApplicationDAO.getInstance(mActivity).getCommUserFromUserId(glossary.getId());
				String phoneNumber = ApplicationDAO.getInstance(mActivity).getCiaoNumberFromId(glossary.getId());

				if (phoneNumber == null) {
					phoneNumber = ApplicationDAO.getInstance(mActivity).getUserPhoneNumber(glossary.getId());
				}

				intent.putExtra("jid", commUser);
				intent.putExtra("user_name", phoneNumber);
				intent.putExtra("user_pic", glossary.getPicUrl());
				mActivity.startActivity(intent);
				break;
		}
	}


	public void pleaseCheckBack(boolean bool){
		if(bool){
			startAnimation();
			((RelativeLayout)mView.findViewById(R.id.rel_contacts_list)).setVisibility(View.VISIBLE);
		}else{
			stopAnimation();
			((RelativeLayout)mView.findViewById(R.id.rel_contacts_list)).setVisibility(View.GONE);
		}
	}

	public void notifyListAdapter(){
		glossaries.clear();

		Uri uri = Uri.parse(AppDatabaseConstants.CONTACTS_CONTENT_URI_STRING);
		cursor = mActivity.getContentResolver().query(uri, null, AppDatabaseConstants.KEY_CIAO_USER + " = ?",new String[] {"Y"}, AppDatabaseConstants.KEY_NAME+ " ASC");
		if (cursor.moveToFirst()) {
			do {
				String name = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				String sortKey = getSortKey(name);
				String picUrl = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
				String id = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_ID));
				Glossary glossary = new Glossary();
				glossary.setName(name);
				glossary.setId(id);
				glossary.setSortKey(sortKey);
				glossary.setPicUrl(picUrl);
				glossaries.add(glossary);
			} while (cursor.moveToNext());
		}

		mAdapter.notifyDataSetChanged();
	}


	@Override
	public void onAnimationStart(Animation animation) {

	}
	@Override
	public void onAnimationEnd(Animation animation) {
		if(AppSharedPreference.getInstance(mActivity).getAppContactSyncing()){
			animation.reset();
			animation.start();
		}else{
			stopAnimation();
		}

	}
	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	public void startAnimation(){
		if(mSyncIV!=null){
			animation =  AnimationUtils.syncInProgressAnimation();
			animation.setAnimationListener(this);
			mSyncIV.startAnimation(animation);
		}
	}

	public void stopAnimation(){
		if(mSyncIV!=null && animation!=null){
			mSyncIV.clearAnimation();
			animation.setAnimationListener(null);
		}

	}

}