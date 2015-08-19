package com.ciao.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ciao.app.datamodel.PurchaseItem;
import com.ciao.app.views.activities.BuyItemsActivity;
import com.poptalk.app.R;

import java.util.List;

/**
 * Created by rajat on 9/2/15.
 * 
 * This Adapter is used to show credit plans inside the app
 */
 
public class PurchaseItemAdapter extends ArrayAdapter<PurchaseItem> {
    private List<PurchaseItem> purchaseItems;
    private Context context;
    private Typeface type;
    public PurchaseItemAdapter(Context context, int resource,List<PurchaseItem> purchaseItems) {
        super(context, resource);
        this.purchaseItems = purchaseItems;
        this.context =context;
        type = Typeface.createFromAsset(context.getAssets(),"fonts/OpenSans-Regular.ttf");
    }

    @Override
    public int getCount() {
        return purchaseItems.size();
    }

    @Override
    public PurchaseItem getItem(int position) {
        return purchaseItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.row_buy_items,null);
            PurchaseItemViewHolder purchaseItemViewHolder = new PurchaseItemViewHolder();
            purchaseItemViewHolder.itemName = (TextView)rowView.findViewById(R.id.tv_Item_name);
            purchaseItemViewHolder.itemCost= (TextView)rowView.findViewById(R.id.tv_item_price);
            purchaseItemViewHolder.buyButton = (Button)rowView.findViewById(R.id.btn_buy);
            purchaseItemViewHolder.buyButton.setTypeface(type); 
            rowView.setTag(purchaseItemViewHolder);
        }
        PurchaseItemViewHolder purchaseItemViewHolder = (PurchaseItemViewHolder)rowView.getTag();
        purchaseItemViewHolder.itemName.setText(purchaseItems.get(position).getItemName());
        purchaseItemViewHolder.itemCost.setText(purchaseItems.get(position).getItemCost());
        purchaseItemViewHolder.buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(context instanceof BuyItemsActivity){
                 ((BuyItemsActivity)context).buyItem(position);
             }
            }
        });
        return rowView;
    }

    static class PurchaseItemViewHolder {
        public TextView itemName;
        public TextView itemCost;
        public Button buyButton;
    }
}
