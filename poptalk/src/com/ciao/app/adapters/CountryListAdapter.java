package com.ciao.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.poptalk.app.R;
import com.ciao.app.datamodel.CountryListBean;


/**
 * Created by vinod on 15/4/15.
 * This Adapter is used in the screen where user can country for which he/she wants to purchase new number
 */
public class CountryListAdapter extends BaseAdapter {
    List<CountryListBean> countryList = new ArrayList<CountryListBean>();
    Context context;
    public CountryListAdapter(Context contaxt, List<CountryListBean> countryList)
    {
        this.context=contaxt;
        this.countryList=countryList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return countryList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder = null;

        if(convertView==null)
        {
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.row_buy_number_country_wise,parent,false);
            holder.countryName=(TextView) convertView.findViewById(R.id.tv_country_name);
            holder.countryImage=(ImageView) convertView.findViewById(R.id.iv_country_image);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.countryName.setText(countryList.get(position).getCountryName());
        holder.countryImage.setImageResource(countryList.get(position).getCountryFlag());
        //holder.imageView.setImageURI(Uri.parse(memberList.get(position).getImage()));


        return convertView;
    }
    static  class ViewHolder
    {
        TextView countryName;
        ImageView countryImage;
    }
}
