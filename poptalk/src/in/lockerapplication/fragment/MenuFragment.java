package in.lockerapplication.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.apppreference.AppSharedPreference;

/**
 * This class is used to show side menu
 * @author Avinash
 */
public class MenuFragment extends Fragment implements OnClickListener
{
	private Activity mActivity;
	private View view_main;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view_main=inflater.inflate(R.layout.menulist, null);

		TextView tv_daily_credits=(TextView)view_main.findViewById(R.id.tv_daily_credits);
        RelativeLayout welcomeRl = (RelativeLayout)view_main.findViewById(R.id.rl_welcome_layout);
        RelativeLayout earningRl = (RelativeLayout)view_main.findViewById(R.id.rl_earn_credits);
        RelativeLayout todayEarned = (RelativeLayout)view_main.findViewById(R.id.rl_today_earned);
		//		tv_daily_credits.setText();
		if(AppSharedPreference.getInstance(getActivity()).isAppOpened()){
			welcomeRl.setVisibility(View.GONE);
		}
		if(AppSharedPreference.getInstance(getActivity()).getIncrementedCredit().equalsIgnoreCase("0.0")){
			todayEarned.setVisibility(View.GONE);
		}else{
			tv_daily_credits.setText("Today You earned : "+AppSharedPreference.getInstance(getActivity()).getIncrementedCredit());
		}
		return view_main;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		mActivity=getActivity();
	}


	//	//  switching the fragment
	//	private void switchFragment(String tag,int pos)
	//	{
	//		if (getActivity() == null)
	//			return;
	//
	//		if (getActivity() instanceof MainActivity)
	//		{
	//			MyBaseActivity homeActivity = (MyBaseActivity) getActivity();
	//			homeActivity.switchContent(tag,pos);
	//		}
	//	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
		//		case R.id.profile_rl:
		//			setMenuIconsDefaults();
		//			switchFragment("profile", 12);
		//			break;
		}
	}


}
