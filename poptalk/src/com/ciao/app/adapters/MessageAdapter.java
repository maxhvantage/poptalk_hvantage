package com.ciao.app.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.imageloader.ImageLoader;
import com.ciao.app.views.activities.ChatActivity;
import com.ciao.app.views.activities.GroupChatActivity;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;

/**
 * Created by rajat on 30/1/15.
 * This Adapter is used to show Chat log  inside the app
 */
public class MessageAdapter extends CursorAdapter {

	private static Context context;
	String strDateFormat = "hh:mm a";
	SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
	private static ImageLoader mImageLoader;
	private static Typeface boldType,normalType ;

	public MessageAdapter(Context context) {
		super(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		this.context = context;
		mImageLoader = ImageLoader.getInstance(context);
		boldType = Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Bold.ttf");
		normalType = Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Regular.ttf");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return ViewHolder.create(parent);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String userID = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.USER_ID));
		String time = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED));
		String lastMessage = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_LAST_MESSAGE));
		String userName = ApplicationDAO.getInstance(context).getUserNameFromUserId(userID);
		String commUser = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.USER_ID));
		String userPic = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
		String isGroupChat = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_IS_GROUP_CHAT));
		String phoneNumber = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_PHONE_NUMBER));
		long timeStamp = Long.parseLong(time);
		Date date = new Date(timeStamp);
		time  = sdf.format(date);
		String unreadMessage = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE));
		if(userID!=null){
			ViewHolder.get(view).bind(userName,userID, commUser,phoneNumber, lastMessage, userPic,time,isGroupChat,unreadMessage);
		}
	}

	private static class ViewHolder implements View.OnClickListener {
		private ViewGroup mLayout;
		private CircularImageView mContactPhoto;
		private TextView mContactName, lastMessageTV, messageTimeTV,unreadMessageTV;
		public ImageView ciaoUserIV;


		public static final ViewGroup create(ViewGroup parent) {
			return new ViewHolder(parent).mLayout;
		}

		public static final ViewHolder get(View parent) {
			return (ViewHolder) parent.getTag();
		}

		private ViewHolder(ViewGroup parent) {
			Context context = parent.getContext();
			mLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.row_message_view, parent, false);
			mLayout.setOnClickListener(this);
			mLayout.setTag(this);
			mContactPhoto = (CircularImageView) mLayout.findViewById(R.id.data_item_image);
			mContactName = (TextView) mLayout.findViewById(R.id.tv_messeage_contact_name);
			ciaoUserIV = (ImageView) mLayout.findViewById(R.id.imageView);
			lastMessageTV = (TextView) mLayout.findViewById(R.id.tv_message_body);
			messageTimeTV = (TextView) mLayout.findViewById(R.id.tv_message_time);
			unreadMessageTV = (TextView)mLayout.findViewById(R.id.tv_unread_count);
		}

		public void bind(String userName,String id, String commUser, String phoneNumber, String lastMessage, String photoUri,String time,String isGroupChat,String unreadCount) {
			if(userName!=null){
				mContactName.setText(userName);
			}else {

				userName = ApplicationDAO.getInstance(context).getUserNameFromCommUser(id, phoneNumber);
				mContactName.setText(userName);	
			}
			if(lastMessage!=null){
				if(lastMessage.contains(".jpg")||lastMessage.contains(".png")){
					lastMessageTV.setText("Image");
				}else {
					lastMessageTV.setText(lastMessage);
				}
			}
			if(isGroupChat != null && isGroupChat.equalsIgnoreCase("Y")){
				photoUri = ApplicationDAO.getInstance(mContactName.getContext()).getGroupImage(id);
			}
			unreadMessageTV.setText(unreadCount);
			if(unreadCount.equalsIgnoreCase("0")){
				unreadMessageTV.setVisibility(View.GONE);
				mContactName.setTypeface(normalType);
			}else{
				unreadMessageTV.setVisibility(View.VISIBLE);
				mContactName.setTypeface(boldType);
			}

			messageTimeTV.setText(time);
			mContactName.setTag(commUser);
			messageTimeTV.setTag(phoneNumber);

			mContactPhoto.setTag(photoUri);
			lastMessageTV.setTag(isGroupChat);
			if (photoUri != null && photoUri.length()>0) {
				mImageLoader.displayImage(photoUri, mContactPhoto, true);
				/*try {
					Bitmap bmp=BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(photoUri)));
					mContactPhoto.setImageBitmap(bmp);  
				} catch (Exception e) {
					e.printStackTrace();
				}*/
			} 
			else {
				if(isGroupChat == null || !isGroupChat.equalsIgnoreCase("Y")){
					mContactPhoto.setImageResource(R.drawable.user_avtar);
				}else{
					mContactPhoto.setImageResource(R.drawable.user_avtar_for_group);	
				}
			}

		}

		@Override
		public void onClick(View v) {
			TextView textView = (TextView) v.findViewById(R.id.tv_messeage_contact_name);
			TextView tvTime = (TextView) v.findViewById(R.id.tv_message_time);
			TextView tvlastMessage = (TextView)v.findViewById(R.id.tv_message_body);
			CircularImageView picCIV = (CircularImageView)v.findViewById(R.id.data_item_image);
			String userJID = (String)textView.getTag();
			String phoneNumber = (String)tvTime.getTag();
			String pic =(String)picCIV.getTag();
			String isGroup = (String)tvlastMessage.getTag();
			if(isGroup == null || !isGroup.equalsIgnoreCase("Y")){
				Intent intent =  new Intent(v.getContext(),ChatActivity.class);
				intent.putExtra("jid",userJID);
				intent.putExtra("user_name",phoneNumber);
				intent.putExtra("user_pic",pic);
				v.getContext().startActivity(intent);	
			}else{
				Intent intent =  new Intent(v.getContext(),GroupChatActivity.class);
				intent.putExtra("jid",userJID);
				intent.putExtra("user_name",phoneNumber);
				intent.putExtra("user_pic",pic);
				v.getContext().startActivity(intent);
			}


		}
	}
}