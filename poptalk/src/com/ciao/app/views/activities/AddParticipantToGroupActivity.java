package com.ciao.app.views.activities;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;

import com.ciao.app.adapters.ContactListAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.Glossary;
import com.ciao.app.datamodel.MemberBean;
import com.ciao.app.netwrok.backgroundtasks.AddNewMemberToGroup;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

//This Activity is used to add new member to existing group
public class AddParticipantToGroupActivity extends Activity implements OnItemClickListener{
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
	private Cursor cursor;
	private String groupName,groupJid,participantJid;
	private List<MemberBean> groupMembersList;
	private Glossary selectedMember;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_participant);
		Intent intent = getIntent();
		if(intent!=null){
			Bundle bundle = intent.getExtras();
			if(bundle!=null){
				groupName = bundle.getString("group_name");
				groupJid = bundle.getString("group_jid");
				groupMembersList = ApplicationDAO.getInstance(this).getGroupMemeberList(groupJid.split("@")[0]);
				
			}
		}
		intViews();
	}
	public void goToPreviousScreen(View view){
		finish();
	}
	
	private void intViews() {
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
		mIndexerLayout = (LinearLayout)findViewById(R.id.indexer_layout);
		mListView = (ListView)findViewById(R.id.contacts_list);
		mTitleLayout = (FrameLayout)findViewById(R.id.title_layout);
		mTitleText = (TextView)findViewById(R.id.title_text);
		mSectionToastLayout = (RelativeLayout)findViewById(R.id.section_toast_layout);
		mSectionToastText = (TextView)findViewById(R.id.section_toast_text);
		mContactSearchET = (EditText)findViewById(R.id.et_search_contact);
		mContactSearchET.setTypeface(type);
		clearSearchIV = (ImageView)findViewById(R.id.bnt_clear_search_et);

		((RelativeLayout)findViewById(R.id.rel_contacts_list)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		for(int i = 0; i < alphabet.length(); i++) {
			TextView letterTextView = new TextView(this);
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
		
		cursor = getContentResolver().query(uri, null, AppDatabaseConstants.KEY_CIAO_USER + " = ? ",new String[] {"Y"}, AppDatabaseConstants.KEY_NAME+ " ASC");
		if (cursor.moveToFirst()) {
			do {
				String name = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_NAME));
				String sortKey = getSortKey(name);
				String picUrl = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
				String id = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_ID));
				String number = ApplicationDAO.getInstance(this).getCiaoRegisteredNumber(id);
				if(!isSubsetOfGroup(number)){
					Glossary glossary = new Glossary();
					glossary.setName(name);
					glossary.setId(id);
					glossary.setSortKey(sortKey);
					glossary.setPicUrl(picUrl);
					glossaries.add(glossary);	
				}
				
			} while (cursor.moveToNext());
		}

		mAdapter = new ContactListAdapter(this, glossaries);
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
		selectedMember = (Glossary)textView.getTag();
		participantJid = ApplicationDAO.getInstance(this).getCiaoRegisteredNumber(selectedMember.getId());
		DialogUtils.showAddToGroupDialog(this, selectedMember.getName(), groupName);
		
	}
	
	//Add new member to group on server
	public void addParticipant(){

		// if network is not enable. go to the phone setting for the network enable.
		if (!AppSharedPreference.getInstance(AddParticipantToGroupActivity.this).getIsConnectedToInternet()){
			DialogUtils.showInternetAlertDialog(AddParticipantToGroupActivity.this);
			return;
		}

		new AddNewMemberToGroup(this, groupJid, participantJid).execute();
	}
	
	//Send join group invitation to new member
	public void sendInvitaionToNewMember(){
		XMPPChatService.addParticipantToGroup(groupJid, participantJid, groupName);
		glossaries.remove(selectedMember);
		mAdapter.notifyDataSetChanged();
		
	}
	
	// Check if  user is already member of a group
	private boolean isSubsetOfGroup(String number){
		for(MemberBean bean:groupMembersList){
			if(bean.getMemberJID()!=null){
				if(bean.getMemberJID().contains(number)){
					return true;
				}
				//Log.e("Member jid = ", bean.getMemberJID());	
			}
			
		}
		return false;
	}

}
