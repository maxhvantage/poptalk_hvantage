package com.ciao.app.datamodel;

import android.graphics.Bitmap;

/**
 * Created by rajat on 6/2/15.
 * This call is used to hold the info about user contacts
 */
public class Contact{
    public String phone = null;
    public String image_uri = null;
    public String name = null;
    public Bitmap image;
    public long id;
    public String lookupKey;
    private String sortKey;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact(){

    }

    public Contact(String name,String phone, String image_uri) {
        this.name = name;
        this.phone = phone;
        this.image_uri = image_uri;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }
    
    public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
}