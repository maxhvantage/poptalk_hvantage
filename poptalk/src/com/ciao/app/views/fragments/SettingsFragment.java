package com.ciao.app.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.chat.XMPPChatService;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.constants.AppNetworkConstants;
import com.ciao.app.database.ApplicationDAO;
import com.ciao.app.imagecropper.Crop;
import com.ciao.app.imageloader.ImageLoader;
import com.ciao.app.netwrok.NetworkCall;
import com.ciao.app.netwrok.RequestBean;
import com.ciao.app.netwrok.backgroundtasks.LogoutAsyntaskLoader;
import com.ciao.app.service.GCMIntentService;
import com.ciao.app.service.ProfilePicUploaderService;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.ciao.app.views.activities.BuyItemsActivity;
import com.ciao.app.views.activities.BuyNumberActivity;
import com.ciao.app.views.activities.CallActivity;
import com.ciao.app.views.activities.ChangePasswordActivity;
import com.ciao.app.views.activities.CountryListActivity;
import com.ciao.app.views.activities.HelpActivity;
import com.ciao.app.views.activities.SplashActivity;
import com.ciao.app.views.activities.TutorialActivity;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;
import com.csipsimple.service.SipService;
import com.poptalk.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import in.lockerapplication.service.LockScreenService;

/**
 * Created by rajat on 25/1/15.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
	private View mView;
	private CircularImageView profileCIV;
	private AlertDialog dialog;
	private ArrayAdapter<String> adapter;
	private String[] items = new String[] { "Take from camera","Select from gallery" };
	public static Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 3;
	private String thumbNailImage = "";
	private String sd_card_path = android.os.Environment.getExternalStorageDirectory() + "/Ciao/Images";
	private ProgressBar progressBar;
	private Switch adLockScreenSwitch;
	private TextView myInternationalPrefixTV;
	private Spinner myNumberSPNR;
	private RequestBean mRequestBean;
	private ProfilePicUploaderService profilePicUploaderService;
	private List<String> ciaoNumberList;
	private Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ciaoNumberList = ApplicationDAO.getInstance(getActivity()).getMyCiaoNumber();
		mActivity = getActivity();
		mView = inflater.inflate(R.layout.fragment_settings, container, false);
		adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_item, items);
		intViews(mView);
		if (ProfilePicUploaderService.getProfilePicUploaderServiceObject() == null) {
			getActivity().startService(	new Intent(getActivity(), ProfilePicUploaderService.class));
			profilePicUploaderService = ProfilePicUploaderService.getProfilePicUploaderServiceObject();
		}
		if(AppSharedPreference.getInstance(getActivity()).getIsProfilePicUploading()){
			progressBar.setVisibility(View.VISIBLE);
		}

		return mView;
	}

	private void intViews(View mView) {
		progressBar = (ProgressBar)mView.findViewById(R.id.progressBar);
		((CallActivity) getActivity()).changeHeader(AppConstants.SETTINGS_FRAGMENT_ID);
		(mView.findViewById(R.id.rl_change_password)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_my_ciao_number)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_my_international_prefix)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_logout)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_tutorials)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_get_ciao_phone_number)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_check_rates)).setOnClickListener(this);
		(mView.findViewById(R.id.rl_help)).setOnClickListener(this);

		profileCIV = (CircularImageView) mView.findViewById(R.id.civ_contact_image);
		profileCIV.setImageResource(R.drawable.user_avtar);
		myNumberSPNR = (Spinner) mView.findViewById(R.id.spnr_my_ciao_number);
		myInternationalPrefixTV = (TextView) mView
				.findViewById(R.id.tv_international_prefix);

		profileCIV.setTag("User profile pic");
		profileCIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createImageOptionDialog();
			}
		});
		myNumberSPNR.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
			   AppSharedPreference.getInstance(getActivity()).setUserCiaoNumber(AppUtils.parseFormattedNumber(ciaoNumberList.get(position)).replace("+", "").trim());

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		adLockScreenSwitch = (Switch) mView.findViewById(R.id.switch_lock_screen);
            final boolean checkUNcheck=AppSharedPreference.getInstance(mActivity).getAdLockScreenVisibility();
            //set the switch to ON
            adLockScreenSwitch.setChecked(checkUNcheck);

            adLockScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        // 0  to disable lockscreen
                        showLockScreenDisbaleDialog();

                    }else{
                        new LockScreenEnableDisableAsyntask(getActivity(), 1).execute();
                    }
                }
            });

	}

	@Override
	public void onResume() {
		super.onResume();
		mRequestBean = new RequestBean();
		mRequestBean.setActivity(getActivity());
		mRequestBean.setLoader(true);

		ArrayAdapter<String> spinnerAdapter =new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ciaoNumberList)
		{
			 public View getView(int position, View convertView, ViewGroup parent) {
			        View v = super.getView(position, convertView, parent);
			        ((TextView) v).setGravity(Gravity.CENTER);
			        return v;
			 }
			 public View getDropDownView(int position, View convertView, ViewGroup parent) {
			        View v = super.getDropDownView(position, convertView,parent);
			        ((TextView) v).setGravity(Gravity.CENTER);
			        return v;
			 }
		};

		myNumberSPNR.setAdapter(spinnerAdapter);
		myNumberSPNR.setSelection(getSelectedNumberPosition());
		setProfilePic();

	}

	private void setProfilePic() {
		String profilePic = AppSharedPreference.getInstance(getActivity()).getProfilePic();
		if (profilePic != null) {
			// http://ciao.appventurez.com/www/upload/images/a8e529314d7c089db23d3b9440e66082.png
			if (profilePic.equalsIgnoreCase("")) {
                profileCIV.setImageResource(R.drawable.user_avtar);
			} else if (profilePic.contains("http://")|| profilePic.contains("https://")) {
				try {
					new ImageLoader(getActivity()).displayProfileImage(profilePic,profileCIV, false);
				} catch (Exception e) {
					 profileCIV.setImageResource(R.drawable.user_avtar);
				}

			} else {
				Bitmap bm = BitmapFactory.decodeFile(profilePic);
				profileCIV.setImageBitmap(bm);
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_change_password:
			startActivity(new Intent(getActivity(),
					ChangePasswordActivity.class));
			break;
		case R.id.rl_my_ciao_number:
			// startActivity(new Intent(getActivity(),
			// ChangeCiaoNumberActivity.class));
			break;
		case R.id.rl_my_international_prefix:
			// startActivity(new Intent(getActivity(),
			// ChangeCiaoNumberActivity.class));
			break;
		case R.id.rl_logout:
			doLogoutFromApp();
			break;
		case R.id.rl_tutorials:
			startActivity(new Intent(getActivity(),TutorialActivity.class));
			break;
		case R.id.rl_get_ciao_phone_number:
			if(AppSharedPreference.getInstance(getActivity()).getUserCiaoNumber()==null) {
				startActivity(new Intent(getActivity(),BuyNumberActivity.class));
			} else {
				startActivity(new Intent(getActivity(), CountryListActivity.class));
			}
			break;
		case R.id.rl_check_rates:
			Intent intent = new Intent(getActivity(), BuyItemsActivity.class);
            intent.putExtra("screen_id", "0");
            startActivity(intent);
			break;
		case R.id.rl_help:
			startActivity(new Intent(getActivity(),HelpActivity.class));
			break;
		}
	}

	/**
	 * Show the logout confirmation dialog
	 */
	private void doLogoutFromApp() {
        if(!AppSharedPreference.getInstance(getActivity()).getIsConnectedToInternet()) {
            DialogUtils.showInternetAlertDialog(getActivity());
            return;
        }

		if (!AppSharedPreference.getInstance(SettingsFragment.this.getActivity()).getIsConnectedToInternet()){
			DialogUtils.showInternetAlertDialog(SettingsFragment.this.getActivity());
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("user_security",	getString(R.string.user_security_key));
			jsonObject.put("user_device_token",AppSharedPreference.getInstance(getActivity()).getDeivceToken());
			jsonObject.put("user_id",AppSharedPreference.getInstance(mRequestBean.getActivity()).getUserID());
			mRequestBean.setJsonObject(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
		bld.setMessage("Are you sure, you want to log out?");
		bld.setCancelable(false);
		bld.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
			getLoaderManager().restartLoader(0, null,new LoaderManager.LoaderCallbacks<String>() {
				@Override
				public Loader<String> onCreateLoader(int id,Bundle args) {

					return new LogoutAsyntaskLoader(getActivity(),mRequestBean);
				}

				@Override
				public void onLoadFinished(Loader<String> loader,
						String data) {
					if (loader instanceof LogoutAsyntaskLoader) {
						((LogoutAsyntaskLoader)loader).hideLoaderDialog();
						try {
							JSONObject serverResponseJsonObject = new JSONObject(
									data);
							String errorCode = serverResponseJsonObject
									.getString("error_code");
							if (errorCode.equalsIgnoreCase("0")) {
								if (XMPPChatService.getConnection() != null) {
									XMPPChatService.getConnection().disconnect();
								}
								getActivity().stopService(new Intent(getActivity(),XMPPChatService.class));
								getActivity().stopService(new Intent(getActivity(),SipService.class));
								getActivity().stopService(new Intent(getActivity(), LockScreenService.class));
								getActivity().stopService(new Intent(getActivity(), GCMIntentService.class));
								AppSharedPreference.getInstance(getActivity()).setAlreadyLoginFlag(false);
								AppSharedPreference.getInstance(getActivity()).setCommUser(null);
								AppSharedPreference.getInstance(getActivity()).setUserID(null);
								Intent intent = new Intent(getActivity(),SplashActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								startActivity(intent);
								getActivity().finish();
							} else {
								AppUtils.showTost(mRequestBean.getActivity(),serverResponseJsonObject.getString("response_string"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void onLoaderReset(Loader<String> loader) {

				}
			});

			}
		});
		bld.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		bld.create().show();
		
	}

	private void createImageOptionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
				// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(),
							"/Ciao/Images/profile_pic.png"));
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
		Uri outputUri = Uri.fromFile(new File(
				AppConstants.APP_IMAGE_BASE_FOLDER, "profile_pic.png"));
		new Crop(source).output(outputUri).asSquare()
		.start(getActivity(), this, Crop.REQUEST_CROP);
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == Activity.RESULT_OK) {
			
			Bitmap bitmap = AppUtils.decodeFile(AppConstants.APP_IMAGE_BASE_FOLDER+ "/profile_pic.png", 100, 100);
			if (bitmap != null) {
				profileCIV.setImageBitmap(bitmap);
				if (profilePicUploaderService == null) {
					getActivity().startService(new Intent(getActivity(),ProfilePicUploaderService.class));
					profilePicUploaderService = ProfilePicUploaderService.getProfilePicUploaderServiceObject();
					if (profilePicUploaderService != null) {
						profilePicUploaderService.uploadImage(profileCIV,
								progressBar, AppConstants.APP_IMAGE_BASE_FOLDER
								+ "/profile_pic.png");
					}

				}

			}
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(getActivity(), Crop.getError(result).getMessage(),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {
			case PICK_FROM_CAMERA:
				if (AppUtils.isSdCardAvailable()) {
					if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						// only for gingerbread and newer versions
						getActivity()
						.sendBroadcast(
								new Intent(
										Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
										Uri.parse("file://"
												+ Environment
												.getExternalStorageDirectory())));
					} else {
						getActivity()
						.sendBroadcast(
								new Intent(
										Intent.ACTION_MEDIA_MOUNTED,
										Uri.parse("file://"
												+ Environment
												.getExternalStorageDirectory())));
					}
				}
				beginCrop(mImageCaptureUri);
				break;
			case PICK_FROM_FILE:
				beginCrop(result.getData());
				break;
			case Crop.REQUEST_CROP:
				handleCrop(resultCode, result);
				break;
			}
		}
	}
	
	private int getSelectedNumberPosition(){
		for(int i=0;i<ciaoNumberList.size();i++){
			String number = AppUtils.parseFormattedNumber(ciaoNumberList.get(i)).replace("+", "").trim();
			String selectedNumber = AppSharedPreference.getInstance(getActivity()).getUserCiaoNumber();
			if(number.equalsIgnoreCase(selectedNumber)){
				return i;
			}
		}
		return 0;
	}
	
	public void showLockScreenDisbaleDialog(){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
		builder1.setMessage("Are you sure you want to turn off sponsored lockscreen and stop earning credits every month?");
		builder1.setCancelable(false);
		builder1.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				new LockScreenEnableDisableAsyntask(getActivity(), 0).execute();
			}
		});
		builder1.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				adLockScreenSwitch.setChecked(true);
			}
		});

		AlertDialog alertDialog = builder1.create();
        alertDialog.show();
	}
	public class LockScreenEnableDisableAsyntask extends AsyncTask<Void, Void, Void>{

		private Context context;
		private int isLockScreenActive;
		private ProgressDialog mDialog;

		public LockScreenEnableDisableAsyntask(Context context,int isLockScreenActive) {
			this.context = context;
			this.isLockScreenActive = isLockScreenActive;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mDialog=new ProgressDialog(context);
			mDialog.setMessage("Please wait ...");
			mDialog.setCancelable(false);
			mDialog.show();
		} 

		@Override
		protected Void doInBackground(Void... params) {

			try {
				JSONObject requestJsonObject = new JSONObject();
				requestJsonObject.put("user_security", context.getString(R.string.user_security_key));
                requestJsonObject.put("user_device_token", AppSharedPreference.getInstance(context).getDeivceToken());
				requestJsonObject.put("user_id", AppSharedPreference.getInstance(context).getUserID());
				requestJsonObject.put("is_lockscreen_active", isLockScreenActive);
				String response= NetworkCall.getInstance(context).hitNetwork(AppNetworkConstants.CHANGE_LOCK_SCREEN_STATUS, requestJsonObject);
				JSONObject serverResponseJsonObject = new JSONObject(response);
				String errorCode = serverResponseJsonObject.getString("error_code");
				Log.d("SettingsFragment", response);
				if (errorCode.equalsIgnoreCase("0")) {
					if(isLockScreenActive == 0){
						//Remove adds	
						AppSharedPreference.getInstance(context).setAdLockScreenVisibility(false);
					}else{
						AppSharedPreference.getInstance(context).setAdLockScreenVisibility(true);	
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}



		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(mDialog != null && mDialog.isShowing())
			{
				mDialog.dismiss();
			}
//			if(isLockScreenActive == 1){
//				adLockScreenSwitch.setChecked(true);
//			}
		}


	}

}