package com.ciao.app.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;
/*
 * This Adapter is used in screen where user can select contacts to create group using Autocompleter.
 */
public class AutocompleteContactAdapter extends ArrayAdapter<CiaoContactBean>{
	private Context context;
	private List<CiaoContactBean> matchedContact;
	private LayoutInflater mInflater;

	public AutocompleteContactAdapter(Context context, int resource,int textViewResourceId, List<CiaoContactBean> matchedContact) {
		super(context, resource, textViewResourceId, matchedContact);
		this.context = context;
		this.matchedContact = matchedContact;
		mInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		return super.getCount();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.contacts_dropdown_item, parent, false);
			holder = new ViewHolder();
			holder.contactName = (TextView)convertView.findViewById(android.R.id.text1);
			holder.contactImage = (CircularImageView)convertView.findViewById(android.R.id.icon);
			convertView.setTag(holder);
		}else{
			holder  = (ViewHolder)convertView.getTag();
		}
		CiaoContactBean ciaoContactBean = matchedContact.get(position);
		holder.contactName.setText(ciaoContactBean.getName());
		holder.contactName.setTag(ciaoContactBean);
		return convertView;
	}

   private static class ViewHolder{
	   TextView contactName ;
	   CircularImageView contactImage;
   }


}
