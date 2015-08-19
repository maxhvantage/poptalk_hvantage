package com.ciao.app.adapters;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ciao.app.datamodel.PhoneNumberBean;
import com.ciao.app.utils.AppUtils;
import com.poptalk.app.R;
/**
 * Created by rajat on 15/4/15.
 * This Adapter is used display the multiple ciao number of user in setting section of the app
 */
public class ContactSpinnerAdapter extends ArrayAdapter<PhoneNumberBean>  {
    private Context context;
    private List<PhoneNumberBean> phoneNumberBean;
	public ContactSpinnerAdapter(Context context, int textViewResourceId, List<PhoneNumberBean> phoneNumberBean) {
		super(context,textViewResourceId, phoneNumberBean);
	    this.context = context;
	    this.phoneNumberBean = phoneNumberBean;
	}

	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent) {
	// TODO Auto-generated method stub
	return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	// TODO Auto-generated method stub
	return getCustomView(position, convertView, parent);
	}
	
	public View getCustomView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.getView(position, convertView, parent);

		LayoutInflater inflater=LayoutInflater.from(context);
		View row=inflater.inflate(R.layout.row_contact_view, parent, false);
		TextView contactNumber=(TextView)row.findViewById(R.id.tv_contact_number);
		String phoneNumber = phoneNumberBean.get(position).getPhoneNumber();
		if(phoneNumber!=null && phoneNumber.length()>=10){
			phoneNumber = AppUtils.formatCiaoNumberUsingParentheses(phoneNumber);
			contactNumber.setText(phoneNumber);
		}
		row.setTag(phoneNumberBean.get(position));
		return row;
		}
}
