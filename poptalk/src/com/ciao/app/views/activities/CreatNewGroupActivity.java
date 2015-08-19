package com.ciao.app.views.activities;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.poptalk.app.R;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.imagecropper.Crop;
import com.ciao.app.utils.AppUtils;

/**
 * Created by rajat on 27/2/15.
 */
public class CreatNewGroupActivity extends Activity{
	private EditText mGroupSubject;
	private AlertDialog dialog;
	private ArrayAdapter<String> adapter;
	private String[] items = new String[]{"Take from camera", "Select from gallery"};
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private String thumbNailImage = "";
	private ImageView groupIcon;
	private String groupIconName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_group);
		mGroupSubject = (EditText)findViewById(R.id.et_group_subject);
		adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
		groupIcon = (ImageView)findViewById(R.id.iv_group_icon);
	}

	public void gotoSelectContactScreen(View view){
		if(mGroupSubject.getText().toString().length()>0){
			Intent intent = new Intent(this,CreateGroupChatAutoCompleterView.class);
			intent.putExtra("group_name", mGroupSubject.getText().toString());
			/*if(mImageCaptureUri!=null){
			intent.putExtra("group_icon", mImageCaptureUri.toString());
			}else{
				intent.putExtra("group_icon", mImageCaptureUri);
			}*/
			intent.putExtra("group_icon", thumbNailImage);
			startActivityForResult(intent, 1);
		}else{
			AppUtils.showTost(this, "Please enter the group subject");
		}

	}

	public void goToPreviousScreen(View view){
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		switch (requestCode) {
		case 1:
			if(resultCode == RESULT_OK){
				finish();
			}
			break;

		case PICK_FROM_FILE:

			if (resultCode == Activity.RESULT_OK){
				beginCrop(result.getData());
			}else {
				Log.e("PICK_FROM_FILE","Canceled");
			}
			break;

		case PICK_FROM_CAMERA:
			if (resultCode == RESULT_OK) {
				if (AppUtils.isSdCardAvailable()) {
					if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						// only for gingerbread and newer versions
						sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
					} else {
						sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
					}
				}
				beginCrop(mImageCaptureUri);

			}else {
				Log.e("PICK_FROM_CAMERA","Canceled");
			}
			break;
		case Crop.REQUEST_CROP:
			handleCrop(resultCode,result);
			break;
		}
	}

	public void chooseGroupIcon(View view){
		createImageOptionDialog();
	}

	private void createImageOptionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { //pick from camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					groupIconName = "group_icon"+System.currentTimeMillis()+".png";
					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/Ciao/Images/"+groupIconName));
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
					try {
						intent.putExtra("return-data", true);
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { //pick from file
					Intent intent;
					if(Build.VERSION.SDK_INT<19){
						intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
					}else{
						intent = new Intent(Intent.ACTION_PICK);
						intent.setType("image/*");
						intent.putExtra(MediaStore.EXTRA_MEDIA_ALBUM,mImageCaptureUri);
						startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
					}

				}
			}
		});
		dialog = builder.create();
		dialog.show();

	}
	
	 private void beginCrop(Uri source) {
	        Uri outputUri = Uri.fromFile(new File(AppConstants.APP_IMAGE_BASE_FOLDER,groupIconName));
	        new Crop(source).output(outputUri).asSquare().start(this);
	    }

	    private void handleCrop(int resultCode, Intent result) {
	        if (resultCode == RESULT_OK) {
	            Bitmap bitmap = AppUtils.hanldeImageRotation(Crop.getOutput(result), this);
	            if(bitmap!=null){
	                thumbNailImage = AppConstants.APP_IMAGE_BASE_FOLDER+"/"+groupIconName;
	                groupIcon.setImageBitmap(bitmap);

	            }
	        } else if (resultCode == Crop.RESULT_ERROR) {
	            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
	            thumbNailImage = "";
	        }

	    }
}
