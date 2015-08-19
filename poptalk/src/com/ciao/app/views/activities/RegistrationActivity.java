package com.ciao.app.views.activities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.ciao.app.apppreference.AppSharedPreference;
import com.poptalk.app.R;
import com.ciao.app.adapters.GenderSpinnerAdapter;
import com.ciao.app.constants.AppConstants;
import com.ciao.app.datamodel.SignUpBean;
import com.ciao.app.facebook.FacebookCall;
import com.ciao.app.imagecropper.Crop;
import com.ciao.app.imageloader.ImageLoader;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.utils.DialogUtils;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;
import com.facebook.Session;

/**
 * Created by rajat on 23/1/15.
 * 
 */
public class RegistrationActivity extends Activity {
	private EditText firstNameET, emailET, referalCodeET, dobET, passwordET;
	private Spinner genderSP;
	private Calendar myCalendar;
	private DatePickerDialog.OnDateSetListener dateSetListener;
	private SignUpBean signUpBean;
	private ImageLoader imageLoader = null;
	private CircularImageView profilePicIV;
	private AlertDialog dialog;
	private ArrayAdapter<String> adapter;
	private String[] items = new String[]{"Take from camera", "Select from gallery"};
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 3;
	private String thumbNailImage = "";
	private boolean isCorrectDob = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myCalendar = Calendar.getInstance();
		signUpBean = new SignUpBean();
		imageLoader = new ImageLoader(this);
		intDateListener();
		setContentView(R.layout.activity_registration);
		intViews();
		adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);


	}

	private void intDateListener() {
		dateSetListener = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				String myFormat = "yyyy-MM-dd"; //In which you need put here
				SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
				dobET.setText(sdf.format(myCalendar.getTime()));
				if(AppUtils.getUserAgeFromDob(view.getContext(), year, monthOfYear, dayOfMonth)<13){
					isCorrectDob = false;
					AppUtils.showTost(view.getContext(), "Uh oh. Is that your real birthday? Please enter your birthday.");	
				}else{
					isCorrectDob = true;
				}


			}

		};
	}

	private void intViews() {
		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/OpenSans-Regular.ttf");
		profilePicIV = (CircularImageView) findViewById(R.id.civ_person_image);
		profilePicIV.setTag("PROFILE_PIC");
		firstNameET = (EditText) findViewById(R.id.et_first_name);
		emailET = (EditText) findViewById(R.id.et_email);
		referalCodeET = (EditText) findViewById(R.id.et_referal_code);
		genderSP = (Spinner) findViewById(R.id.sp_gender);
		dobET = (EditText) findViewById(R.id.et_dob);
		passwordET = (EditText) findViewById(R.id.et_password);

		firstNameET.setTypeface(type);
		emailET.setTypeface(type);
		referalCodeET.setTypeface(type);
		passwordET.setTypeface(type);
		dobET.setTypeface(type);


		dobET.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});
		profilePicIV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createImageOptionDialog();
			}
		});

		referalCodeET.setOnEditorActionListener(new OnEditorActionListener() {        
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId==EditorInfo.IME_ACTION_DONE){
					goToNextStep();
				}
				return false;
			}
		});
		GenderSpinnerAdapter adapter = new GenderSpinnerAdapter(this,R.layout.view_gender_spinner,Arrays.asList(getResources().getStringArray(R.array.gender_array)));
		genderSP.setAdapter(adapter);
	}

	public void goToNextScreen(View view) {

		goToNextStep();

	}

	//Go to next screen where user can select country and enter mobile number for verification
	private void goToNextStep() {
		signUpBean.setUserFirstName(firstNameET.getText().toString().trim());
		signUpBean.setEmail(emailET.getText().toString().trim());
		signUpBean.setPassword(passwordET.getText().toString().trim());
		signUpBean.setGender(getGender(genderSP.getSelectedItemPosition()));
		signUpBean.setReferalCode(referalCodeET.getText().toString().trim());
		signUpBean.setDob(dobET.getText().toString().trim());
		signUpBean.setProfilePicUrl(thumbNailImage);
		if(!AppSharedPreference.getInstance(this).getIsConnectedToInternet()) {
			DialogUtils.showInternetAlertDialog(this);
			return;
		}
		if(isCorrectDob){
			JSONObject formValidationResponse = AppUtils.isFormDataValid(signUpBean, 0);
			try {
				JSONObject responseJsonObject = formValidationResponse.getJSONObject("response");
				boolean isFormValid = responseJsonObject.getBoolean("isFormValid");
				if (isFormValid) {
					Intent intent = new Intent(this, RegistrationNextActivity.class);
					intent.putExtra("sign_up_bean", signUpBean);
					startActivityForResult(intent, 1001);
				} else {
					if(responseJsonObject.getString("error_message").equalsIgnoreCase("1")){
                     DialogUtils.showCountryCodeDialog(RegistrationActivity.this);
					}else{
						AppUtils.showTost(this, responseJsonObject.getString("error_message"));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}else{
			AppUtils.showTost(this, "Uh oh - you are not validating the birthday.");
		}


	}

	private String getGender(int selectedItemPosition) {
		if (selectedItemPosition == 1) {
			return "male";
		} else if (selectedItemPosition == 2){
			return "female";
		}else{
			return null;
		}

	}


	private void showDatePickerDialog() {
		new DatePickerDialog(RegistrationActivity.this, dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
	}

	public void facebookLogin(View view) {
		FacebookCall.getInstance(this).facebookLogin(this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		switch (requestCode) {
		case PICK_FROM_FILE:
			/* if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT < 19) {
                        mImageCaptureUri = data.getData();
                    } else {
                        mImageCaptureUri = data.getData();
                    }
                    Log.e("mImageCaptureUri = ",""+mImageCaptureUri);
                } else {
                    Log.e("PICK_FROM_FILE","Canceled");
                    //finish();
                }*/
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
		case 1001:
			if(resultCode == RESULT_OK){
				Intent backIntent = new Intent();
				setResult(RESULT_OK, backIntent);
				finish();
			}
			break;
		case 1002:
			if(resultCode == 1){
				if(result!=null){
					String countryCode = result.getStringExtra("_countrycode");
					if(countryCode!=null){
						countryCode = countryCode.replace("+", "");
						countryCode = countryCode.replace(" ", "");
						referalCodeET.setText(countryCode+referalCodeET.getText().toString().trim());	
					}

				}
			}
			break;	
		default:
			if (!AppConstants.FB_LOGIN) {
				Session.getActiveSession().onActivityResult(this, requestCode, resultCode, result);
			}
			break;


		}


	}


	public void autoFillWithFacebook(SignUpBean msignUpBean) {
		signUpBean = msignUpBean;
		try {
			firstNameET.setText(signUpBean.getUserFirstName());
			emailET.setText(signUpBean.getEmail());
//			dobET.setText(formatDateFromFacebook(signUpBean.getDob()));
			genderSP.setSelection(getGenderItemPosition(signUpBean.getGender()), true);
			imageLoader.displayProfileImage(signUpBean.getProfilePicUrl(), profilePicIV, true);
			thumbNailImage = signUpBean.getProfilePicUrl();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}


	}

	private String formatDateFromFacebook(String date) {
		/*mm/dd/yyyy*/
		String converted_date = "";
		converted_date += date.substring(date.lastIndexOf('/') + 1, date.length());
		converted_date += "-" + date.substring(0, date.indexOf('/'));
		converted_date += "-" + date.substring(date.indexOf('/') + 1, date.lastIndexOf('/'));
		return converted_date;

	}

	private int getGenderItemPosition(String gender) {
		if (gender.equalsIgnoreCase("male")) {
			return 0;
		} else {
			return 1;
		}
	}

	
	// show the dialog to choose picture form camera or gallery
	private void createImageOptionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { //pick from camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/Ciao/Images/profile_pic.png"));
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
		Uri outputUri = Uri.fromFile(new File(AppConstants.APP_IMAGE_BASE_FOLDER,"profile_pic.png"));
		new Crop(source).output(outputUri).asSquare().start(this);
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {
			thumbNailImage = AppConstants.APP_IMAGE_BASE_FOLDER+"/profile_pic.png";
			profilePicIV.setImageBitmap(AppUtils.decodeFile(thumbNailImage, 100, 100));
			
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
			thumbNailImage = "";
		}

	}

	public void showCountryList() {
		Intent countryIntent = new Intent(this,CountrySelectionActivity.class);
		startActivityForResult(countryIntent, 1002);

	}
}
