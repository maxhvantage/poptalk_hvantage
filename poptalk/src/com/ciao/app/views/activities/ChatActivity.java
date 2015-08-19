package com.ciao.app.views.activities;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.utils.DialogUtils;
import com.poptalk.app.R;
import com.ciao.app.adapters.ChatAdapterNew;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppDatabaseConstants;
import com.ciao.app.constants.XmppConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.interfaces.MedaiUploadListener;
import com.ciao.app.netwrok.NetworkStatus;
import com.ciao.app.netwrok.backgroundtasks.MediaUploadHandler;
import com.ciao.app.utils.AppUtils;
import com.csipsimple.utils.CallBaseActivity;


/**
 * Created by rajat on 1/2/15.
 * This screen show the chat window where user can chat with selected  contact
 */
public class ChatActivity extends CallBaseActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, MedaiUploadListener {
    private ListView chatLV;
    private EditText chatET;
    private ImageView /*sendIV,audioIV,*/ plusIV, backForAddNewUserIV;
    private LinearLayout chatOptionPopUp;
    private ImageView chatOptionMenuIV;
    private RelativeLayout addChatRL;
    //private ChatAdapter customadapter;
    private ChatAdapterNew customadapter;
    private String userNumber, userName, userJID, userPic, userID;
    private TextView userNameTV, userContactTV, sendTV;
    private String messageType, filePath, type;
    private Uri fileUri;
    private ImageView mCallIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        intViews();
        intListiner();

