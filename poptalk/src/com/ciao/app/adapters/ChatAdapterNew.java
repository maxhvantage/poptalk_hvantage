package com.ciao.app.adapters;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.poptalk.app.R;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.constants.XmppConstants;
import com.ciao.app.imageloader.ImageLoader;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.activities.FullScreenImageActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
/*
 * This Adapter is used in  Chat window screnn
 */
public class ChatAdapterNew extends CursorAdapter {
	private static ImageLoader imageLoader;
	private static String userJid;
	public ChatAdapterNew(Context context, Cursor c,String userJid) {
		super(context, c,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		imageLoader = ImageLoader.getInstance(context);
		this.userJid = userJid;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return  ViewHolder.create(parent);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder.get(view).bind(cursor);
	}

	private static class ViewHolder implements OnClickListener{

		private ViewGroup chatRowParentView;
		private ViewGroup sentChatView;
		private ViewGroup receivedChatView;
		private FrameLayout sentImageFL;
		private FrameLayout receivedImageFL;
		private TextView sentMessageTV;
		private TextView receivedMessageTV;
		private TextView sentTimeTV;
		private TextView receivedTimeTV;
		private ImageView sentImage;
		private ImageView receivedImage;
		
		

		private ViewHolder(ViewGroup parent) {
			Context context = parent.getContext();
			chatRowParentView =(ViewGroup)LayoutInflater.from(context).inflate(R.layout.row_chatmessage, parent, false);
			
			sentChatView  = (ViewGroup)chatRowParentView.findViewById(R.id.rl_sent_message_container);
			sentImageFL = (FrameLayout)sentChatView.findViewById(R.id.fl_media_sent);
			sentMessageTV =  (TextView)sentChatView.findViewById(R.id.tvsendMessage);
			sentTimeTV =  (TextView)sentChatView.findViewById(R.id.tvtimesend);
			sentImage = (ImageView)sentChatView.findViewById(R.id.iv_pic_sent);
			
			receivedChatView = (ViewGroup)chatRowParentView.findViewById(R.id.rl_received_message_container);
			receivedImageFL= (FrameLayout)receivedChatView.findViewById(R.id.fl_media_received);
			receivedMessageTV = (TextView)receivedChatView.findViewById(R.id.tvreceivedMessage);
			receivedTimeTV= (TextView)receivedChatView.findViewById(R.id.tvtimereceived);
			receivedImage= (ImageView)receivedChatView.findViewById(R.id.iv_pic_received);
			
			sentChatView.setOnClickListener(this);
			receivedChatView.setOnClickListener(this);
			chatRowParentView.setTag(this);
		}

		public static final ViewGroup create(ViewGroup parent) {
			return new ViewHolder(parent).chatRowParentView;
		}

		public void bind(Cursor cursor){
			String coversationType = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_CONVERSATION_TYPE));
			String messageType = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.MESSAGE_TYPE));
			String time = cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_TIME_SENT_RECEIVED));
			//Time logic
			long messageTime = Long.parseLong(time);
	        String timeformat = getDate(messageTime, "dd/MM/yyyy hh:mm aa");
//			Convert24to12(timeformat);
			String date = timeformat.substring(0, 10);
			int day = Integer.parseInt(date.substring(0, 2));
			int date1 = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
			if (day == date1) {
				date = "Today";
			}
			String currenttime = timeformat.substring(11, 19);
			//Time logic
			if(messageType==null){
				messageType =XmppConstants.TYPE_TEXT;
			}
			if(coversationType.equalsIgnoreCase("1")){
				sentChatView.setVisibility(View.VISIBLE);
				receivedChatView.setVisibility(View.GONE);
				
				if(messageType.equalsIgnoreCase(XmppConstants.TYPE_TEXT)){
					sentMessageTV.setVisibility(View.VISIBLE);
					sentMessageTV.setText(cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE)));
					sentImageFL.setVisibility(View.GONE);
				}else if(messageType.equalsIgnoreCase(XmppConstants.TYPE_IMAGE)){
					sentImageFL.setVisibility(View.VISIBLE);
					sentImage.setTag("Sent Image");
					imageLoader.displayImage(cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE)),sentImage,false);
					sentMessageTV.setVisibility(View.GONE);
					sentImageFL.setTag(cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE)));
				}
				
				sentTimeTV.setText(currenttime);

			}else if (coversationType.equalsIgnoreCase("2")){
				sentChatView.setVisibility(View.GONE);
				receivedChatView.setVisibility(View.VISIBLE);
				if(messageType.equalsIgnoreCase(XmppConstants.TYPE_TEXT)){
					receivedImageFL.setVisibility(View.GONE);
					receivedMessageTV.setVisibility(View.VISIBLE);
					receivedMessageTV.setText(cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE)));
				}else if(messageType.equalsIgnoreCase(XmppConstants.TYPE_IMAGE)){
					receivedImageFL.setVisibility(View.VISIBLE);
					receivedMessageTV.setVisibility(View.GONE);
					receivedImage.setTag("Received Image");
					imageLoader.displayImage(cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE)),receivedImage,false);
					receivedImageFL.setTag(cursor.getString(cursor.getColumnIndex(AppDatabaseConstants.COLUMN_MESSAGE)));
				}
				
				receivedTimeTV.setText(currenttime);
			}

		}
		public static final ViewHolder get(View parent) {
			return (ViewHolder) parent.getTag();
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			FrameLayout sentImageFL = (FrameLayout)v.findViewById(R.id.fl_media_sent);
			FrameLayout recievedImageFL = (FrameLayout)v.findViewById(R.id.fl_media_received);
			
			if(sentImageFL!=null && sentImageFL.getTag()!=null){
				Intent intent = new Intent(sentImageFL.getContext(),FullScreenImageActivity.class); 
				intent.putExtra("current_image", sentImageFL.getTag().toString().trim());
				intent.putExtra("userJid", userJid);
				intent.putExtra("is_group_chat", false);
				sentImageFL.getContext().startActivity(intent);
				
			}else if (recievedImageFL!=null && recievedImageFL.getTag()!=null) {
				Intent intent = new Intent(recievedImageFL.getContext(),FullScreenImageActivity.class); 
				intent.putExtra("current_image", recievedImageFL.getTag().toString().trim());
				intent.putExtra("userJid", userJid);
				intent.putExtra("is_group_chat", false);
				recievedImageFL.getContext().startActivity(intent);
			}
			
			
			
		}

	}
	
	public static String getDate(long milliSeconds, String dateFormat) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		DateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public static String Convert24to12(String time) {
		String convertedTime = "";
		try {
			SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
			SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
			Date date = (Date) parseFormat.parse(time);
			convertedTime = displayFormat.format(date);
			System.out.println("convertedTime : " + convertedTime);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return convertedTime;
		// Output will be 10:23 PM
	}
}
