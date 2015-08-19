package com.ciao.app.adapters;

import java.util.ArrayList;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.poptalk.app.R;
import com.ciao.app.utils.AppUtils;
import com.ciao.app.views.customviews.CustomTextView;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;
/*
 * This Adapter is used to display the call log in app
 */
public class CallLogAdapter extends BaseAdapter {

	private static class ViewHolder {
		ImageView callType;

		CircularImageView callerPhoto;
		CustomTextView name, time, duration;// number
	}

	private ArrayList<CallLogsBean> callLogBeans;

	private LayoutInflater mInflater;

	public CallLogAdapter(FragmentActivity activity,
			ArrayList<CallLogsBean> callLogsBeans) {
		if (activity != null) {
			mInflater = LayoutInflater.from(activity);
		}
		this.callLogBeans = callLogsBeans;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return callLogBeans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return callLogBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_call_log, null);
			holder = new ViewHolder();
			holder.callerPhoto = (CircularImageView) convertView
					.findViewById(R.id.civ_contact_image);
			holder.callType = (ImageView) convertView
					.findViewById(R.id.iv_call_type);

			holder.name = (CustomTextView) convertView
					.findViewById(R.id.tv_contact_name);
			// holder.isBlocked = (TextView)
			// convertView.findViewById(R.id.isBlocked);
			// holder.number = (CustomTextView) convertView
			// .findViewById(R.id.contactNumber);
			holder.time = (CustomTextView) convertView
					.findViewById(R.id.tv_call_time);
			holder.duration = (CustomTextView) convertView
					.findViewById(R.id.tv_call_duration);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (callLogBeans.get(position).getCallType()) {
		case android.provider.CallLog.Calls.OUTGOING_TYPE:
			holder.callType
					.setImageResource(R.drawable.ic_call_outgoing_holo_dark);

			break;
		case android.provider.CallLog.Calls.MISSED_TYPE:
			holder.callType
					.setImageResource(R.drawable.ic_call_missed_holo_dark);

			break;
		case android.provider.CallLog.Calls.INCOMING_TYPE:
			holder.callType
					.setImageResource(R.drawable.ic_call_incoming_holo_dark);

			break;

		default:
			break;
		}

		// holder.callerPhoto.setIma
		// loader.displayImage(callLogBeans.get(position).getImagePath(),
		// holder.callerPhoto, options);

		if (callLogBeans.get(position).getContactName().equals("Unknown")) {
			holder.name.setText(AppUtils.formatNumberUsingParenthesesforCallLog(callLogBeans.get(position).getContactNumber(),convertView.getContext()));
			// holder.number.setText("Unknown");
		} else {
			holder.name.setText(callLogBeans.get(position).getContactName());
			// holder.number.setText(callLogBeans.get(position).e());
		}
		holder.time.setText("" + callLogBeans.get(position).getCallTime());

		// holder.name.setTextColor(Color.parseColor("#33b6ea"));

		// holder.time.setTextColor(Color.parseColor("#33b6ea"));
		holder.duration.setText(callLogBeans.get(position).getCallDuration());

		return convertView;

	}

}
