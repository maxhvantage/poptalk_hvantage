package com.ciao.app.views.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.adapters.CallLogAdapter;
import com.ciao.app.adapters.CallLogsBean;
import com.ciao.app.apppreference.AppSharedPreference;
import com.ciao.app.views.activities.CallActivity;
import com.ciao.app.views.activities.ContactDetailsActivity;
import com.csipsimple.ui.prefs.CallLimitPreference;

/**
 * 
 * @author manoharpeswani
 *
 */
public class CallLogFragment extends Fragment {

	CallLogAdapter aa;

	class GetCallLogs extends AsyncTask<Integer, Void, Boolean> {

		private FragmentActivity activity;
		private Bundle savedInstanceState;

		public GetCallLogs(Bundle bundle, FragmentActivity activity) {
			this.savedInstanceState = bundle;
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			if (savedInstanceState == null) {

				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(activity);
				String limit = preferences.getString("no_of_calllog", "250");
				if (TextUtils.isDigitsOnly(limit)) {
					limit = "LIMIT " + limit;
				} else {
					limit = "";
				}
				ContentResolver contentResolver = this.activity
						.getContentResolver();

				String[] strFields = { android.provider.CallLog.Calls.NUMBER,
						android.provider.CallLog.Calls.TYPE,
						android.provider.CallLog.Calls.CACHED_NAME,
						android.provider.CallLog.Calls.CACHED_NUMBER_TYPE,
						android.provider.CallLog.Calls.DATE,
						android.provider.CallLog.Calls.DURATION

				};
				String where = android.provider.CallLog.Calls.TYPE + " = ";
				String sortOrder = android.provider.CallLog.Calls.DATE
						+ " DESC " + limit;

				where = null;

				Cursor managedCursor = contentResolver.query(
						android.provider.CallLog.Calls.CONTENT_URI, strFields,
						where, null, sortOrder);
				final int number = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
				final int type = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.TYPE);
				final int date = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.DATE);
				final int namei = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
				final int duration = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.DURATION);
				final SimpleDateFormat dateFormat = new SimpleDateFormat(
						"dd MMM yyyy KK:mm aaa", Locale.getDefault());
				final Calendar calendar = Calendar.getInstance();
				String contactName = null;

