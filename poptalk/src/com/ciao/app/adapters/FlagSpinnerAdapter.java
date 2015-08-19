package com.ciao.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.poptalk.app.R;

/**
 * Created by rajat on 28/1/15.
 * not is use now
 */
public class FlagSpinnerAdapter extends BaseAdapter{
   private Context context;
   private int[] flagArray = {R.drawable.flag_brazil,R.drawable.flag_usa,R.drawable.flag_india};
   private  int layoutID;
    public FlagSpinnerAdapter(Context context,int layoutID) {
        this.context = context;
        this.layoutID = layoutID;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return 3;
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
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View row=inflater.inflate(layoutID, parent, false);
        ImageView flagImage = (ImageView)row.findViewById(R.id.iv_flag);
        flagImage.setImageResource(flagArray[position]);
        return row;
    }
}