        Intent intent = getIntent();
        if (intent != null) {

            Bundle bundle = intent.getExtras();
            userJID = bundle.getString("jid");
            userNumber = bundle.getString("user_name");
            userPic = bundle.getString("user_pic");
            userNameTV.setText(userNumber);

            try {
                if (userJID == null) {
                    userJID = getCommUser(userNumber);
                }

                if (!userJID.contains("@")) {
                    userJID += "@" + XmppConstants.HOST;
                }

//			if(userID!=null){
                String userName = ApplicationDAO.getInstance(this).getUserNameFromCommUser(userJID, userNumber);
                if (userName != null) {
                    userNameTV.setText(userName);
                    userContactTV.setText(AppUtils.formatNumberUsingParenthesesforCallLog(userNumber, this));
                    userContactTV.setTag(userNumber);
                    userContactTV.setVisibility(View.VISIBLE);
                } else {
                    userContactTV.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                Log.e("chat_activity", "Error getting comm user of contact!");
                return;
            }

//			}else{
//				userID = ApplicationDAO.getInstance(this).getUserIdFromNumber(userNumber);
//
//				try {
//					userJID = getCommUser(userNumber);
//				} catch (Exception e) {
//					Log.e("chat_activity", "Error getting comm user of contact!");
//					return;
//				}
//				userJID = userJID+"@"+XmppConstants.HOST;
//				userContactTV.setText(AppUtils.formatNumberUsingParenthesesforCallLog(userNumber, this));
//				userContactTV.setVisibility(View.GONE);
//			}

            if (userPic != null && (!userPic.equalsIgnoreCase(""))) {
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(userPic)));
                    ((ImageView) findViewById(R.id.civ_contact_image)).setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((ImageView) findViewById(R.id.civ_contact_image)).setImageResource(R.drawable.user_avtar);
            }
        }
        customadapter = new ChatAdapterNew(ChatActivity.this, null, userJID);
        chatLV.setAdapter(customadapter);
        getLoaderManager().restartLoader(0, null, this);
        ApplicationDAO.getInstance(ChatActivity.this).updateUnreadCount(userJID);

    }

    private String getCommUser(String userNumber) throws Exception {
        String commUser = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String phoneNumber = params[0];
                String userId = ApplicationDAO.getInstance(ChatActivity.this).getUserIdFromNumber(phoneNumber);
                String commUser = ApplicationDAO.getInstance(ChatActivity.this).getCommUserFromUserId(userId);
                if (commUser == null) {
                    // go to API to store this number
                    try {
                        String response = NetworkCall.getInstance(ChatActivity.this).hitNetwork(AppNetworkConstants.GET_COMM_USER_BY_PHONE + "?did=" + phoneNumber);
                        JSONObject jsonResponse = new JSONObject(response);
                        commUser = jsonResponse.getString("comm_user");
                    } catch (Exception e) {
                        Log.e("call_api", "error to get cmm_user");
                    }

                }
                return commUser;
            }
        }.execute(userNumber).get();
        return commUser;
    }

    private void toggleChatMenuPopUp() {
        /*int popVisibility = chatOptionPopUp.getVisibility();
        if (popVisibility == View.GONE) {
            chatOptionPopUp.setVisibility(View.VISIBLE);
        } else {
            chatOptionPopUp.setVisibility(View.GONE);
        }*/
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
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        addChatRL = (RelativeLayout) findViewById(R.id.rl_add_chat);
        chatOptionPopUp = (LinearLayout) findViewById(R.id.ll_chat_options_pop_up);
        chatOptionMenuIV = (ImageView) findViewById(R.id.iv_chat_option);
        chatLV = (ListView) findViewById(R.id.lv_chat);
        chatET = (EditText) findViewById(R.id.et_chat_txt);
        mCallIV = (ImageView) findViewById(R.id.iv_call);
        chatET.setTypeface(type);
		/*sendIV = (ImageView) findViewById(R.id.iv_send_chat);
		audioIV = (ImageView)findViewById(R.id.iv_voice_chat);*/
        sendTV = (TextView) findViewById(R.id.tv_send);
        plusIV = (ImageView) findViewById(R.id.iv_add_new_chat);
        backForAddNewUserIV = (ImageView) findViewById(R.id.iv_back_from_add_new_chat_user);
        sendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chatET.getText().toString().trim().length() > 0) {
                    sendMessage(XmppConstants.TYPE_TEXT, chatET.getText().toString());
                    chatET.setText("");
                }


            }
        });
        mCallIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkStatus.isConected(ChatActivity.this)) {
                    if (userID == null) {
                        //Contact is not save on our device
                        placeCallWithOption(null, false, userNumber);
                        AppSharedPreference.getInstance(ChatActivity.this).setIsCaioOutCall(true);
                    } else {
                        String ciaoNumber = ApplicationDAO.getInstance(ChatActivity.this).getCiaoNumberFromId(userID);
                        placeCallWithOption(null, false, ciaoNumber);
                        AppSharedPreference.getInstance(ChatActivity.this).setIsCaioOutCall(false);
                    }

                } else {
                    AppUtils.showTost(ChatActivity.this, "Please check you internet connection");
                }

            }
        });
		/*audioIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});*/
        userNameTV = (TextView) findViewById(R.id.tv_message_contact_name);
        userContactTV = (TextView) findViewById(R.id.tv_message_contact_number);
        chatET.addTextChangedListener(textWatcher);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_chat_option:
                toggleChatMenuPopUp();
                hideKeyPad();
                break;
            case R.id.iv_add_new_chat:
                //addChatRL.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_back_from_add_new_chat_user:
                //addChatRL.setVisibility(View.GONE);
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

    //This method is used to send the text message and save the same message to local db.
    private void sendMessage(String type, String messageText) {
        String phoneNumber = AppSharedPreference.getInstance(this).getUserCiaoNumber();

        if (phoneNumber == null) {
            phoneNumber = AppSharedPreference.getInstance(this).getUserCountryCode()+AppSharedPreference.getInstance(this).getUserPhoneNumber();
        }

        XMPPConnection xmppConnection = XMPPChatService.getConnection();
        Message msg = new Message(userJID, Message.Type.chat);
        msg.setProperty(XmppConstants.XMP_SENT_TIME, System.currentTimeMillis());
        msg.setProperty("phone_number", phoneNumber);
        msg.setBody(messageText);
        msg.setSubject(type);
        ApplicationDAO.getInstance(this).saveChatMessageInDb(this, messageText, userJID, userContactTV.getTag().toString(), msg.getPacketID(), XmppConstants.WAITING, XmppConstants.SEND, type, null, false);
        if (xmppConnection != null && xmppConnection.isConnected()) {
            xmppConnection.sendPacket(msg);
        } else {
            startService(new Intent(this, XMPPChatService.class));
        }
    }

    //This method is used to send the image and save the same image to local db.
    private void sendMedaiMessage(String type, String messageText) {
        XMPPConnection xmppConnection = XMPPChatService.getConnection();
        if (xmppConnection != null && xmppConnection.isConnected()) {
            Message msg = new Message(userJID, Message.Type.chat);
            msg.setProperty(XmppConstants.XMP_SENT_TIME, System.currentTimeMillis());
            msg.setBody(messageText);
            msg.setSubject(type);
            xmppConnection.sendPacket(msg);
            AppUtils.playChatBubbleSound(this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String appUserID = AppSharedPreference.getInstance(this).getUserID();
        return new CursorLoader(this, Uri.parse(AppDatabaseConstants.CHAT_USER_MESSAGE_CONTENT_URI_STRING),
                null,
                AppDatabaseConstants.USER_CHAT_ID + " = ? AND " + AppDatabaseConstants.KEY_APP_USER_ID + " =? ", new String[]{userJID, appUserID},
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

    //Navigate to contact detail screen on tap of header at the top of screen.
    public void goToContactDetailScreen(View view) {
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        intent.putExtra("_id", userID);
        intent.putExtra("_name", userName);
        intent.putExtra("_pic", userPic);
        startActivity(intent);
    }

    private void hideKeyPad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
            if (text != null && text.length() > 0) {
				/*audioIV.setVisibility(View.GONE);
				sendIV.setVisibility(View.VISIBLE);*/
            } else {
				/*audioIV.setVisibility(View.VISIBLE);
				sendIV.setVisibility(View.GONE);*/
            }
        }
    };

    // Upload sent image to server 
    private void uploadMedia(String path, String type) {
        String phoneNumber = AppSharedPreference.getInstance(this).getUserPhoneNumber();
        ApplicationDAO.getInstance(this).saveChatMessageInDb(this, path, userJID, phoneNumber, Long.toString(System.currentTimeMillis()), XmppConstants.WAITING, XmppConstants.SEND, type, path, false);
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {
            reqEntity.addPart("media", new FileBody(new File(path)));
            reqEntity.addPart("media_type", new StringBody(new String(type)));
            new MediaUploadHandler(this, this, true).execute(reqEntity);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("IsCareNeeded", "Exception=" + e.toString());
        }
    }

    @Override
    public void getServerResponse(JSONObject json) {
        try {
            if (json != null) {
                String errorCode = json.getString("error_code");
                if (errorCode.equalsIgnoreCase("0")) {
                    JSONObject jsonObject = json.getJSONObject("result");
                    String media_type = jsonObject.getString("media_type");
                    String media_url = jsonObject.getString("url");
                    sendMedaiMessage(media_type, media_url);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSharingDialog() {
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
        filePath = AppConstants.APP_IMAGE_BASE_FOLDER + "/" + System.currentTimeMillis() + ".jpg";
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
        if (data != null || requestCode == 1015 || requestCode == 1016) {
            if (requestCode == 1001 && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                filePath = AppUtils.getRealPathFromURI(this, uri, MediaStore.Images.ImageColumns.DATA);
                type = "image";
                if (filePath != null) {
                    uploadMedia(filePath, type);
                } else {
                    AppUtils.showTost(this, "Unable to take picture,Please try again ");
                }

            } else if (requestCode == 1002 && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                filePath = AppUtils.getRealPathFromURI(this, uri, MediaStore.Video.VideoColumns.DATA);
                type = "video";
                if (filePath != null) {
                    uploadMedia(filePath, type);
                } else {
                    AppUtils.showTost(this, "Unable to take picture,Please try again ");
                }
            } else if (requestCode == 1015 && resultCode == RESULT_OK) {
                type = "image";
                if (filePath != null) {
                    uploadMedia(filePath, type);
                } else {
                    AppUtils.showTost(this, "Unable to take picture,Please try again");
                }
            } else if (requestCode == 1016 && resultCode == RESULT_OK) {
                type = "video";
                if (filePath != null) {
                    uploadMedia(filePath, type);
                } else {
                    AppUtils.showTost(this, "Unable to take picture,Please try again ");
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

    public void makeCall(View view) {


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
