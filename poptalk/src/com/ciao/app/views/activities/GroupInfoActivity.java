package com.ciao.app.views.activities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.XmppConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.datamodel.MediaBean;
import com.ciao.app.datamodel.MemberBean;
import com.ciao.app.imagecropper.Crop;
import com.ciao.app.imageloader.ImageLoader;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.backgroundtasks.UpdateGroupImageAsyncTask;
import com.ciao.app.service.ProfilePicUploaderService;
import com.ciao.app.utils.AppUtils;

/*
 * This screen show the UI for group info like group name  ,group media,group members etc.
 */
public class GroupInfoActivity extends Activity implements OnClickListener {
	private TextView groupNameTV, participantsTV, mediaCountTV,createdAt,createdByTV;
	private List<MemberBean> groupMemeberList;
	private List<MediaBean> groupMediaTypeList;
	private LinearLayout groupMemberLL, groupMediaLL;
	private RelativeLayout groupMediaRL;
	private static ImageLoader imageLoader;
	private ImageView groupMediaPic;
	LayoutInflater inflater;
	String groupID,groupJID;
	private String groupCreationTime;
	private String groupAdminName;
	private ImageView groupIconIV,editGroupInfoIV;
	private String groupName;
	private AlertDialog dialog;
	private ArrayAdapter<String> adapter;
	private String[] items = new String[] { "Take from camera","Select from gallery" };
	public static Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 3;
	private String thumbNailImage = "";
	private String sd_card_path = android.os.Environment.getExternalStorageDirectory() + "/Ciao/Images";
	private Button exitGroupLayout;
	private boolean isGroupMember;
	String groupIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_info);
		adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, items);
		Intent intent = getIntent();
		if(intent!=null){
			groupName = intent.getStringExtra("_name");
			groupIcon = intent.getStringExtra("_pic");
			groupID = intent.getStringExtra("_groupjid");
			isGroupMember = intent.getBooleanExtra("is_group_member", true);
		}
		groupJID = groupID.split("@")[0];
		groupCreationTime = ApplicationDAO.getInstance(this).getGroupCreationTime(groupJID);
		imageLoader = ImageLoader.getInstance(this);
		groupNameTV = (TextView) findViewById(R.id.tv_grp_name);
		groupMemberLL = (LinearLayout) findViewById(R.id.ll_member_list);
		groupMediaLL = (LinearLayout) findViewById(R.id.ll_media);
		groupMediaRL = (RelativeLayout) findViewById(R.id.rl_grp_media);
		participantsTV = (TextView) findViewById(R.id.tv_participants_count);
		mediaCountTV = (TextView) findViewById(R.id.tv_media_count);
		createdAt = (TextView) findViewById(R.id.tv_grp_created_at);
		createdByTV = (TextView) findViewById(R.id.tv_grp_created_by);
		groupIconIV = (ImageView)findViewById(R.id.iv_grp_img);
		editGroupInfoIV = (ImageView)findViewById(R.id.edit_grp_info);
		exitGroupLayout = (Button)findViewById(R.id.bt_exit_group);
		groupIconIV.setTag("GroupImage");
		groupMemeberList = ApplicationDAO.getInstance(this).getGroupMemeberList(groupJID);
		groupNameTV.setText(groupName);
		if(groupIcon!=null&&groupIcon.length()>0){
			imageLoader.displayImage(groupIcon, groupIconIV, true);	
		}else{
			groupIconIV.setImageResource(R.drawable.user_avtar_for_group);
		}

		participantsTV.setText(groupMemeberList.size() + " OF 30");

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Add group member row ui to screen
		for (int index = 0; index < groupMemeberList.size(); index++) {
			View view = inflater.inflate(R.layout.custom_members_list,groupMemberLL, false);
			((TextView) view.findViewById(R.id.tv_member_name)).setText(groupMemeberList.get(index).getName());
			ImageView profileIV = ((ImageView) view.findViewById(R.id.iv_member_img));
			profileIV.setTag("profile_iv");
			imageLoader.displayImage(groupMemeberList.get(index).getProfilePic(), profileIV, true);

			if(groupMemeberList.get(index).isAdmin()){
				groupAdminName = groupMemeberList.get(index).getName();
				((TextView) view.findViewById(R.id.txt_group_admin)).setVisibility(View.VISIBLE);
			}else{
				((TextView) view.findViewById(R.id.txt_group_admin)).setVisibility(View.INVISIBLE);
			}
			groupMemberLL.addView(view);
		}

		// show visibility of Group Media
		Boolean mediaType = ApplicationDAO.getInstance(this)
				.isGroupMediaAvailable(groupJID);
		if (mediaType) {
			groupMediaRL.setVisibility(View.VISIBLE);
			groupMediaTypeList = new ArrayList<MediaBean>();
			groupMediaTypeList = ApplicationDAO.getInstance(this)
					.getGroupMedia(groupJID);

			mediaCountTV.setText(groupMediaTypeList.size() + "");

			for (int index = 0; index < groupMediaTypeList.size(); index++) {
				if (groupMediaTypeList.get(index).getMediaType()
						.equalsIgnoreCase(XmppConstants.TYPE_IMAGE)) {

					LinearLayout layout = new LinearLayout(this);
					layout.setLayoutParams(new LayoutParams(80, 80));
					layout.setGravity(Gravity.CENTER);

					groupMediaPic = new ImageView(this);
					groupMediaPic.setLayoutParams(new LayoutParams(70, 70));
					groupMediaPic.setBackgroundColor(Color
							.parseColor("#00FF00"));
					groupMediaPic.setScaleType(ImageView.ScaleType.FIT_XY);

					imageLoader.displayImage(groupMediaTypeList.get(index)
							.getMediaFilePath(), groupMediaPic, false);
					groupMediaPic.setTag(groupMediaTypeList.get(index)
							.getMediaFilePath());

					layout.addView(groupMediaPic);
					groupMediaLL.addView(layout);

					groupMediaPic.setOnClickListener(this);

				} else {
					// for video
				}

			}

		} else {
			groupMediaRL.setVisibility(View.GONE);
		}
		editGroupInfoIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Goto screen where user can change the group name
				Intent intent = new Intent(GroupInfoActivity.this,EditgroupInfoActivity.class);
				intent.putExtra("group_name", groupName);
				intent.putExtra("group_jid", groupJID);
				startActivityForResult(intent, 1001);

			}
		});
		groupIconIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Change group icon
				createImageOptionDialog();
			}
		});
		exitGroupLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.showTost(GroupInfoActivity.this, "Coming soon");
				//XMPPChatService.leaveGroup(groupJID);
				/*ApplicationDAO.getInstance(GroupInfoActivity.this).leaveGroup(groupJID);
				goback(false);*/
			}
		});
	}

	public void goToPreviousScreen(View view) {
		goback(true);
	}

	// back from this screen
	private void goback(boolean isGroupMemebr) {
		Intent backIntent = new Intent();
		backIntent.putExtra("group_name", groupName);
		backIntent.putExtra("isGroupMemebr", isGroupMemebr);
		setResult(RESULT_OK, backIntent);
		finish();
	}

	@Override
	public void onClick(View v) {

		// on tap of media ,go to screen where user can see media in full screen
		Intent intent = new Intent(GroupInfoActivity.this,FullScreenImageActivity.class);
		intent.putExtra("current_image", groupMediaPic.getTag().toString().trim());
		intent.putExtra("userJid", groupID);
		intent.putExtra("is_group_chat", true);
		startActivity(intent);

	}

	@Override
	protected void onResume() {

		super.onResume();
		Date date = new Date(Long.parseLong(groupCreationTime));
		SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy hh:mm a", Locale.US);
		String dateString = formatter.format(date);
		String formattedDate = getResources().getString(R.string.txt_group_creation_time,dateString);
		String formattedCreatedBy = getResources().getString(R.string.txt_group_admin_name,groupAdminName);
		createdAt.setText(formattedDate);
		createdByTV.setText(formattedCreatedBy);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1001){
			if(resultCode == RESULT_OK){
				groupName = 	data.getStringExtra("group_name");
				groupNameTV.setText(groupName);
				ApplicationDAO.getInstance(this).updateGroupName(groupName, groupJID);
				XMPPChatService.updateGroupName(groupName, groupJID);
				AppUtils.showTost(this, "Group name changed successfully");
			}
		}

		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {
			case PICK_FROM_CAMERA:
				if (AppUtils.isSdCardAvailable()) {
					if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						// only for gingerbread and newer versions
						sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
					} else {
						sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
					}
				}
				beginCrop(mImageCaptureUri);
				break;
			case PICK_FROM_FILE:
				beginCrop(data.getData());
				break;
			case Crop.REQUEST_CROP:
				handleCrop(resultCode, data);
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent backIntent = new Intent();
		backIntent.putExtra("group_name", groupName);
		setResult(RESULT_OK, backIntent);
		finish();
	}

	private void createImageOptionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
				// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(),
							"/Ciao/Images/temp_pic.png"));
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);
					try {
						intent.putExtra("return-data", true);
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { // pick from file
					Intent intent;
					if (Build.VERSION.SDK_INT < 19) {
						intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent,
								"Complete action using"), PICK_FROM_FILE);
					} else {
						intent = new Intent(Intent.ACTION_PICK);
						intent.setType("image/*");
						intent.putExtra(MediaStore.EXTRA_MEDIA_ALBUM,mImageCaptureUri);
						startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_FILE);
					}

				}
			}
		});
		dialog = builder.create();
		dialog.show();

	}

	private void beginCrop(Uri source) {
		Uri outputUri = Uri.fromFile(new File(AppConstants.APP_IMAGE_BASE_FOLDER, "temp_pic.png"));
		new Crop(source).output(outputUri).asSquare().start(GroupInfoActivity.this, Crop.REQUEST_CROP);;
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == Activity.RESULT_OK) {

			Bitmap bitmap = AppUtils.decodeFile(AppConstants.APP_IMAGE_BASE_FOLDER+ "/temp_pic.png", 100, 100);
			if (bitmap != null) {
				groupIconIV.setImageBitmap(bitmap);
				new UpdateGroupImageAsyncTask(this, groupJID).execute();
			}

		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(),Toast.LENGTH_SHORT).show();
		}

	}



}

