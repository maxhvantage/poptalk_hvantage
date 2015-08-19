package com.ciao.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.poptalk.app.R;

/*
 * This Adapter is used in interstitial country selection screen
 */
public class AlphabetListAdapter extends BaseAdapter implements Filterable {

    public static abstract class Row {
    }
//some demo test
    public static final class Section extends Row {
        private String sortKey;

        public Section(String sortKey) {
            this.sortKey = sortKey;
        }

        public String getSortKey() {
            return sortKey;
        }

        public void setSortKey(String sortKey) {
            this.sortKey = sortKey;
        }

    }

     public static final class CountryBean extends Row implements Comparable<CountryBean> {

         private String countryName;
         private String countryCode;

        public CountryBean() {
        }

        public CountryBean(String countryName, String countryCode) {
            this.countryName = countryName;
            this.countryCode = countryCode;


        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }


        @Override
        public int compareTo(CountryBean another) {
            return countryName.compareTo(another.countryName);
        }
    }


    private List<Row> rows,searchRows;

    public void setRows(List<Row> rows) {
        this.rows = rows;
        searchRows = rows;

    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Row getItem(int position) {
        return rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        if (getItemViewType(position) == 0) { // Item
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (RelativeLayout) inflater.inflate(R.layout.row_item, parent, false);
                holder = new ViewHolder();
                holder.countryName = (TextView) view.findViewById(R.id.country_name);
                holder.countryCode = (TextView) view.findViewById(R.id.country_code);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }


            CountryBean countryBean = (CountryBean) getItem(position);
            holder.countryName.setText(countryBean.getCountryName());
            holder.countryCode.setText(countryBean.getCountryCode());

        } else { // Section
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.row_section, parent, false);
            }

            Section section = (Section) getItem(position);
            holder = new ViewHolder();
            holder.sortKey = (TextView) view.findViewById(R.id.textView1);
            holder.sortKey.setText(section.getSortKey());

        }

        return view;
    }

    private static class ViewHolder {
        TextView sortKey;
        TextView countryName;
        TextView countryCode;
    }



    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {


        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            rows = (List<Row>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }

        @Override
        public FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<CountryBean> tempCountryList=new ArrayList<CountryBean>();

            if(constraint != null && searchRows!=null) {
                int length=searchRows.size();
                //Log.i("Filtering", "glossaries size"+length);
                int i=0;
                while(i<length){
                    // Real filtering:

                    Row item = searchRows.get(i);
                    if(item instanceof  Section){

                    }else{

                     CountryBean countryBean = (CountryBean)item;
                     if(countryBean.getCountryName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        tempCountryList.add(countryBean);
                    }
                      if(countryBean.getCountryCode().contains(constraint.toString().toLowerCase())){
                            tempCountryList.add(countryBean);
                        }
                    }

                    i++;
                }

                filterResults.values = tempCountryList;
                filterResults.count = tempCountryList.size();
                //Log.i("Filtering", "Filter result count size"+filterResults.count);
            }
            return filterResults;
        }
    };

}
