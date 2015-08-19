package com.ciao.app.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.poptalk.app.R;
import com.ciao.app.datamodel.NumbersListBean;

/**
 * Created by vinod on 16/4/15.
 * This Adapter is used in the screen where user can select number on the basis of selectee country
 */

public class CountryNumbersListAdapter extends BaseAdapter {
    private SparseBooleanArray checked;
    int selectedPosition;
    int selectedIndex = -1;
    List<NumbersListBean> numberList = new ArrayList<NumbersListBean>();
    Context context;
    public CountryNumbersListAdapter(Context contaxt, List<NumbersListBean> numberList)
    {
        this.context=contaxt;
        this.numberList=numberList;

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return numberList.size();
    }
    public void setSelectedIndex(int index){
        selectedIndex = index;
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
            convertView=inflater.inflate(R.layout.row_country_numbers_list,parent,false);
            holder.name=(RadioButton) convertView.findViewById(R.id.rb_choose_number);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.name.setText(numberList.get(position).getNumber());

        if(selectedIndex == position)
        {
            holder.name.setChecked(true);
        }
        else
        {
            holder.name.setChecked(false);
        }
        return convertView;




    }
    static  class ViewHolder
    {
        RadioButton name;

    }
    }

