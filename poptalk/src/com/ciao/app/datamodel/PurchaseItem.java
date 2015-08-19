package com.ciao.app.datamodel;

/**
 * Created by rajat on 9/2/15.
 * This method is used to store info about credit plan
 */
public class PurchaseItem {
    String itemName,itemCost;

    public PurchaseItem(String itemName, String itemCost) {
        this.itemName = itemName;
        this.itemCost = itemCost;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCost() {
        return itemCost;
    }

    public void setItemCost(String itemCost) {
        this.itemCost = itemCost;
    }
}