				long start = 0l;
				while (managedCursor.moveToNext()) {
					long start1 = System.currentTimeMillis();

					CallLogsBean callLogsBean = new CallLogsBean();
					String phoneNo = managedCursor.getString(number);
					contactName = managedCursor.getString(namei);
					if (contactName == null || contactName.equals("")) {
						contactName = "Unknown";
					}

					final String callType = managedCursor.getString(type);
					String callDuration = managedCursor.getString(duration);
					final String callDate = managedCursor.getString(date);
					calendar.setTimeInMillis(Long.parseLong(callDate));

					int dur = Integer.parseInt(callDuration);
					int min = dur / 60;
					int sec = dur % 60;

					if (sec == 0) {
						callDuration = "00m : 00s";
					} else if (min > 0) {
						callDuration = (min > 9 ? min : "0" + min) + "m : "
								+ (sec > 9 ? sec : "0" + sec) + "s";
					} else {
						callDuration = "00m : " + (sec > 9 ? sec : "0" + sec)
								+ "s";
					}

					callLogsBean.setContactName(contactName);
					callLogsBean.setContactNumber(phoneNo);
					callLogsBean.setCallType(Integer.parseInt(callType));
					callLogsBean.setCallTime(dateFormat.format(
							calendar.getTime()).toString());
					callLogsBean.setCallDuration(callDuration);

					if (!contactName.equals("Unknown")) {
						// String iPath = MyUtility.path
						// + callLogsBean.getContactName() + ".jpg";
						// if (new File(iPath).exists()) {
						// callLogsBean.setImagePath(MyUtility.file + iPath);
						//
						// } else {
						// callLogsBean.setImagePath("");
						// }
					} else {
						callLogsBean.setImagePath("");
					}

					callLogBeans.add(callLogsBean);
					start += (System.currentTimeMillis() - start1);

				}
				managedCursor.close();
				//Log.v("time call logs", "==>" + start);

			} else {

				callLogBeans = savedInstanceState
						.getParcelableArrayList("calllogs");
				selection = savedInstanceState.getInt("selection");
			}
            AppSharedPreference.getInstance(activity).setMissedCallCount();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean b) {

			bar.setVisibility(View.GONE);

			if (callLogBeans != null && callLogBeans.size() <= 0) {
				listView.setVisibility(View.GONE);
				not_found.setVisibility(View.VISIBLE);
				// not_found.setText(R.string.no_log_found);

			} else {
				listView.setVisibility(View.VISIBLE);
				not_found.setVisibility(View.GONE);
				aa = new CallLogAdapter(this.activity, callLogBeans);
				// listView.setVisibility(View.VISIBLE);
				listView.setAdapter(aa);
				listView.setSelection(selection);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id) {

						Intent intent = new Intent(getActivity(),ContactDetailsActivity.class);
						intent.putExtra("bean", callLogBeans.get(position));

						getActivity().startActivity(intent);
					}
				});

				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int arg2, long arg3) {

						final String phoneNo = callLogBeans.get(arg2)
								.getContactNumber();
						String addOrRemove = "";
						String callTo = getResources().getString(R.string.call)
								+ phoneNo;
						return true;

					}
				});
			}
              if(activity instanceof CallActivity){
            	  ((CallActivity)activity).updateMissedCallCounter();
              }
		}

		@Override
		protected void onPreExecute() {
			// listView.setEmptyView(new View())
			if (savedInstanceState == null) {
				callLogBeans = new ArrayList<CallLogsBean>();
				// map = new HashMap<String, ArrayList<CallLogsBean>>();
			}

			bar.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}

	}

	static CallLogFragment newInstance(int num) {
		CallLogFragment f = new CallLogFragment();

		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	private ArrayList<CallLogsBean> callLogBeans;
	private ListView listView;
	// private int navigationPos = 0;
	private int selection = 0;
	// private int navigation;
	private GetCallLogs task = null;
	private ProgressBar bar;
	private TextView not_found;

	private void callNumber(int position) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:"
				+ callLogBeans.get(position).getContactNumber()));
		startActivity(callIntent);
	}

	private boolean clearAllCallLogs() {
		if (callLogBeans.size() > 0) {
			ContentResolver cr = getActivity().getContentResolver();
			int i = cr.delete(Calls.CONTENT_URI, null, null);
			if (i > 0) {
				callLogBeans.clear();

			}

			return i > 0;
		}
		return false;

	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (getActivity() != null) {

				String[] strFields = { android.provider.CallLog.Calls.NUMBER,
						android.provider.CallLog.Calls.TYPE,
						android.provider.CallLog.Calls.CACHED_NAME,
						android.provider.CallLog.Calls.CACHED_NUMBER_TYPE,
						android.provider.CallLog.Calls.DATE,
						android.provider.CallLog.Calls.DURATION

				};
				String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

				Cursor managedCursor = context.getContentResolver().query(
						android.provider.CallLog.Calls.CONTENT_URI, strFields,
						null, null, strOrder + " LIMIT 1");
				final int number = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
				final int type = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.TYPE);
				final int date = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.DATE);
				final int namei = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
				final int duration = managedCursor
						.getColumnIndex(android.provider.CallLog.Calls.DURATION);
				final SimpleDateFormat dateFormat = new SimpleDateFormat(
						"dd MMM yyyy KK:mm aaa", Locale.getDefault());
				final Calendar calendar = Calendar.getInstance();

				while (managedCursor.moveToNext()) {
					CallLogsBean callLogsBean = new CallLogsBean();
					String phoneNo = managedCursor.getString(number);
					String contactName = managedCursor.getString(namei);
					if (contactName == null || contactName.equals("")) {
						contactName = "Unknown";
					}

					final String callType = managedCursor.getString(type);
					String callDuration = managedCursor.getString(duration);
					final String callDate = managedCursor.getString(date);
					calendar.setTimeInMillis(Long.parseLong(callDate));

					int dur = Integer.parseInt(callDuration);
					int min = dur / 60;
					int sec = dur % 60;

					if (sec == 0) {
						callDuration = "00m : 00s";
					} else if (min > 0) {
						callDuration = (min > 9 ? min : "0" + min) + "m : "
								+ (sec > 9 ? sec : "0" + sec) + "s";
					} else {
						callDuration = "00m : " + (sec > 9 ? sec : "0" + sec)
								+ "s";
					}

					callLogsBean.setContactName(contactName);
					callLogsBean.setContactNumber(phoneNo);
					callLogsBean.setCallType(Integer.parseInt(callType));
					callLogsBean.setCallTime(dateFormat.format(
							calendar.getTime()).toString());
					callLogsBean.setCallDuration(callDuration);

					if (!contactName.equals("Unknown")) {
						// String iPath = MyUtility.path
						// + callLogsBean.getContactName() + ".jpg";
						// if (new File(iPath).exists()) {
						// callLogsBean.setImagePath(MyUtility.file + iPath);
						//
						// } else {
						// callLogsBean.setImagePath("");
						// }
					} else {
						callLogsBean.setImagePath("");
					}

					callLogBeans.add(0, callLogsBean);

				}

				aa.notifyDataSetChanged();
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// getActivity().registerReceiver(receiver,
		// new IntentFilter(ClsIncommingCallService.CALL_LOG_CHANGE));
		// dataBaseAdapter = ((MainContactsApplication) getActivity()
		// .getApplication()).getDataBaseAdapter();
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_call_log, null);
		listView = (ListView) v.findViewById(R.id.listView);
		not_found = (TextView) v.findViewById(R.id.not_found);

		bar = (ProgressBar) v.findViewById(R.id.progresss);
		task = new GetCallLogs(savedInstanceState, getActivity());
		int navigation = 0;
		if (savedInstanceState != null) {
			navigation = savedInstanceState.getInt("navigation");
		}

		task.execute(navigation);
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		outState.putParcelableArrayList("calllogs", callLogBeans);
		outState.putInt("selection", listView.getFirstVisiblePosition());

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		// getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

}
