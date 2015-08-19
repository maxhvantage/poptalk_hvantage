package com.ciao.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.poptalk.app.R;
import com.ciao.app.views.customviews.circularimageview.CircularImageView;

/**
 * Created by rajat on 28/1/15.
 *not in use now
 */
public class FavoriteContactHListAdapter extends BaseAdapter {
    private Context context;
    public FavoriteContactHListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.contact_view_item,null);
            ContactViewHolder contactViewHolder = new ContactViewHolder();
            contactViewHolder.personImage = (CircularImageView)rowView.findViewById(R.id.civ_person_image);
            contactViewHolder.personNameTV = (TextView)rowView.findViewById(R.id.tv_person_name);
            contactViewHolder.ciaoUserIV = (ImageView)rowView.findViewById(R.id.iv_ciao_user);
            rowView.setTag(contactViewHolder);
        }
        return rowView;
    }
   static class ContactViewHolder{
       public CircularImageView personImage;
       public ImageView ciaoUserIV;
       public TextView personNameTV;
   }
}
