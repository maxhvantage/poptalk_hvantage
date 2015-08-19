package com.ciao.app.views.activities;

import java.io.File;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONObject;

import com.poptalk.app.R;
import com.ciao.app.adapters.ChatAdapterNew;
import com.ciao.app.adapters.GroupChatAdapter;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.constants.XmppConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.imageloader.ImageLoader;
import com.ciao.app.interfaces.MedaiUploadListener;
import com.ciao.app.netwrok.backgroundtasks.MediaUploadHandler;
import com.ciao.app.utils.AppUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/*
 * This Activity show the group chat window
 */
public class GroupChatActivity extends Activity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,MedaiUploadListener {
	private ListView chatLV;
	private EditText chatET;
	private ImageView /*sendIV,audioIV,*/ plusIV, backForAddNewUserIV;
	private LinearLayout chatOptionPopUp;
	private ImageView chatOptionMenuIV;
	private RelativeLayout addChatRL;
	//private ChatAdapter customadapter;
	private GroupChatAdapter customadapter;
	private String userNumber;
	private static String groupName;
	private String groupJID;
	private static String groupPic;
	private static TextView  groupNameTV,userContactTV,sendTV;
	private String messageType,filePath,type;
	private Uri fileUri;
	private Context mContext;
	private static ImageLoader imageLoader;
	private static ImageView groupIV;
	private String groupAdminJid,myJid;
	private boolean isGroupMemeber = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_chat);
		mContext = this;
		imageLoader = ImageLoader.getInstance(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Intent intent = getIntent();
		if(intent!=null){
			Bundle bundle =  intent.getExtras();
			userNumber =  bundle.getString("jid");
			groupName = bundle.getString("user_name");
			groupPic = bundle.getString("user_pic");
			groupJID = userNumber;
			ApplicationDAO.getInstance(GroupChatActivity.this).updateUnreadCount(groupJID);
			isGroupMemeber = ApplicationDAO.getInstance(this).isMemberOfThisGroup(groupJID);
			groupJID = groupJID+XmppConstants.GROUP_HOST;
			
		}
		intViews();
		intListiner();
		customadapter = new GroupChatAdapter(GroupChatActivity.this,null,groupJID);
		chatLV.setAdapter(customadapter);
		getLoaderManager().restartLoader(0, null, this);

	}

	private void toggleChatMenuPopUp() {
		showSharingDialog();
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppSharedPreference.getInstance(this).setChatSceenVisbility(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppSharedPreference.getInstance(this).setChatSceenVisbility(true);
	}

	private void intViews() {
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
		addChatRL = (RelativeLayout) findViewById(R.id.rl_add_chat);
		chatOptionPopUp = (LinearLayout) findViewById(R.id.ll_chat_options_pop_up);
		chatOptionMenuIV = (ImageView) findViewById(R.id.iv_chat_option);
		chatLV = (ListView) findViewById(R.id.lv_chat);
		chatET = (EditText) findViewById(R.id.et_chat_txt);
		sendTV = (TextView)findViewById(R.id.tv_send);
		chatET.setTypeface(type);
		plusIV = (ImageView) findViewById(R.id.iv_add_new_chat);
		backForAddNewUserIV = (ImageView) findViewById(R.id.iv_back_from_add_new_chat_user);
		groupIV = ((ImageView) findViewById(R.id.civ_contact_image));
		groupIV.setTag("GroupIcon");
		sendTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if(chatET.getText().toString().trim().length()>0){
					sendMessage(XmppConstants.TYPE_TEXT, chatET.getText().toString(), null);
					chatET.setText("");
				}
			}
		});
		groupNameTV = (TextView)findViewById(R.id.tv_message_contact_name);
		userContactTV = (TextView)findViewById(R.id.tv_message_contact_number);
		chatET.addTextChangedListener(textWatcher);
		
		groupNameTV.setText(groupName);
		userContactTV.setText(userNumber);
		if (groupPic != null&&(!groupPic.equalsIgnoreCase(""))) {
			imageLoader.displayImage(groupPic, groupIV, true); 
		} else {
			((ImageView)findViewById(R.id.civ_contact_image)).setImageResource(R.drawable.user_avtar_for_group);
		}
		groupAdminJid = ApplicationDAO.getInstance(this).getGroupAdmin(groupJID.split("@")[0]);
		if(XMPPChatService.getConnection()!=null){
			myJid = AppSharedPreference.getInstance(mContext).getUserCountryCode()+AppSharedPreference.getInstance(mContext).getUserPhoneNumber();
			if(groupAdminJid.equalsIgnoreCase(myJid)){
				plusIV.setVisibility(View.VISIBLE);	
			}else{
				plusIV.setVisibility(View.INVISIBLE);
			}
		}else{
			plusIV.setVisibility(View.INVISIBLE);
		}
        if(!isGroupMemeber){
        	showUIforNonGroupMember();
        	
        }

	}

	private void showUIforNonGroupMember() {
		chatET.setText("You can't send message to this group because you're no longer a participant.");
    	chatET.setFocusable(false);
    	chatET.setEnabled(false);
    	sendTV.setEnabled(false);
    	sendTV.setVisibility(View.GONE);
    	chatOptionMenuIV.setEnabled(false);
    	chatOptionMenuIV.setClickable(false);
    	chatOptionMenuIV.setVisibility(View.GONE);
    	plusIV.setClickable(false);
    	plusIV.setEnabled(false);
    	plusIV.setVisibility(View.GONE);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_chat_option:
			toggleChatMenuPopUp();
			hideKeyPad();
			break;
		case R.id.iv_add_new_chat:
			Intent addParticipantIntent = new Intent(this,AddParticipantToGroupActivity.class);
			addParticipantIntent.putExtra("group_name", groupName);
			addParticipantIntent.putExtra("group_jid", groupJID);
			startActivity(addParticipantIntent);
			break;
		case R.id.iv_back_from_add_new_chat_user:
			addChatRL.setVisibility(View.GONE);
		default:
			break;
		}
	}

	private void intListiner() {
		chatOptionMenuIV.setOnClickListener(this);
		addChatRL.setOnClickListener(this);
		plusIV.setOnClickListener(this);
		backForAddNewUserIV.setOnClickListener(this);

	}

	public void backFromChatAcitivity(View view) {
		finish();
	}

	private void sendMessage(String type, String messageText, String localFilePath) {
		myJid = AppSharedPreference.getInstance(mContext).getUserCountryCode()+AppSharedPreference.getInstance(mContext).getUserPhoneNumber();
		XMPPConnection xmppConnection = XMPPChatService.getConnection();
		Message msg = new Message(groupJID, Message.Type.groupchat);
		msg.setProperty(XmppConstants.XMP_SENT_TIME, System.currentTimeMillis());
		msg.setBody(messageText);
		msg.setSubject(type);
		ApplicationDAO.getInstance(this).updateLastMessage(groupJID.split("@")[0], System.currentTimeMillis(), messageText, 1, false,true);
		ApplicationDAO.getInstance(this).saveGroupChatMessageInDb(this, messageText, groupJID.split("@")[0], myJid, msg.getPacketID(), XmppConstants.WAITING,  XmppConstants.SEND, type, localFilePath, false);
		if (xmppConnection != null && xmppConnection.isConnected()) {
			MultiUserChat multiUserChat  = new MultiUserChat(xmppConnection, groupJID);
			try {
				multiUserChat.sendMessage(msg);
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}else{
			startService(new Intent(this,XMPPChatService.class));
		}
	}

	private void sendMedaiMessage(String type, String messageText){
		XMPPConnection xmppConnection = XMPPChatService.getConnection();
		if (xmppConnection != null && xmppConnection.isConnected()) {
			Message msg = new Message(groupJID, Message.Type.groupchat);
			msg.setProperty(XmppConstants.XMP_SENT_TIME, System.currentTimeMillis());
			msg.setBody(messageText);
			msg.setSubject(type);
			if (xmppConnection != null && xmppConnection.isConnected()) {
				MultiUserChat multiUserChat  = new MultiUserChat(xmppConnection, groupJID);
				try {
					multiUserChat.sendMessage(msg);
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}else{
				startService(new Intent(this,XMPPChatService.class));
			}
			//xmppConnection.sendPacket(msg);
		}
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, Uri.parse(AppDatabaseConstants.GROUP_CHAT_DETAIL_CONTENT_URI_STRING),
				null,
				AppDatabaseConstants.COLUMN_GROUP_JID + " = ? ", new String[]{groupJID.split("@")[0]},
				null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		customadapter.swapCursor(data);
		chatLV.post(new Runnable() {
			@Override
			public void run() {
				chatLV.setSelection(customadapter.getCount());
			}
		});

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		customadapter.swapCursor(null);
	}

	public void goToContactDetailScreen(View view){
		if(isGroupMemeber){
		Intent intent = new Intent(this, GroupInfoActivity.class);
		intent.putExtra("_name",groupName);
		intent.putExtra("_pic",groupPic);
		intent.putExtra("_groupjid", groupJID);
		intent.putExtra("is_group_member", isGroupMemeber);
		startActivityForResult(intent, 1111);
		}
	}

	private void hideKeyPad(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(chatET.getWindowToken(), 0);
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable text) {
			//audioIV,sendIV;
			/*if(text!=null&&text.length()>0){
				audioIV.setVisibility(View.GONE);
				sendIV.setVisibility(View.VISIBLE);
			}else{
				audioIV.setVisibility(View.VISIBLE);
				sendIV.setVisibility(View.GONE);
			}*/
		}
	};


	private void uploadMedia(String path,String type){
		//ContactDAO.getInstance(this).saveChatMessageInDb(this, path, groupJID, Long.toString(System.currentTimeMillis()), XmppConstants.WAITING, XmppConstants.SEND,type,path,false);
		ApplicationDAO.getInstance(this).saveGroupChatMessageInDb(this, path, groupJID.split("@")[0], null, Long.toString(System.currentTimeMillis()), XmppConstants.WAITING, XmppConstants.SEND, type, path, false);
		ApplicationDAO.getInstance(this).updateLastMessage(groupJID.split("@")[0], System.currentTimeMillis(), path, 1, false,true);
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			reqEntity.addPart("media", new FileBody(new File(path)));
			reqEntity.addPart("media_type",new StringBody(new String(type)));
			new MediaUploadHandler(this,this,true).execute(reqEntity);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("IsCareNeeded", "Exception=" + e.toString());
		}
	}

	@Override
	public void getServerResponse(JSONObject json) {
		try{
			if(json!=null){
				String errorCode = json.getString("error_code");
				if (errorCode.equalsIgnoreCase("0")) {
					JSONObject jsonObject = json.getJSONObject("result");
					String media_type = jsonObject.getString("media_type");
					String media_url = jsonObject.getString("url");
					sendMedaiMessage(media_type,media_url);

				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void showSharingDialog(){
		try {
			final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
			dialog.setContentView(R.layout.dialog_chat_media_chooser);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);
			dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Camera();
					dialog.dismiss();
				}
			});

			dialog.findViewById(R.id.tv_choose_photo).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, 1001);
					dialog.dismiss();
				}
			});

			/*dialog.findViewById(R.id.tv_choose_video).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, 1002);
					dialog.dismiss();
				}
			});

			 dialog.findViewById(R.id.tv_sticker).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    findViewById(R.id.gridview).setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.tv_location).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    String location = SharedPreferenceWriter.getInstance(ChatActivity.this).getString(SPreferenceKey.ADDRESS, "") + ":" + currentLat + ":" + currentLng;
                    sendMessage("location", location, null);
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.tv_contact).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(pickContactIntent, 1003);
                    dialog.dismiss();
                }
            });*/
			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void Camera() {
		filePath = AppConstants.APP_IMAGE_BASE_FOLDER +"/"+ System.currentTimeMillis() + ".jpg";
		File file = new File(filePath);
		fileUri = Uri.fromFile(file);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, 1015);
		/*final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.camera));
		builder.setPositiveButton(getResources().getString(R.string.photo), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				filePath = AppConstants.APP_IMAGE_BASE_FOLDER +"/"+ System.currentTimeMillis() + ".jpg";
				File file = new File(filePath);
				fileUri = Uri.fromFile(file);
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(intent, 1015);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.video), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				filePath = AppConstants.APP_VIDEO_BASE_FOLDER +"/"+ System.currentTimeMillis() + ".mp4";
				File file = new File(filePath);
				Uri outFileUri = Uri.fromFile(file);
				Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outFileUri);
				startActivityForResult(intent, 1016);
				dialog.dismiss();
			}
		});
		builder.show();*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null || requestCode == 1015 || requestCode == 1016){
			if(requestCode == 1001 && resultCode == RESULT_OK){
				Uri uri = data.getData();
				filePath = AppUtils.getRealPathFromURI(this, uri, MediaStore.Images.ImageColumns.DATA);
				type = "image";
				if(filePath!=null){
					uploadMedia(filePath,type);
				}else {
					AppUtils.showTost(this,"Unable to take picture,Please try again ");
				}

			}else if(requestCode == 1002 && resultCode == RESULT_OK){
				Uri uri = data.getData();
				filePath  = AppUtils.getRealPathFromURI(this, uri, MediaStore.Video.VideoColumns.DATA);
				type = "video";
				if(filePath!=null){
					uploadMedia(filePath,type);
				}else {
					AppUtils.showTost(this,"Unable to take picture,Please try again ");
				}
			}else if(requestCode == 1015 && resultCode == RESULT_OK){
				type = "image";
				if(filePath!=null){
					uploadMedia(filePath,type);
				}else {
					AppUtils.showTost(this,"Unable to take picture,Please try again ");
				}
			}else if(requestCode == 1016 && resultCode == RESULT_OK){
				type = "video";
				if(filePath!=null){
					uploadMedia(filePath,type);
				}else {
					AppUtils.showTost(this,"Unable to take picture,Please try again ");
				}
			}else if (requestCode == 1111 && resultCode == RESULT_OK) {
				String groupNewName = data.getStringExtra("group_name");
				boolean isGroupMember = data.getBooleanExtra("isGroupMemebr", true);
				if(groupNewName!=null){
					groupNameTV.setText(groupNewName);

				}
				if(isGroupMember){
					
				}

			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on scren orientation
		// changes
		outState.putParcelable("file_uri", fileUri);
	}

	/*
	 * Here we restore the fileUri again
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// get the file url
		fileUri = savedInstanceState.getParcelable("file_uri");
	}

	public static void updateGroupName(String groupChanged){
		if(groupChanged!=null){
			if(groupChanged.contains("http://")||groupChanged.contains("https://")){
				if(groupIV!=null){
					imageLoader.displayImage(groupChanged, groupIV, true);
					groupPic = groupChanged;
				}
			}else{
				if(groupNameTV!=null){
					groupNameTV.setText(groupChanged);
					groupName =groupChanged;
					
				}
			}
		}

	}


}

