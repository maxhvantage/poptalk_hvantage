package com.ciao.app.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.datamodel.CiaoContactBean;
import com.ciao.app.datamodel.Contact;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;

/**
 * Created by rajat on 28/2/15.
 * 
 */
public class GroupUserContactsAdapter extends ArrayAdapter<CiaoContactBean> {
    private ArrayList<CiaoContactBean> contactList;
    private Context context;
    public GroupUserContactsAdapter(Context context, int resource, ArrayList<CiaoContactBean> contactList) {
        super(context, resource, contactList);
        this.contactList = contactList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.contacts_dropdown_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(android.R.id.text1);
            viewHolder.image = (CircularImageView) rowView.findViewById(android.R.id.icon);
            viewHolder.deleteUserIV = (ImageView)rowView.findViewById(R.id.iv_remove_contact);
            viewHolder.deleteUserIV.setTag(position);
            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        final CiaoContactBean contact = contactList.get(position);
        holder.text.setText(contact.getName());
        //holder.image.setImageBitmap(contact.image);
        holder.deleteUserIV.setVisibility(View.VISIBLE);
        holder.deleteUserIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(contactList.get(position));
                notifyDataSetChanged();
            }
        });
        return rowView;
    }

    private class ViewHolder {
        public TextView text;
        public CircularImageView image;
        public ImageView deleteUserIV;
    }
}
