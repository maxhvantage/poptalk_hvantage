package in.lockerapplication.fragment;

import in.lockerapplication.asyncTask.ChekinAsyncTask;
import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.networkKeys.NetworkKeys;
import in.lockerapplication.networkcall.CheckConnection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;

public class FragmentCheckin extends Fragment {

	private View view ;
	private int mYear,mMonth,mDay;
	private String d1,d2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) 
	{
		view = inflater.inflate(R.layout.fragment_checkin, container, false);
		//		bool=AppPreferences.INSTANCE.getChekinButtonVisibility();

		Calendar cal = Calendar.getInstance();
		mYear=cal.get(Calendar.YEAR);
		mMonth=cal.get(Calendar.MONTH);
		mDay=cal.get(Calendar.DAY_OF_MONTH);

		d1=String.valueOf(new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" "));
		d2=AppSharedPreference.getInstance(getActivity()).getChekinDate();

		if(d1.equalsIgnoreCase("") || d2.equalsIgnoreCase("")){
			((ImageView)view.findViewById(R.id.iv_checkin)).setTag("grayImage");
			((ImageView)view.findViewById(R.id.iv_checkin)).setBackgroundResource(R.drawable.gray_location);
		}else{
			if(setDateTo(d1,d2)){
				((ImageView)view.findViewById(R.id.iv_checkin)).setTag("grayImage");
				((ImageView)view.findViewById(R.id.iv_checkin)).setBackgroundResource(R.drawable.gray_location);
			}else{
				((ImageView)view.findViewById(R.id.iv_checkin)).setTag("redImage");
				((ImageView)view.findViewById(R.id.iv_checkin)).setBackgroundResource(R.drawable.red_location);
			}
		}

		((Button)view.findViewById(R.id.btn_checkin)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){

				if(((ImageView)view.findViewById(R.id.iv_checkin)).getTag().equals("grayImage")){
					callChekin(getActivity(),FragmentCheckin.this ,AppSharedPreference.getInstance(getActivity()).getUserID());
				}else{
					Toast.makeText(getActivity(), "You have already checked In for the day!", Toast.LENGTH_LONG).show();
				}
				//				if(AppPreferences.INSTANCE.getChekinButtonVisibility()){
				//					
				//					Toast.makeText(getActivity(), "Yess", Toast.LENGTH_LONG).show();
				//					//					Utils.callAddCredit(context, user_id, type, subtype, size, credit_amount);
				//
				//				}else{
				//					
				//					Toast.makeText(getActivity(), "No", Toast.LENGTH_LONG).show();
				//					
				//				}
			}
		});

		return  view ;

	}


	public void callChekin(Context context,Fragment frag, String user_id)
	{
		if(CheckConnection.isConnection(context))
		{
			try 
			{
				JSONObject jsonObject= new JSONObject();
				jsonObject.put(String.valueOf(NetworkKeys.user_security),"abchjds1256a12dasdsad67672");
				jsonObject.put(String.valueOf(NetworkKeys.user_id),user_id);

				new ChekinAsyncTask(context, frag, jsonObject).execute();

			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}else{
			Toast.makeText(context, context.getResources().getString(R.string.nointernetconnction), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Method called on successful**/
	public void onSuccess(CreditResponseBean responseBean){
		((ImageView)view.findViewById(R.id.iv_checkin)).setTag("redImage");
		((ImageView)view.findViewById(R.id.iv_checkin)).setBackgroundResource(R.drawable.red_location);
		AppSharedPreference.getInstance(getActivity()).setChekinDate(d1);
	}

	public void onError(){
		Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
	}

	private Boolean setDateTo(String d1, String d2){
		Boolean isNetxtDay = false;
		try{

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			Date date1 = sdf.parse(d1);
			Date date2 = sdf.parse(d2);

			System.out.println(sdf.format(date1));
			System.out.println(sdf.format(date2));

			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(date1);
			cal2.setTime(date2);

			if(cal1.after(cal2)){
				System.out.println("Date1 is after Date2");
				isNetxtDay=true;
			}

			if(cal1.before(cal2)){
				System.out.println("Date1 is before Date2");
				isNetxtDay=false;
			}

			if(cal1.equals(cal2)){
				System.out.println("Date1 is equal Date2");
				isNetxtDay=false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return isNetxtDay;
	}

	/**
	 * method to get difference of days between current date and user selected date
	 * @param selectedDateTime: your date n time
	 * @param isLocalTimeStamp: defines whether the timestamp d is in local or UTC format
	 * @return days
	 */
	//	public static long getDateDiff(long selectedDateTime, boolean isLocalTimeStamp)
	//	{
	//		long timeOne = Calendar.getInstance().getTime().getTime();
	//		long timeTwo = selectedDateTime;
	//		if(!isLocalTimeStamp) 
	//			timeTwo += getLocalToUtcDelta();
	//		long delta = (timeOne - timeTwo) / ONE_DAY;
	//
	//		if(delta == 0 || delta == 1){
	//			Calendar cal1 = new GregorianCalendar();
	//			cal1.setTimeInMillis(timeOne);
	//			Calendar cal2 = new GregorianCalendar();
	//			cal2.setTimeInMillis(timeTwo);
	//			long dayDiff = cal1.get(Calendar.DAY_OF_MONTH) - cal2.get(Calendar.DAY_OF_MONTH);
	//			return dayDiff;
	//		}
	//
	//		return delta;
	//	}
}