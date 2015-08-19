package com.ciao.app.views.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.poptalk.app.R;
import com.ciao.app.adapters.FullScreenImageAdapter;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.constants.XmppConstants;

public class FullScreenImageActivity extends Activity {
	private ViewPager mImagePager;
	private PagerAdapter mPagerAdapter;
	private ArrayList<String> imageList;
	private String currentImage,userJid;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		imageList = new ArrayList<String>();
		Intent intent = getIntent();
		if(intent!=null){
			Bundle bundle = intent.getExtras();
			if(bundle!=null){
				currentImage =	bundle.getString("current_image");
				userJid = 	bundle.getString("userJid");
				boolean isGroupChat = bundle.getBoolean("is_group_chat");
				if(isGroupChat){
					getAllImagesForGivenGroupChat(userJid);
				}else{
					getAllImagesForGivenChat(userJid);	
				}
				
				
			}
		}

		setContentView(R.layout.activity_full_screen_image_slider);
		mImagePager = (ViewPager)findViewById(R.id.vp_image_viewer);
		mPagerAdapter = new FullScreenImageAdapter(this,imageList);
		mImagePager.setAdapter(mPagerAdapter);
		mImagePager.setCurrentItem(getIndexOfCurrentImage(currentImage));
	}

	

	public void goToPreviousScreen(View view){
		finish();
	}
	
	public void getAllImagesForGivenChat(String userJid){
		Cursor cursor = getContentResolver().query(Uri.parse(AppDatabaseConstants.CHAT_USER_MESSAGE_CONTENT_URI_STRING), null, AppDatabaseConstants.USER_CHAT_ID+" = ? AND "+AppDatabaseConstants.MESSAGE_TYPE+" = ?", new String[]{userJid,XmppConstants.TYPE_IMAGE}, null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				String imagePath = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE));
				imageList.add(imagePath);
				
			}
		}
		
	}
	private void getAllImagesForGivenGroupChat(String groupJid) {
		groupJid = groupJid.split("@")[0];
		Cursor cursor = getContentResolver().query(Uri.parse(AppDatabaseConstants.GROUP_CHAT_DETAIL_CONTENT_URI_STRING), null, AppDatabaseConstants.COLUMN_GROUP_JID+" = ? AND "+AppDatabaseConstants.MESSAGE_TYPE+" = ?", new String[]{groupJid,XmppConstants.TYPE_IMAGE}, null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				String imagePath = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE));
				imageList.add(imagePath);
				
			}
		}
		
	}
	
	private int getIndexOfCurrentImage(String currentImage){
		int index=0;
		for(int i=0;i<imageList.size();i++){
			if(currentImage.equalsIgnoreCase(imageList.get(i))){
				index = i;
				break;
			}
		}
		return index;
		
	}

}
