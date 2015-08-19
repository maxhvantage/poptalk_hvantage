package com.ciao.app.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.views.activities.NewSmsActivity;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;

/**
 * Created by rajat on 30/1/15.
 * This Adapter is used to show SMS Log under the sms tab inside the app
 */
public class SmsLogAdapter extends  CursorAdapter {

	private static Context context;
	String strDateFormat = "HH:mm a";
	SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
	private static Typeface boldType,normalType ;

	public SmsLogAdapter(Context context) {
		super(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		this.context = context;
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
		String userName = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.NAME));
		String lastMessage = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_LAST_MESSAGE));
		String messageTime = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED));
		String userPic = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.KEY_CONTACT_PIC));
		String unreadMessage = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_UNREAD_MESSAGE));

		long timeStamp = Long.parseLong(messageTime);
		Date date = new Date(timeStamp);
		messageTime  = sdf.format(date);
		ViewHolder.get(view).bind(userID,userPic,null,lastMessage, messageTime,unreadMessage);

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
			mLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.row_sms_view, parent, false);
			mLayout.setOnClickListener(this);
			mLayout.setTag(this);
			mContactPhoto = (CircularImageView) mLayout.findViewById(R.id.data_item_image);
			mContactName = (TextView) mLayout.findViewById(R.id.tv_messeage_contact_name);
			ciaoUserIV = (ImageView) mLayout.findViewById(R.id.imageView);
			lastMessageTV = (TextView) mLayout.findViewById(R.id.tv_message_body);
			messageTimeTV = (TextView) mLayout.findViewById(R.id.tv_message_time);
			unreadMessageTV = (TextView)mLayout.findViewById(R.id.tv_unread_count);
		}

		public void bind(String userID,String photoUri,String userName, String lastMessage,String messageTime,String unreadCount) {
			String phoneNumber = userID;
			userID = ApplicationDAO.getInstance(context).getUserIdFromNumber(phoneNumber);
			if(userID==null){
				mContactName.setText(AppUtils.formatPhoneNumberUsingParentheses(phoneNumber));
			}else {
				userName = ApplicationDAO.getInstance(context).getUserNameFromUserId(userID);
				mContactName.setText(userName);
			}
			messageTimeTV.setText(messageTime);

			if (photoUri != null) {
				try {
					Bitmap bmp=BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(photoUri)));
					mContactPhoto.setImageBitmap(bmp);  
				} catch (Exception e) {
					Log.e("sms_log_adapter", "picture not found");
				}
			} 
			else {
				mContactPhoto.setImageResource(R.drawable.user_avtar);
			}

			if(lastMessage!=null){
				if(lastMessage.contains(".jpg")){
					lastMessageTV.setText("Image");
				}else {
					lastMessageTV.setText(lastMessage);
				}
			}
			unreadMessageTV.setText(unreadCount);
			if(unreadCount.equalsIgnoreCase("0")){
				unreadMessageTV.setVisibility(View.GONE);
				mContactName.setTypeface(normalType);
			}else{
				unreadMessageTV.setVisibility(View.VISIBLE);
				mContactName.setTypeface(boldType);
			}
			mContactName.setTag(phoneNumber);
			messageTimeTV.setTag(userName);
			mContactPhoto.setTag(photoUri);

		}

		@Override
		public void onClick(View v) {

			TextView nameTV = (TextView)v.findViewById(R.id.tv_messeage_contact_name);
			TextView  messageTimeTV = (TextView) v.findViewById(R.id.tv_message_time);
			CircularImageView picCIV = (CircularImageView)v.findViewById(R.id.data_item_image);
			String userID = (String)nameTV.getTag();
			/*String userIdInContact = ApplicationDAO.getInstance(v.getContext()).getUserIdFromNumber(userID);
			if(userIdInContact!=null){
				userID =userIdInContact;
			}*/
			String userName = (String)messageTimeTV.getTag();
			String pic = (String)picCIV.getTag();
            
			//Open Sms Window
			Intent intent = new Intent(v.getContext(),NewSmsActivity.class);
			intent.putExtra("_id",userID);
			intent.putExtra("_name",userName);
			intent.putExtra("_pic",pic);
			v.getContext().startActivity(intent);


		}

	}
}